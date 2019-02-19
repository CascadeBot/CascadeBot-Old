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

import java.util.function.Consumer;

public class SoftBanCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 0) {
            context.replyDanger("Not enough arguments (No specified member)");
            return;
        }
        Member targetMember = DiscordUtils.getMember(context.getMessage(0), context.getGuild());
        if (targetMember.getUser() == sender.getUser()) {
            context.replyWarning("Why would you want to soft ban yourself~");
            return;
        }
        if (targetMember.getUser() == context.getSelfMember()) {
            context.replyWarning(("My programming forbids me to soft ban myself~"));
            return;
        }
        if (targetMember == null) {
            context.replyDanger("Could not find that user");
        } else {
            try {
                // Failure consumer to be used on both the ban and the unban
                Consumer<Throwable> failure = throwable -> context.replyException("Could not softban the user %s!", throwable, targetMember.getUser().getAsTag());
                context.getGuild().getController().ban(targetMember.getUser(), 7).queue(aVoid -> {
                    // This is considered successful if the user is banned. If the user is unable to be unbanned an exception will be thrown
                    context.replyInfo("%s has been softbanned!", targetMember.getUser().getAsTag());
                    context.getGuild().getController().unban(targetMember.getUser()).queue(null, failure);
                }, failure);
            } catch (InsufficientPermissionException e) {
                context.replyWarning("Cannot soft ban user " + targetMember.getUser().getAsTag() +
                        ", missing Ban Members permission");
            } catch (HierarchyException e) {
                context.replyWarning("Cannot soft ban user " + targetMember.getUser().getAsTag() +
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
        return CascadePermission.of("Softban Command", "ban",
                false, Permission.BAN_MEMBERS);
    }


    @Override
    public Module getModule() {
        return Module.MODERATION;
    }

}
