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
import org.apache.commons.lang.ObjectUtils;

public class BanCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {

        if (context.getArgs().length == 0) {
            context.replyDanger("Not enough arguments!");
            return;
        }

        Member targetMember = DiscordUtils.getMember(context.getMessage(0), context.getGuild());

        if (targetMember == null) {
            context.replyDanger("We couldn't find that member!");
            return;
        }

        try {
            context.getGuild().getController().ban(targetMember.getUser(), 7).queue(success -> {
                context.replyInfo("**%s** has been banned!", targetMember.getUser().getAsTag());
            }, throwable -> {
                context.replyException("Could not ban the user %s!", throwable, targetMember.getUser().getAsTag());
            });

        }  catch (InsufficientPermissionException e) {
            context.replyWarning("Cannot ban user " + targetMember.getUser().getAsTag() +
                    ", missing Ban Members permission");
        } catch (HierarchyException e) {
            context.replyWarning("Cannot ban user " + targetMember.getUser().getAsTag() +
                    ", the top role they have is higher than mine");
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
    public CascadePermission getPermission() {
        return CascadePermission.of("Ban command", "ban", false, Permission.BAN_MEMBERS);
    }

}
