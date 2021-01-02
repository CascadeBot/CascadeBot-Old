package org.cascadebot.cascadebot.scripting.objects;

import net.dv8tion.jda.api.entities.Emote;
import org.cascadebot.cascadebot.scripting.Promise;

import java.util.concurrent.CompletableFuture;

public class ScriptEmote extends ScriptSnowflake {

    protected Emote internalEmote;

    public ScriptEmote(ScriptContext scriptContext) {
        super(scriptContext);
    }

    public String getName() {
        return internalEmote.getName();
    }

    public boolean isAnimated() {
        return internalEmote.isAnimated();
    }

    public Promise setName(String name) {
        CompletableFuture<Void> voidCompletableFuture = internalEmote.getManager().setName(name).submit();
        return scriptContext.handleVoidCompletableFuture(voidCompletableFuture);
    }

    public Promise delete() {
        CompletableFuture<Void> voidCompletableFuture = internalEmote.delete().submit();
        return scriptContext.handleVoidCompletableFuture(voidCompletableFuture);
    }

    public void setInternalEmote(Emote emote) {
        internalEmote = emote;
        internalSnowflake = emote;
    }
}
