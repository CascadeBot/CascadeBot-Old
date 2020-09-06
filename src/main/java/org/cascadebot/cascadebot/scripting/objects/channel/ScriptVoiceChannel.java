package org.cascadebot.cascadebot.scripting.objects.channel;

import net.dv8tion.jda.api.entities.VoiceChannel;

public class ScriptVoiceChannel extends ScriptChannel {

    protected VoiceChannel internalVoiceChannel;

    public void setInternalVoiceChannel(VoiceChannel channel) {
        internalVoiceChannel = channel;
        internalChannel = channel;
        internalSnowflake = channel;
    }

}
