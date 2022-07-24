/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.moderation;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.MainCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.utils.DiscordUtils;

public class KickCommand extends MainCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 0) {
            context.getUiMessaging().replyUsage();
            return;
        }

        Member targetMember = DiscordUtils.getMember(context.getGuild(), context.getArg(0));

        if (targetMember == null) {
            context.getTypedMessaging().replyDanger(MessagingObjects.getStandardMessageEmbed(context.i18n("responses.cannot_find_user"), context.getUser(), context.getLocale()));
            return;
        }

        String reason = null;
        if (context.getArgs().length > 1) {
            reason = context.getMessage(1);
        }

        CascadeBot.INS.getModerationManager().kick(
                context,
                targetMember,
                sender,
                reason
        );
    }

    @Override
    public String command() {
        return "kick";
    }

    @Override
    public Module module() {
        return Module.MODERATION;
    }

}
