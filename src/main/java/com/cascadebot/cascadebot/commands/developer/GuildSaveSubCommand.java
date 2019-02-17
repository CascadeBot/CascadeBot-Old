package com.cascadebot.cascadebot.commands.developer;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import com.cascadebot.cascadebot.data.mapping.GuildDataMapper;
import com.cascadebot.cascadebot.permissions.CascadePermission;
import net.dv8tion.jda.core.entities.Member;

public class GuildSaveSubCommand implements ICommandExecutable {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArg(0).length() == 0) {
            GuildDataMapper.getGuilds().invalidate(context.getGuild().getIdLong());
            context.replySuccess("Saved **this guild's** information successfully!");
        } else if (context.getArg(0).equals("all")) {
            GuildDataMapper.getGuilds().invalidateAll();
            context.replySuccess("Saved **all** guild information successfully!");
        } else {
            GuildDataMapper.getGuilds().invalidate(context.getArg(0));
            context.replySuccess("Saved guild information for guild **" + context.getArg(0) + "**!");
        }
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
