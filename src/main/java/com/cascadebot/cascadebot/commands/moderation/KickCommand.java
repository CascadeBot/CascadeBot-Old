package com.cascadebot.cascadebot.commands.moderation;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandMain;
import com.cascadebot.cascadebot.commandmeta.Module;
import com.cascadebot.cascadebot.moderation.ModAction;
import com.cascadebot.cascadebot.permissions.CascadePermission;
import com.cascadebot.cascadebot.utils.DiscordUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;

public class KickCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 0) {
            context.replyDanger("Not enough arguments (No specified member)");
            return;
        }
        Member targetMember = DiscordUtils.getMember(context.getMessage(0), context.getGuild());

        if (targetMember == null) {
            context.replyDanger("Could not find that user");
        }

        CascadeBot.INS.getModerationManager().kick(
                context,
                targetMember,
                sender,
                "" // TODO add this
        );
    }

    @Override
    public String command() {
        return "kick";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Kick Command", "kick",
                false, Permission.KICK_MEMBERS);
    }

    @Override
    public String description() {
        return "Kick a user";
    }

    @Override
    public Module getModule() {
        return Module.MODERATION;
    }

}
