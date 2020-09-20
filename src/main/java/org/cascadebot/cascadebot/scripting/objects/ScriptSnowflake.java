package org.cascadebot.cascadebot.scripting.objects;

import net.dv8tion.jda.api.entities.ISnowflake;

public class ScriptSnowflake {

    protected ISnowflake internalSnowflake;
    protected ScriptContext scriptContext;

    public ScriptSnowflake(ScriptContext scriptContext) {
        this.scriptContext = scriptContext;
    }

    public String getId() {
        return internalSnowflake.getId();
    }

}
