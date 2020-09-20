package org.cascadebot.cascadebot.scripting.objects.channel;

import net.dv8tion.jda.api.entities.StoreChannel;
import org.cascadebot.cascadebot.scripting.objects.ScriptContext;

public class ScriptStoreChannel extends ScriptChannel {

    protected StoreChannel internalStoreChannel;

    public ScriptStoreChannel(ScriptContext scriptContext) {
        super(scriptContext);
    }

    public void setInternalStoreChannel(StoreChannel storeChannel) {
        internalStoreChannel = storeChannel;
        internalChannel = storeChannel;
        internalSnowflake = storeChannel;
    }

}
