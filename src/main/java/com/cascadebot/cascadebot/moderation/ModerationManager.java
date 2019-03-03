/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.moderation;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.messaging.MessagingObjects;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import org.apache.commons.lang3.StringUtils;

import java.util.function.BiConsumer;

public class ModerationManager {

    public static final BiConsumer<Throwable, ModerationAction> FAILURE_CONSUMER = ((throwable, moderationAction) -> {
        moderationAction.context.replyException("Could not %s the user %s!", throwable, moderationAction.action, moderationAction.target.getAsTag());
    });

    public void handleModeration(CommandContext context, ModAction action, User target, Member submitter) {
        handleModeration(context, action, target, submitter, null);
    }

    public void handleModeration(CommandContext context, ModAction action, User target, Member submitter, String reason) {
        Guild guild = context.getGuild();
        if (!guild.equals(submitter.getGuild())) {
            // This should never really happen, this is here to make sure it definitely never happens
            return;
        }

        Member targetMember = guild.getMember(target);

        if (action.needsMember() && targetMember == null) {
            context.replyWarning("The user %s is not in the guild to be %s!%s",
                    target.getAsTag(),
                    action.getVerb(),
                    action == ModAction.BAN ? "\nUse `;forceban` to forcibly ban a user!" : "");
            return;
        }

        if (target.equals(submitter.getUser())) {
            context.replyWarning("You cannot %s yourself!", action);
            return;
        }

        if (target.isBot()) {
            context.replyWarning("You cannot %s a bot!", action);
            return;
        }

        ModerationAction moderationAction = new ModerationAction(context, action, target, submitter, reason);

        try {
            switch (action) {
                case BAN:
                    ban(moderationAction);
                    break;
                case UNBAN:
                    unban(moderationAction);
                    break;
                case SOFT_BAN:
                    softBan(moderationAction);
                    break;
                case KICK:
                    kick(moderationAction);
                    break;
                case MUTE:
                    // Unimplemented
                    break;
                case WARN:
                    // Unimplemented
                    break;
            }
        } catch (InsufficientPermissionException e) {
            context.replyDanger("Cannot %s user %s, missing `%s` permission!", action, target.getAsTag(), e.getPermission().getName());
        } catch (HierarchyException e) {
            context.replyDanger("Cannot %s user %s the top role they have is higher than mine!", action, target.getAsTag());
        }
    }

    private void ban(ModerationAction modAction) {
        modAction.context.getGuild().getController()
                .ban(modAction.target, 7, modAction.reason)
                .queue(success -> {
            EmbedBuilder builder = MessagingObjects.getStandardMessageEmbed(String.format(
                    "%s has been %s!", modAction.target.getAsTag(), modAction.action.getVerb()
            ), modAction.submitter.getUser());

            if (!StringUtils.isBlank(modAction.reason)) {
                builder.addField("Reason", modAction.reason, false);
            }

            builder.setTitle(StringUtils.capitalize(modAction.action.getVerb()) + " user");
            modAction.context.replySuccess(builder);
        }, (throwable -> FAILURE_CONSUMER.accept(throwable, modAction)));
    }

    private void unban(ModerationAction modAction) {
        modAction.context.getGuild().getController().unban(modAction.target).queue(success -> {
            modAction.context.replySuccess("User %s has been unbanned!", modAction.target.getAsTag());
        }, throwable -> FAILURE_CONSUMER.accept(throwable, modAction));
    }

    private void softBan(ModerationAction modAction) {
        ban(modAction);
        modAction.context.getGuild().getController()
                .unban(modAction.target)
                .reason("Softban: Unbanned user")
                .queue(null, throwable -> FAILURE_CONSUMER.accept(throwable, modAction));
    }

    private void kick(ModerationAction moderationAction) {

    }


    private class ModerationAction {

        private CommandContext context;
        private ModAction action;
        private User target;
        private Member submitter;
        private String reason;

        public ModerationAction(CommandContext context, ModAction action, User target, Member submitter, String reason) {
            this.context = context;
            this.action = action;
            this.target = target;
            this.submitter = submitter;
            this.reason = reason;
        }

    }


}
