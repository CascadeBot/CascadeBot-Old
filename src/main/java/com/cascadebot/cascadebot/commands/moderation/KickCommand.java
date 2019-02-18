package com.cascadebot.cascadebot.commands.moderation;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandMain;
import com.cascadebot.cascadebot.commandmeta.Module;
import com.cascadebot.cascadebot.permissions.CascadePermission;
import com.cascadebot.cascadebot.utils.DiscordUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;

import java.util.function.Consumer;

public class KickCommand implements ICommandMain {

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
                context.getGuild().getController().kick(targetMember).queue();
                context.replyInfo("User: " + targetMember.getUser().getAsTag() + " has been kicked");
            } catch (InsufficientPermissionException e) {
                context.replyWarning("Cannot kick user " + targetMember.getUser().getAsTag() + " due to lack of permissions");
            } catch (HierarchyException e) {
                context.replyWarning("Cannot kick user " + targetMember.getUser().getAsTag() + " due to them being higher then me");
            }
        }
    }

    @Override
    public String command() {
        return "kick";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Kick Command", "kick", false);
    }


    @Override
    public Module getModule() {
        return Module.MODERATION;
    }
}
