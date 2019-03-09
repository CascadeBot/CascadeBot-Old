package com.cascadebot.cascadebot.commands.moderation;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.commandmeta.Argument;
import com.cascadebot.cascadebot.commandmeta.ArgumentType;
import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandMain;
import com.cascadebot.cascadebot.commandmeta.Module;
import com.cascadebot.cascadebot.messaging.MessagingObjects;
import com.cascadebot.cascadebot.permissions.CascadePermission;
import com.cascadebot.cascadebot.utils.DiscordUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;

import java.util.Set;

public class KickCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 0) {
            context.replyUsage(this);
            return;
        }

        Member targetMember = DiscordUtils.getMember(context.getGuild(), context.getArg(0));

        if (targetMember == null) {
            context.replyDanger(MessagingObjects.getStandardMessageEmbed("Could not find that user!", context.getUser()));
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
    public CascadePermission getPermission() {
        return CascadePermission.of("Kick Command", "kick",
                true, Permission.KICK_MEMBERS);
    }

    @Override
    public String description() {
        return "Kick a user";
    }

    @Override
    public Set<Argument> getUndefinedArguments() {
        return Set.of(Argument.of(
                "member", "", ArgumentType.REQUIRED, Set.of(
                        Argument.of("reason", "Kicks a member", ArgumentType.OPTIONAL)
                )
        ));
    }

    @Override
    public Module getModule() {
        return Module.MODERATION;
    }

}
