package org.cascadebot.cascadebot.scripting.objects;

import net.dv8tion.jda.api.entities.VoiceChannel;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.music.CascadePlayer;
import org.cascadebot.cascadebot.scripting.Promise;
import org.cascadebot.cascadebot.scripting.objects.exceptions.ScriptGuildNotFoundException;

import java.util.concurrent.CompletableFuture;

public class ScriptVoiceChannel extends ScriptChannel {

    protected VoiceChannel internalVoiceChannel;

    public ScriptVoiceChannel(ScriptContext scriptContext) {
        super(scriptContext);
    }

    public void joinChannel() throws ScriptGuildNotFoundException {
        CascadePlayer cascadePlayer = CascadeBot.INS.getMusicHandler().getPlayer(Long.parseLong(scriptContext.getGuild().getId()));
        if (cascadePlayer != null) {
            cascadePlayer.join(internalVoiceChannel);
            return;
        }
        throw new ScriptGuildNotFoundException(scriptContext.getGuild().getId());
    }

    public int getUserLimit() {
        return internalVoiceChannel.getUserLimit();
    }

    public int getBitrate() {
        return internalVoiceChannel.getBitrate();
    }

    public Promise setUserLimit(int limit) {
        CompletableFuture<Void> completableFuture = internalVoiceChannel.getManager().setUserLimit(limit).submit();
        return scriptContext.handleVoidCompletableFuture(completableFuture);
    }

    public Promise setBitrate(int bitrate) {
        CompletableFuture<Void> completableFuture = internalVoiceChannel.getManager().setBitrate(bitrate).submit();
        return scriptContext.handleVoidCompletableFuture(completableFuture);
    }

    public void setInternalVoiceChannel(VoiceChannel channel) {
        internalVoiceChannel = channel;
        internalChannel = channel;
        internalSnowflake = channel;
    }

}
