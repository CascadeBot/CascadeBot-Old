package com.cascadebot.cascadebot.commands.moderation;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandMain;
import com.cascadebot.cascadebot.commandmeta.Module;
import com.cascadebot.cascadebot.permissions.CascadePermission;
import com.cascadebot.cascadebot.utils.DiscordUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;

public class SoftBanCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 0) {
            context.replyDanger("Not enough Args (No specified member)");
            return;
        }
        Member targetMember = DiscordUtils.getMember(context.getMessage(0), context.getGuild());
        if (targetMember == null) {
            context.replyDanger("Could not find that user");
        } else {
            try {
                context.getGuild().getController().ban(targetMember.getUser(),7).complete();
                context.getGuild().getController().unban(targetMember.getUser()).complete();
                context.replyInfo("User: " + targetMember.getUser().getAsTag() + " has been kicked");
            } catch (InsufficientPermissionException e) {
                context.replyWarning("Cannot kick user " + targetMember.getUser().getAsTag() +
                        ", missing Kick Members permission");
            } catch (HierarchyException e) {
                context.replyWarning("Cannot kick user " + targetMember.getUser().getAsTag() +
                        ", the top role they have is higher than mine");
            }
        }
    }

    @Override
    public String command() {
        return "softban";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Softban Command", "kick",
                false, Permission.BAN_MEMBERS);
    }


    @Override
    public Module getModule() {
        return Module.MODERATION;
    }

}
