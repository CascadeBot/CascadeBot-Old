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
import org.cascadebot.cascadebot.messaging.MessagingObjects;

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

    private boolean runChecks(ModAction action, User target, Member submitter, CommandContext context) {
        if (!context.getGuild().equals(submitter.getGuild())) {
            // This should never really happen, this is here to make sure it definitely never happens
            return false;
        } else if (target.equals(submitter.getUser())) {
            context.getTypedMessaging().replyWarning(context.i18n("moderation_manager.cannot_action_yourself", action.getName(context.getLocale())));
            return false;
        } else if (target.equals(context.getSelfUser())) {
            context.getTypedMessaging().replyWarning(context.i18n("moderation_manager.cannot_action_bot", action.getName(context.getLocale())));
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
        EmbedBuilder builder = MessagingObjects.getStandardMessageEmbed(context.i18n("moderation_manager.success", target.getAsTag(), action.getVerb(context.getLocale())), submitter.getUser());

        if (!StringUtils.isBlank(reason)) {
            builder.addField(context.i18n("words.reason"), reason, false);
        }

        builder.setTitle(StringUtils.capitalize(action.getVerb(context.getLocale())) + " user");
        context.getTypedMessaging().replySuccess(builder);
    }

    @FunctionalInterface
    private interface FailureConsumer {

        void accept(CommandContext context, Throwable throwable, User target, ModAction action);

    }


}
