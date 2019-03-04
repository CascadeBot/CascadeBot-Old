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

public class SoftBanCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 0) {
            context.replyDanger("Not enough arguments (No specified member)");
            return;
        }

        Member targetMember = DiscordUtils.getMember(context.getArg(0), context.getGuild());
        if (targetMember == null) {
            context.replyDanger("Could not find that user!");
            return;
        }

        String reason = null;

        if (context.getArgs().length >= 2) {
            reason = context.getMessage(1);
        }

        CascadeBot.INS.getModerationManager().softBan(
                context,
                ModAction.SOFT_BAN,
                targetMember.getUser(),
                sender,
                reason,
                7 // TODO
        );

    }

    @Override
    public String command() {
        return "softban";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Soft-ban Command", "softban",
                false, Permission.BAN_MEMBERS);
    }

    @Override
    public String description() {
        return "Ban a user";
    }


    @Override
    public Module getModule() {
        return Module.MODERATION;
    }

}
