package org.cascadebot.cascadebot.commands.music;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class SeekCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        long time = Long.parseLong(context.getArg(0));
        // String regex = "(\\d+)(?: ?hours|h)|(\\d+)(?: ?minutes|m)|(\\d+)(?: ?seconds|s)";

        if (time < 0) {
            context.getTypedMessaging().replyDanger("You cannot seek to a negative value!");
            return;
        }

        context.getMusicPlayer().getPlayer().seekTo(time);
    }

    @Override
    public Module getModule() {
        return Module.MUSIC;
    }

    @Override
    public String command() {
        return "seek";
    }

    @Override
    public CascadePermission getPermission() {
        return null;
    }

}
