package org.cascadebot.cascadebot.scripting.objects.channel;

import net.dv8tion.jda.api.entities.TextChannel;

public class ScriptTextChannel extends ScriptChannel {

    protected TextChannel internalTextChannel;

    public void setInternalTextChannel(TextChannel textChannel) {
        internalTextChannel = textChannel;
        internalChannel = textChannel;
        internalSnowflake = textChannel;
    }

}
