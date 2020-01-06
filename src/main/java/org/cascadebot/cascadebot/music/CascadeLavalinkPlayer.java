package org.cascadebot.cascadebot.music;

import lavalink.client.io.LavalinkSocket;
import lavalink.client.io.Link;
import lavalink.client.io.jda.JdaLink;
import lavalink.client.player.LavalinkPlayer;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.cascadebot.cascadebot.CascadeBot;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

public class CascadeLavalinkPlayer extends LavalinkPlayer implements CascadePlayer {

    public CascadeLavalinkPlayer(Link link) {
        super(link);
    }

    @Override
    public void join(VoiceChannel channel) {
        getLink().connect(channel);
    }

    @Override
    public void leave() {
        getLink().disconnect();
    }

    @Override
    public VoiceChannel getConnectedChannel() {
        if (getLink().getChannel() != null) {
            return CascadeBot.INS.getShardManager().getVoiceChannelById(getLink().getChannel());
        } else {
            return null;
        }
    }

    public JdaLink getLink() {
        return MusicHandler.getLavaLink().getLink(getGuild());
    }

    public void setBands(Map<Integer, Float> bands) throws UnsupportedOperationException {
        LavalinkSocket node =  super.getLink().getNode(false);
        if (node != null) {
            JSONObject json = new JSONObject();
            json.put("op", "equalizer");
            json.put("guildId", super.getLink().getGuildId());
            JSONArray jsonArray = new JSONArray();
            for (Map.Entry<Integer, Float> entry : bands.entrySet()) {
                if (entry.getKey() < 0 || entry.getKey() > 14) {
                    throw new UnsupportedOperationException("Cannot set a band that doesn't exist");
                }
                JSONObject bandJson = new JSONObject();
                bandJson.put("band", entry.getKey());
                bandJson.put("gain", entry.getValue());
                jsonArray.put(bandJson);
            }
            node.send(json.toString());
        }
    }

}
