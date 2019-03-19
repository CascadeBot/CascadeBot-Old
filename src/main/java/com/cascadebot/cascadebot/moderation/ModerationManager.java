/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.moderation;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.messaging.MessagingObjects;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import org.apache.commons.lang3.StringUtils;

public class ModerationManager {

    private static final FailureConsumer FAILURE_CONSUMER = ((context, throwable, target, action) -> {
        context.replyException("Could not %s the user %s due to an exception!", throwable, action, target.getAsTag());
    });

    // This is keeping the mod-action parameter as it is used for force-ban, soft-ban and normal ban.
    public void ban(CommandContext context, ModAction action, User target, Member submitter, String reason, int messagesToDelete) {
        if (runChecks(action, target, submitter, context)) {
            runWithCheckedExceptions(() -> {
                context.getGuild().getController()
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
                context.getGuild().getController().unban(target).reason(reason).queue(success -> {
                    sendSuccess(context, target, submitter, ModAction.UNBAN, reason);
                }, throwable -> FAILURE_CONSUMER.accept(context, throwable, target, ModAction.UNBAN));
            }, context, ModAction.UNBAN, target);
        }
    }

    public void softBan(CommandContext context, User target, Member submitter, String reason, int messagesToDelete) {
        if (runChecks(ModAction.SOFT_BAN, target, submitter, context)) {
            ban(context, ModAction.SOFT_BAN, target, submitter, reason, messagesToDelete);
            runWithCheckedExceptions(() -> {
                context.getGuild().getController()
                        .unban(target)
                        .reason("Softban: Unbanned user")
                        .queue(null, throwable -> FAILURE_CONSUMER.accept(context, throwable, target, ModAction.SOFT_BAN));
            }, context, ModAction.SOFT_BAN, target);
        }
    }

    public void kick(CommandContext context, Member target, Member submitter, String reason) {
        if (runChecks(ModAction.KICK, target.getUser(), submitter, context)) {
            runWithCheckedExceptions(() -> {
                context.getGuild().getController()
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
            context.replyWarning("You cannot %s yourself!", action);
            return false;
        } else if (target.isBot()) {
            context.replyWarning("You cannot %s a bot!", action);
            return false;
        }
        return true;
    }

    private void runWithCheckedExceptions(Runnable actionToRun, CommandContext context, ModAction action, User target) {
        try {
            actionToRun.run();
        } catch (InsufficientPermissionException e) {
            context.replyDanger("Cannot %s user %s, missing `%s` permission!", action, target.getAsTag(), e.getPermission().getName());
        } catch (HierarchyException e) {
            if (context.getGuild().getOwner().getUser().getIdLong() == target.getIdLong()) {
                context.replyDanger("Cannot %s user %s as they are the owner of the guild!", action, target.getAsTag());
            } else {
                context.replyDanger("Cannot %s user %s the top role they have is higher than mine!", action, target.getAsTag());
            }
        }
    }

    private void sendSuccess(CommandContext context, User target, Member submitter, ModAction action, String reason) {
        EmbedBuilder builder = MessagingObjects.getStandardMessageEmbed(String.format(
                "%s has been %s!", target.getAsTag(), action.getVerb()
        ), submitter.getUser());

        if (!StringUtils.isBlank(reason)) {
            builder.addField("Reason", reason, false);
        }

        builder.setTitle(StringUtils.capitalize(action.getVerb()) + " user");
        context.replySuccess(builder);
    }

    @FunctionalInterface
    private interface FailureConsumer {

        void accept(CommandContext context, Throwable throwable, User target, ModAction action);

    }


}
