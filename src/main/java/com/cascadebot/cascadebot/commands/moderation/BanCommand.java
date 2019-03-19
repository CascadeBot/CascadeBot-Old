/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.moderation;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.commandmeta.Argument;
import com.cascadebot.cascadebot.commandmeta.ArgumentType;
import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandMain;
import com.cascadebot.cascadebot.commandmeta.Module;
import com.cascadebot.cascadebot.messaging.MessagingObjects;
import com.cascadebot.cascadebot.moderation.ModAction;
import com.cascadebot.cascadebot.permissions.CascadePermission;
import com.cascadebot.cascadebot.utils.ConfirmUtils;
import com.cascadebot.cascadebot.utils.DiscordUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class BanCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 0) {
            context.replyUsage(this);
            return;
        }

        Member targetMember = DiscordUtils.getMember(context.getGuild(), context.getArg(0));
        String reason = null;

        if (targetMember == null) {
            context.replyDanger(MessagingObjects.getStandardMessageEmbed("We couldn't find that user in this guild!\n" +
                    "If you would like to forceban them, please react to this message!", sender.getUser()));
            if (!ConfirmUtils.hasConfirmedAction("forceban_user", sender.getUser().getIdLong())) {
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (ConfirmUtils.hasConfirmedAction("forceban_user", sender.getUser().getIdLong())) {
                    // forceban
                    context.replyDanger("FORCEBAN");
                } else {
                    context.replyDanger("Stopping command, user doesn't exist and you don't want us to forceban.");
                    return;
                }
            }

            if (context.getArgs().length >= 2) {
                reason = context.getMessage(1);
            }

            if (targetMember == null) {
                return;
            }

            CascadeBot.INS.getModerationManager().ban(
                    context,
                    ModAction.BAN,
                    targetMember.getUser(),
                    sender,
                    reason,
                    7 // TODO: add this as an arg
            );
        }




    }

    @Override
    public Module getModule() {
        return Module.MODERATION;
    }

    @Override
    public String command() {
        return "ban";
    }

    @Override
    public String description() {
        return "Bans people I guess ;)";
    }

    @Override
    public Set<Argument> getUndefinedArguments() {
        return Set.of(Argument.of(
                "member", "", ArgumentType.REQUIRED, Set.of(
                        Argument.of("reason", "Bans a member", ArgumentType.OPTIONAL)
                )
        ));
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Ban command", "ban", false, Permission.BAN_MEMBERS);
    }

}
