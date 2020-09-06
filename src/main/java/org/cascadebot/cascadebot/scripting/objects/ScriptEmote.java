package org.cascadebot.cascadebot.scripting.objects;

import net.dv8tion.jda.api.entities.Emote;

public class ScriptEmote extends ScriptSnowflake {

    protected Emote internalEmote;

    public void setInternalEmote(Emote emote) {
        internalEmote = emote;
        internalSnowflake = emote;
    }
}
