package com.cascadebot.cascadebot.commands.developer;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import com.cascadebot.cascadebot.data.mapping.GuildDataMapper;
import com.cascadebot.cascadebot.permissions.CascadePermission;
import net.dv8tion.jda.core.entities.Member;

public class GuildSaveSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        GuildDataMapper.getGuilds().invalidate(context.getGuild().getIdLong());
        context.replySuccess("Saved all guild information successfully!");
    }

    @Override
    public String command() {
        return "save";
    }

    @Override
    public CascadePermission getPermission() {
        return null;
    }

}
