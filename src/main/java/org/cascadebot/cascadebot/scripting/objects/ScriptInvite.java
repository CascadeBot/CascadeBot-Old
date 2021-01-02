package org.cascadebot.cascadebot.scripting.objects;

import net.dv8tion.jda.api.entities.Invite;
import org.cascadebot.cascadebot.scripting.Promise;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ScriptInvite {

    private ScriptContext scriptContext;
    private Invite internalInvite;

    public ScriptInvite(ScriptContext scriptContext) {
        this.scriptContext = scriptContext;
    }

    public String getCode() {
        return internalInvite.getCode();
    }

    public String getUrl() {
        return internalInvite.getUrl();
    }

    public ScriptUser getInviter() {
        ScriptUser scriptUser = new ScriptUser(scriptContext);
        scriptUser.setInternalUser(scriptContext.getGuild().getMember(Objects.requireNonNull(internalInvite.getInviter())));
        return scriptUser;
    }

    public int getMaxAge() {
        return internalInvite.getMaxAge();
    }

    public int getMaxUsers() {
        return internalInvite.getMaxUses();
    }

    public String getTimeCreated() {
        return String.valueOf(internalInvite.getTimeCreated().toInstant().toEpochMilli());
    }

    public int getUses() {
        return internalInvite.getUses();
    }

    public boolean isTemporary() {
        return internalInvite.isTemporary();
    }

    public Promise delete() {
        CompletableFuture<Void> voidCompletableFuture = internalInvite.delete().submit();
        return scriptContext.handleVoidCompletableFuture(voidCompletableFuture);
    }

    public void setInternalInvite(Invite internalInvite) {
        this.internalInvite = internalInvite;
    }
}
