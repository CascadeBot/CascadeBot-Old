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

    public void ban(CommandContext context, ModAction action, User target, Member submitter, String reason, int messagesToDelete) {
        if (runChecks(action, target, submitter, context)) {
            runWithCheckedExceptions(() -> {
                context.getGuild().getController()
                        .ban(target, messagesToDelete, reason)
                        .queue(success -> {
                            EmbedBuilder builder = MessagingObjects.getStandardMessageEmbed(String.format(
                                    "%s has been %s!", target.getAsTag(), action.getVerb()
                            ), submitter.getUser());

                            if (!StringUtils.isBlank(reason)) {
                                builder.addField("Reason", reason, false);
                            }

                            builder.setTitle(StringUtils.capitalize(action.getVerb()) + " user");
                            context.replySuccess(builder);
                        }, throwable -> FAILURE_CONSUMER.accept(context, throwable, target, action));
            }, context, action, target);
        }
    }

    public void unban(CommandContext context, User target, Member submitter, String reason) {
        if (runChecks(ModAction.UNBAN, target, submitter, context)) {
            runWithCheckedExceptions(() -> {
                context.getGuild().getController().unban(target).reason(reason).queue(success -> {
                    context.replySuccess("User %s has been unbanned!", target.getAsTag());
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
                            context.replySuccess("Use %s has been kicked!", target.getUser().getAsTag());
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
            context.replyDanger("Cannot %s user %s the top role they have is higher than mine!", action, target.getAsTag());
        }
    }

    @FunctionalInterface
    private interface FailureConsumer {

        void accept(CommandContext context, Throwable throwable, User target, ModAction action);

    }


}
