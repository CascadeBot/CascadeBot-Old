package org.cascadebot.cascadebot.scripting.objects;

import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.WebhookType;
import org.cascadebot.cascadebot.scripting.Promise;

import java.util.concurrent.CompletableFuture;

public class ScriptWebhook extends ScriptSnowflake {

    protected Webhook internalWebhook;
    public ScriptWebhook(ScriptContext scriptContext) {
        super(scriptContext);
    }

    public WebhookType getType() {
        return internalWebhook.getType();
    }

    public ScriptUser getOwner() {
        ScriptUser scriptUser = new ScriptUser(scriptContext);
        scriptUser.setInternalUser(internalWebhook.getOwner());
        return scriptUser;
    }

    public String getName() {
        return internalWebhook.getName();
    }

    public String getUrl() {
        return internalWebhook.getName();
    }

    public String getToken() {
        return internalWebhook.getToken();
    }

    public Promise setName(String name) {
        CompletableFuture<Void> completableFuture = internalWebhook.getManager().setName(name).submit();
        return scriptContext.handleVoidCompletableFuture(completableFuture);
    }

    public Promise delete() {
        CompletableFuture<Void> completableFuture = internalWebhook.delete().submit();
        return scriptContext.handleVoidCompletableFuture(completableFuture);
    }

    public void setInternalWebhook(Webhook internalWebhook) {
        this.internalWebhook = internalWebhook;
        this.internalSnowflake = internalWebhook;
    }
}
