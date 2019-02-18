package com.cascadebot.cascadebot.commands.moderation;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandMain;
import com.cascadebot.cascadebot.commandmeta.Module;
import com.cascadebot.cascadebot.permissions.CascadePermission;
import com.cascadebot.cascadebot.utils.DiscordUtils;
import net.dv8tion.jda.core.entities.Member;

public class KickCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        Member targetMember = DiscordUtils.getMember(context.getArg(0),context.getGuild());
        context.getGuild().getController().kick(targetMember).queue();
        context.replyInfo("User: " + targetMember.getUser().getAsTag() + " has been kicked");
        }

    @Override
    public String command() {
        return "kick";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Kick Command", "kick", true);
    }


    @Override
    public Module getModule() {
        return Module.MODERATION;
    }
}
