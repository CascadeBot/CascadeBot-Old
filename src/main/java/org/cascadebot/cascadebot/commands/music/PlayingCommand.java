package org.cascadebot.cascadebot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.music.CascadePlayer;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.FormatUtils;

import java.util.Set;

public class PlayingCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        CascadePlayer player = context.getData().getMusicPlayer();
        AudioTrack track = player.getPlayer().getPlayingTrack();

        if (player.getPlayer().getPlayingTrack() == null) {
            context.getTypedMessaging().replyWarning("No music playing!");
        } else {
            EmbedBuilder embedBuilder = MessagingObjects.getClearThreadLocalEmbedBuilder();
            embedBuilder.setAuthor(track.getInfo().author);
            embedBuilder.setTitle(track.getInfo().title, track.getInfo().uri);
            if (player.getArtwork() != null) {
                embedBuilder.setThumbnail(player.getArtwork());
            }
            embedBuilder.addField("Status", player.getPlayer().isPaused() ? "\u23F8 Paused" : "\u25B6 Playing", true);

            if (!track.getInfo().isStream) {
                embedBuilder.addField("Progress", player.getTrackProgressBar(context.getData().getSettings().useEmbedForMessages()), false);
            }

            embedBuilder.addField("Amount played", FormatUtils.formatLongTimeMills(track.getPosition()) + "/" +
                    (!track.getInfo().isStream ? FormatUtils.formatLongTimeMills(track.getDuration()) : "\u221e" /* Infinity Symbol */), true);
            embedBuilder.addField("Volume", player.getPlayer().getVolume() + "%", true);
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
