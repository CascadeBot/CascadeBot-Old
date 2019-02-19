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
        } else {
            try {
                context.getGuild().getController().kick(targetMember).queue(aVoid -> {
                    context.replyInfo("%s has been kicked!", targetMember.getUser().getAsTag());
                }, throwable -> {
                    context.replyException("Could not kick the user %s!", throwable, targetMember.getUser().getAsTag());
                });
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
        return "kick";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Kick Command", "kick",
                false, Permission.KICK_MEMBERS);
    }

    @Override
    public Module getModule() {
        return Module.MODERATION;
    }

}
