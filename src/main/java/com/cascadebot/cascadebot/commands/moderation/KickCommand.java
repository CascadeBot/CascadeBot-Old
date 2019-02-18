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
        Member TargetMember = DiscordUtils.getMember(context.getArg(0),context.getGuild());
        context.getGuild().getController().kick(TargetMember).queue();
        context.reply("User: " + TargetMember.getUser().getAsTag() + " has been kicked");
        }

    @Override
    public String command() {
        return "kick";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Kick Command", "Kick", true);
    }


    @Override
    public Module getModule() {
        return Module.MODERATION;
    }
}
