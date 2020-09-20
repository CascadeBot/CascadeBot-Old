package org.cascadebot.cascadebot.scripting.objects.channel;

import net.dv8tion.jda.api.entities.VoiceChannel;
import org.cascadebot.cascadebot.scripting.objects.ScriptContext;

public class ScriptVoiceChannel extends ScriptChannel {

    protected VoiceChannel internalVoiceChannel;

    public ScriptVoiceChannel(ScriptContext scriptContext) {
        super(scriptContext);
    }

    public void joinChannel() {
        // TODO
    }

    public void setInternalVoiceChannel(VoiceChannel channel) {
        internalVoiceChannel = channel;
        internalChannel = channel;
        internalSnowflake = channel;
    }

}
