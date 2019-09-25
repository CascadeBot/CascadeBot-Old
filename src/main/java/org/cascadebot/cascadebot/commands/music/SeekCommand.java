package org.cascadebot.cascadebot.commands.music;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.shared.Regex;

import java.util.Arrays;
import java.util.regex.Pattern;

public class SeekCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        String time = context.getArg(0);

        Pattern pattern = Pattern.compile("(\\d+)(?: ?hours|h)|(\\d+)(?: ?minutes|m)|(\\d+)(?: ?seconds|s)");
        time = Arrays.toString(pattern.split(time));
        System.out.println(time);

        if (Long.parseLong(time) < 0) {
            context.getTypedMessaging().replyDanger("You cannot seek to a negative value!");
            return;
        }

        context.getMusicPlayer().getPlayer().seekTo(Long.parseLong(time));
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
