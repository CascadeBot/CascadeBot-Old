/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.moderation;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import org.apache.commons.lang3.StringUtils;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.data.entities.ActionType;
import org.cascadebot.cascadebot.data.entities.GuildSettingsModerationEntity;
import org.cascadebot.cascadebot.data.entities.ScheduledActionEntity;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.utils.ExtensionsKt;
import org.cascadebot.cascadebot.utils.FormatUtils;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

public class ModerationManager {

    private static final FailureConsumer FAILURE_CONSUMER = ((context, throwable, target, action) -> {
        context.getTypedMessaging().replyException(context.i18n("moderation_manager.failed_action", action.getName(context.getLocale()), target.getAsTag()), throwable);
    });

    // This is keeping the mod-action parameter as it is used for force-ban, soft-ban and normal ban.
    public void ban(CommandContext context, ModAction action, User target, Member submitter, String reason, int messagesToDelete) {
        if (runChecks(action, target, submitter, context)) {
            runWithCheckedExceptions(() -> {
                context.getGuild()
                        .ban(target, messagesToDelete, reason)
                        .queue(success -> {
                            sendSuccess(context, target, submitter, action, reason);
                        }, throwable -> FAILURE_CONSUMER.accept(context, throwable, target, action));
            }, context, action, target);
        }
    }

    public void unban(CommandContext context, User target, Member submitter, String reason) {
        if (runChecks(ModAction.UNBAN, target, submitter, context)) {
            runWithCheckedExceptions(() -> {
                context.getGuild().unban(target).reason(reason).queue(success -> {
                    sendSuccess(context, target, submitter, ModAction.UNBAN, reason);
                }, throwable -> FAILURE_CONSUMER.accept(context, throwable, target, ModAction.UNBAN));
            }, context, ModAction.UNBAN, target);
        }
    }

    public void tempBan(CommandContext context, User target, Member submitter, String reason, long delay) {
        if (runChecks(ModAction.TEMP_BAN, target, submitter, context)) {
            runWithCheckedExceptions(() -> {
                context.getGuild().ban(target, 7).reason(reason).queue(success -> {
                    /*ScheduledActionManager.registerScheduledAction(new ScheduledActionEntity(
                            ActionType.UNBAN,
                            new ScheduledActionEntity.ModerationActionData(target.getIdLong()),
                            context.getGuild().getIdLong(),
                            context.getChannel().getIdLong(),
                            submitter.getIdLong(),
                            Instant.now(),
                            delay
                    ));*/
                    sendTempSuccess(context, target, submitter, ModAction.TEMP_BAN, reason, delay);
                });
            }, context, ModAction.TEMP_BAN, target);
        }
    }

    public void softBan(CommandContext context, User target, Member submitter, String reason, int messagesToDelete) {
        if (runChecks(ModAction.SOFT_BAN, target, submitter, context)) {
            ban(context, ModAction.SOFT_BAN, target, submitter, reason, messagesToDelete);
            runWithCheckedExceptions(() -> {
                context.getGuild()
                        .unban(target)
                        .reason("Softban: Unbanned user")
                        .queue(null, throwable -> FAILURE_CONSUMER.accept(context, throwable, target, ModAction.SOFT_BAN));
            }, context, ModAction.SOFT_BAN, target);
        }
    }

    public void kick(CommandContext context, Member target, Member submitter, String reason) {
        if (runChecks(ModAction.KICK, target.getUser(), submitter, context)) {
            runWithCheckedExceptions(() -> {
                context.getGuild()
                        .kick(target, reason)
                        .queue(success -> {
                            sendSuccess(context, target.getUser(), submitter, ModAction.KICK, reason);
                        }, throwable -> FAILURE_CONSUMER.accept(context, throwable, target.getUser(), ModAction.KICK));
            }, context, ModAction.KICK, target.getUser());
        }
    }

    public void mute(CommandContext context, Member target, Member submitter, String reason) {
        if (runChecks(ModAction.MUTE, target.getUser(), submitter, context)) {
            runWithCheckedExceptions(() -> {
                context.getGuild()
                        .addRoleToMember(target, ExtensionsKt.getMutedRole(context.getGuild()))
                        .reason(context.i18n("mod_actions.mute.reason",
                                target.getUser().getAsTag(),
                                submitter.getUser().getAsTag(),
                                reason))
                        .queue(aVoid -> {
                            sendSuccess(context, target.getUser(), submitter, ModAction.MUTE, reason);
                        });
            }, context, ModAction.MUTE, target.getUser());
        }
    }

