package org.cascadebot.cascadebot.scripting.objects.channel;

import net.dv8tion.jda.api.entities.StoreChannel;

public class ScriptStoreChannel extends ScriptChannel {

    protected StoreChannel internalStoreChannel;

    public void setInternalStoreChannel(StoreChannel storeChannel) {
        internalStoreChannel = storeChannel;
        internalChannel = storeChannel;
        internalSnowflake = storeChannel;
    }

}
