package org.cascadebot.cascadebot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lavalink.client.player.IPlayer;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.FormatUtils;

import java.util.Set;

public class PlayingCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        IPlayer player = context.getData().getMusicPlayer().getPlayer();
        AudioTrack track = player.getPlayingTrack();

        if (player.getPlayingTrack() == null) {
            context.getTypedMessaging().replyWarning("No music playing!");
        } else {
            EmbedBuilder embedBuilder = MessagingObjects.getClearThreadLocalEmbedBuilder();
            embedBuilder.setTitle(track.getInfo().title + " - " + track.getInfo().author, track.getInfo().uri);
            embedBuilder.setThumbnail("https://img.youtube.com/vi/" + track.getIdentifier() + "/hqdefault.jpg");
            embedBuilder.addField("Status", player.isPaused() ? "\u23F8 Paused" : "\u25B6 Playing", true);
            embedBuilder.addField("Duration", track.getInfo().isStream ? "This is a livestream!" : FormatUtils.formatLongTimeMills(track.getDuration()), true);
            embedBuilder.addField("Amount played", FormatUtils.formatLongTimeMills(track.getPosition()), true);
            embedBuilder.addField("Volume", player.getVolume() + "%", true);
            embedBuilder.setFooter("Requested by " + sender.getUser().getAsTag(), sender.getUser().getEffectiveAvatarUrl());
            context.getTypedMessaging().replyInfo(embedBuilder);
        }

    }

    @Override
    public Module getModule() {
        return Module.MUSIC;
    }

    @Override
    public Set<String> getGlobalAliases() {
        return Set.of("song");
    }

    @Override
    public String command() {
        return "playing";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Playing Command", "playing", true);
    }

    @Override
    public String description() {
        return "get playing music";
    }

}