    public void tempMute(CommandContext context, Member target, Member submitter, String reason, long delay) {
        if (runChecks(ModAction.TEMP_MUTE, target.getUser(), submitter, context)) {
            runWithCheckedExceptions(() -> {
                context.getGuild()
                        .addRoleToMember(target, ExtensionsKt.getMutedRole(context.getGuild()))
                        .reason(context.i18n("mod_actions.mute.reason",
                                target.getUser().getAsTag(),
                                submitter.getUser().getAsTag(),
                                reason,
                                FormatUtils.formatDateTime(OffsetDateTime.now().plus(delay, ChronoUnit.MILLIS), context.getLocale())))
                        .queue(aVoid -> {
                            /*ScheduledActionManager.registerScheduledAction(new ScheduledActionEntity(
                                    ActionType.UNMUTE,
                                    new ScheduledActionEntity.ModerationActionData(target.getIdLong()),
                                    context.getGuild().getIdLong(),
                                    context.getChannel().getIdLong(),
                                    submitter.getIdLong(),
                                    Instant.now(),
                                    delay
                            ));*/
                            sendTempSuccess(context, target.getUser(), submitter, ModAction.TEMP_MUTE, reason, delay);
                        });
            }, context, ModAction.TEMP_MUTE, target.getUser());
        }
    }

    private boolean runChecks(ModAction action, User target, Member submitter, CommandContext context) {
        GuildSettingsModerationEntity moderationSettings = context.getDataObject(GuildSettingsModerationEntity.class);
        if (moderationSettings == null) {
            throw new UnsupportedOperationException("This shouldn't happen");
        }
        Member memberTarget = context.getGuild().getMember(target);
        if (!context.getGuild().equals(submitter.getGuild())) {
            // This should never really happen, this is here to make sure it definitely never happens
            return false;
        } else if (target.equals(submitter.getUser())) {
            context.getTypedMessaging().replyWarning(context.i18n("moderation_manager.cannot_action_yourself", action.getName(context.getLocale())));
            return false;
        } else if (target.equals(context.getSelfUser())) {
            context.getTypedMessaging().replyWarning(context.i18n("moderation_manager.cannot_action_bot", action.getName(context.getLocale())));
            return false;
        } else if (moderationSettings.getRespectHierarchy() && memberTarget != null && !submitter.canInteract(memberTarget)) {
            context.getTypedMessaging().replyWarning(context.i18n("moderation_manager.user_cannot_action_superior", action.getName(context.getLocale()), target.getName()));
            return false;
        }
        return true;
    }

    private void runWithCheckedExceptions(Runnable actionToRun, CommandContext context, ModAction action, User target) {
        try {
            actionToRun.run();
        } catch (InsufficientPermissionException e) {
            context.getTypedMessaging().replyDanger(context.i18n("moderation_manager.missing_permission", action.getName(context.getLocale()), target.getAsTag(), e.getPermission().getName()));
        } catch (HierarchyException e) {
            if (context.getGuild().getOwner().getUser().equals(target)) {
                context.getTypedMessaging().replyDanger(context.i18n("moderation_manager.cannot_action_owner", action.getName(context.getLocale()), target.getAsTag()));
            } else {
                context.getTypedMessaging().replyDanger(context.i18n("moderation_manager.cannot_action_superior", action.getName(context.getLocale()), target.getAsTag()));
            }
        }
    }

    private void sendSuccess(CommandContext context, User target, Member submitter, ModAction action, String reason) {
        EmbedBuilder builder = MessagingObjects.getStandardMessageEmbed(context.i18n("moderation_manager.success", target.getAsTag(), action.getVerb(context.getLocale())), submitter.getUser(), context.getLocale());

        if (!StringUtils.isBlank(reason)) {
            builder.addField(context.i18n("words.reason"), reason, false);
        }

        builder.setTitle(StringUtils.capitalize(action.getVerb(context.getLocale())) + " user");
        context.getTypedMessaging().replySuccess(builder);
    }

    private void sendTempSuccess(CommandContext context, User target, Member submitter, ModAction action, String reason, long delay) {
        EmbedBuilder builder = MessagingObjects.getStandardMessageEmbed(context.i18n("moderation_manager.success", target.getAsTag(), action.getVerb(context.getLocale())), submitter.getUser(), context.getLocale());

        if (!StringUtils.isBlank(reason)) {
            builder.addField(context.i18n("words.reason"), reason, false);
        }

        // TODO, Use kotlin string extensions when PR #232 is merged.
        builder.addField(
                context.i18n("words.duration"),
                FormatUtils.formatTime(delay, context.getLocale(), true)
                        + " (" + context.i18n("words.until") + " " + FormatUtils.formatDateTime(OffsetDateTime.now().plus(delay, ChronoUnit.MILLIS), context.getLocale()) + ")",
                true
        );

        builder.setTitle(StringUtils.capitalize(action.getVerb(context.getLocale())) + " user");
        context.getTypedMessaging().replySuccess(builder);
    }

    @FunctionalInterface
    private interface FailureConsumer {

        void accept(CommandContext context, Throwable throwable, User target, ModAction action);

    }


}
