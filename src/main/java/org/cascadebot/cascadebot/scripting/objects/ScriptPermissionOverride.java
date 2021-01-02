package org.cascadebot.cascadebot.scripting.objects;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.PermissionOverride;
import org.cascadebot.cascadebot.scripting.Promise;
import org.graalvm.polyglot.Value;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ScriptPermissionOverride extends ScriptSnowflake {

    private PermissionOverride internalPermissionOverride;

    public ScriptPermissionOverride(ScriptContext scriptContext) {
        super(scriptContext);
    }

    public List<Permission> getAllowed() {
        return new ArrayList<>(internalPermissionOverride.getAllowed());
    }

    public List<Permission> getInherit() {
        return new ArrayList<>(internalPermissionOverride.getInherit());
    }

    public List<Permission> getDenied() {
        return new ArrayList<>(internalPermissionOverride.getDenied());
    }

    public ScriptPermissionHolder getPermissionHolder() {
        return ScriptPermissionHolder.fromJda(scriptContext, internalPermissionOverride.getPermissionHolder());
    }

    public boolean isUserOverride() {
        return internalPermissionOverride.isMemberOverride();
    }

    public boolean isRoleOverride() {
        return internalPermissionOverride.isRoleOverride();
    }

    public Promise reset() {
        CompletableFuture<PermissionOverride> completableFuture = internalPermissionOverride.getManager().reset().submit();
        return handlePermissionOverrideFuture(completableFuture);
    }

    public Promise allow(Permission... permissions) {
        CompletableFuture<PermissionOverride> completableFuture = internalPermissionOverride.getManager().grant(permissions).submit();
        return handlePermissionOverrideFuture(completableFuture);
    }

    public Promise deny(Permission... permissions) {
        CompletableFuture<PermissionOverride> completableFuture = internalPermissionOverride.getManager().deny(permissions).submit();
        return handlePermissionOverrideFuture(completableFuture);
    }

    public Promise inherit(Permission... permissions) {
        CompletableFuture<PermissionOverride> completableFuture = internalPermissionOverride.getManager().clear(permissions).submit();
        return handlePermissionOverrideFuture(completableFuture);
    }

    private Promise handlePermissionOverrideFuture(CompletableFuture<PermissionOverride> completableFuture) {
        return new Promise() {
            @NotNull
            @Override
            public Promise intThen(@NotNull Value resolve, @NotNull Value reject, @NotNull Value callback) {
                completableFuture.whenComplete((permissionOverride, throwable) -> {
                    if (throwable == null) {
                        resolve.executeVoid(permissionOverride);
                    } else {
                        reject.executeVoid(throwable);
                    }
                    callback.executeVoid();
                });
                return this;
            }
        };
    }

    protected void setInternalPermissionOverride(PermissionOverride internalPermissionOverride) {
        this.internalPermissionOverride = internalPermissionOverride;
        this.internalSnowflake = internalPermissionOverride;
    }
}
