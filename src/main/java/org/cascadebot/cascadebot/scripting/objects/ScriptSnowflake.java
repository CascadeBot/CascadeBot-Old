package org.cascadebot.cascadebot.scripting.objects;

import net.dv8tion.jda.api.entities.ISnowflake;

import java.util.Date;

public class ScriptSnowflake {

    protected ISnowflake internalSnowflake;
    protected ScriptContext scriptContext;

    public ScriptSnowflake(ScriptContext scriptContext) {
        this.scriptContext = scriptContext;
    }

    public String getId() {
        return internalSnowflake.getId();
    }

    public String getTimeCreated() {
        return String.valueOf(internalSnowflake.getTimeCreated().toInstant().toEpochMilli());
    }

}
