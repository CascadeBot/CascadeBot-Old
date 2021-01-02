package org.cascadebot.cascadebot.scripting.objects;

import net.dv8tion.jda.api.entities.GuildVoiceState;

public class ScriptGuildVoiceState {

    private ScriptContext scriptContext;
    private GuildVoiceState guildVoiceState;

    public ScriptGuildVoiceState(ScriptContext scriptContext) {
        this.scriptContext = scriptContext;
    }

    public boolean isSelfMuted() {
        return guildVoiceState.isSelfMuted();
    }

    public boolean isSelfDeafened() {
        return guildVoiceState.isSelfDeafened();
    }

    public boolean isMuted() {
        return guildVoiceState.isMuted();
    }

    public boolean isDeafened() {
        return guildVoiceState.isDeafened();
    }

    public boolean isGuildMuted() {
        return guildVoiceState.isGuildMuted();
    }

    public boolean isGuildDeafened() {
        return guildVoiceState.isGuildDeafened();
    }

    public boolean isSuppressed() {
        return guildVoiceState.isSuppressed();
    }

    public boolean isStreaming() {
        return guildVoiceState.isStream();
    }

    public ScriptVoiceChannel getChannel() {
        ScriptVoiceChannel scriptVoiceChannel = new ScriptVoiceChannel(scriptContext);
        scriptVoiceChannel.setInternalVoiceChannel(guildVoiceState.getChannel());
        return scriptVoiceChannel;
    }

    public ScriptUser getUser() {
        ScriptUser scriptUser = new ScriptUser(scriptContext);
        scriptUser.setInternalUser(guildVoiceState.getMember());
        return scriptUser;
    }

    public boolean inVoiceChannel() {
        return guildVoiceState.inVoiceChannel();
    }

    public void setInternalVoiceState(GuildVoiceState guildVoiceState) {
        this.guildVoiceState = guildVoiceState;
    }

}
