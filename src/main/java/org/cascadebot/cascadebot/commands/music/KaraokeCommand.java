package org.cascadebot.cascadebot.commands.music;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.music.KaraokeHandler;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import javax.xml.parsers.ParserConfigurationException;


public class KaraokeCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        try {
            KaraokeHandler.getSongLyrics(context.getMusicPlayer().getPlayer().getPlayingTrack().getIdentifier(), context.getChannel(), context.getGuild().getIdLong());
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
    }
    }

    @Override
    public Module getModule() {
        return Module.MUSIC;
    }

    @Override
    public String command() {
        return "karaoke";
    }

    @Override
    public CascadePermission getPermission() {
        return null;
    }
}
