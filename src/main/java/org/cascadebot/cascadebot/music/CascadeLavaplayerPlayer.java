package org.cascadebot.cascadebot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import lavalink.client.player.LavaplayerPlayerWrapper;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class CascadeLavaplayerPlayer extends LavaplayerPlayerWrapper implements CascadePlayer {

    public CascadeLavaplayerPlayer(AudioPlayer player) {
        super(player);
    }

    @Override
    public void join(VoiceChannel channel) {
        channel.getGuild().getAudioManager().openAudioConnection(channel);
    }

    @Override
    public void leave() {
        getGuild().getAudioManager().closeAudioConnection();
    }

    @Override
    public VoiceChannel getConnectedChannel() {
        return getGuild().getAudioManager().getConnectedChannel();
    }

}
