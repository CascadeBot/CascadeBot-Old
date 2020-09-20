package org.cascadebot.cascadebot.scripting.objects;

import net.dv8tion.jda.api.entities.Message;

public class ScriptMessage extends ScriptSnowflake {

    private Message internalMessage;

    public ScriptMessage(ScriptContext scriptContext) {
        super(scriptContext);
    }

    public void editMessage(String message) {
        scriptContext.addFuture(internalMessage.editMessage(message).submit());
    }

    public void setInternalMessage(Message message) {
        internalMessage = message;
        internalSnowflake = message;
    }

}
