package org.cascadebot.cascadebot.commands.music;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.music.KaraokeHandler;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import javax.xml.parsers.ParserConfigurationException;


public class KaraokeCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getMusicPlayer().getPlayer().getPlayingTrack() == null) {
            KaraokeHandler.setKaraoke(context.getGuild().getIdLong(), false);
            context.getTypedMessaging().replyDanger("nothing playing so i dont wanna turn on karaoke");
            return;
        }

        if (KaraokeHandler.isKaraoke(context.getGuild().getIdLong())) {
            KaraokeHandler.setKaraoke(context.getGuild().getIdLong(), false);
            context.getTypedMessaging().replySuccess("Karaoke disabled thx for using cascade xo");
        } else {
            KaraokeHandler.setKaraoke(context.getGuild().getIdLong(), true);
            context.getTypedMessaging().replySuccess("enabled");
        }

        try {
            context.getTypedMessaging().replyInfo("Loading lyrics...");
            System.out.println(context.getMessage().getId());
            context.getChannel().getLatestMessageId();
            KaraokeHandler.getSongLyrics(context.getMusicPlayer().getPlayer().getPlayingTrack().getIdentifier(), context.getChannel(), context.getGuild().getIdLong(), context.getMessage().getIdLong());
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
