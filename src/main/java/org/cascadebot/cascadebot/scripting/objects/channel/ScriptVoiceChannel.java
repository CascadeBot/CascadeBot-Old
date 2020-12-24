package org.cascadebot.cascadebot.scripting.objects.channel;

import net.dv8tion.jda.api.entities.VoiceChannel;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.music.CascadePlayer;
import org.cascadebot.cascadebot.music.MusicHandler;
import org.cascadebot.cascadebot.scripting.objects.ScriptContext;
import org.cascadebot.cascadebot.scripting.objects.exceptions.ScriptGuildNotFoundException;

public class ScriptVoiceChannel extends ScriptChannel {

    protected VoiceChannel internalVoiceChannel;

    public ScriptVoiceChannel(ScriptContext scriptContext) {
        super(scriptContext);
    }

    public void joinChannel() throws ScriptGuildNotFoundException {
        CascadePlayer cascadePlayer = CascadeBot.INS.getMusicHandler().getPlayer(scriptContext.getGuild().getIdLong());
        if (cascadePlayer != null) {
            cascadePlayer.join(internalVoiceChannel);
            return;
        }
        throw new ScriptGuildNotFoundException(scriptContext.getGuild().getIdLong());
    }

    public void setInternalVoiceChannel(VoiceChannel channel) {
        internalVoiceChannel = channel;
        internalChannel = channel;
        internalSnowflake = channel;
    }

}
