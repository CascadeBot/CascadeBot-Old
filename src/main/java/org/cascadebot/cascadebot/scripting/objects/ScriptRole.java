package org.cascadebot.cascadebot.scripting.objects;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import org.cascadebot.cascadebot.scripting.Promise;

import java.awt.Color;
import java.util.concurrent.CompletableFuture;

public class ScriptRole extends ScriptPermissionHolder {

    protected Role internalRole;

    public ScriptRole(ScriptContext scriptContext) {
        super(scriptContext);
    }

    public String getName() {
        return internalRole.getName();
    }

    public Color getColor() {
        return internalRole.getColor();
    }

    public int getPosition() {
        return internalRole.getPosition();
    }

    public boolean isManaged() {
        return internalRole.isManaged();
    }

    public boolean isHoisted() {
        return internalRole.isHoisted();
    }

    public boolean isMentionable() {
        return internalRole.isMentionable();
    }

    public Promise setName(String name) {
        CompletableFuture<Void> voidCompletableFuture = internalRole.getManager().setName(name).submit();
        return scriptContext.handleVoidCompletableFuture(voidCompletableFuture);
    }

    public Promise setColor(Color color) {
        CompletableFuture<Void> voidCompletableFuture = internalRole.getManager().setColor(color).submit();
        return scriptContext.handleVoidCompletableFuture(voidCompletableFuture);
    }

    public Promise setHoisted(boolean hoisted) {
        CompletableFuture<Void> voidCompletableFuture = internalRole.getManager().setHoisted(hoisted).submit();
        return scriptContext.handleVoidCompletableFuture(voidCompletableFuture);
    }

    public Promise setMentionable(boolean mentionable) {
        CompletableFuture<Void> voidCompletableFuture = internalRole.getManager().setMentionable(mentionable).submit();
        return scriptContext.handleVoidCompletableFuture(voidCompletableFuture);
    }

    public Promise addPermissions(Permission... permissions) {
        CompletableFuture<Void> voidCompletableFuture = internalRole.getManager().givePermissions(permissions).submit();
        return scriptContext.handleVoidCompletableFuture(voidCompletableFuture);
    }

    public Promise setPermissions(Permission... permissions) {
        CompletableFuture<Void> voidCompletableFuture = internalRole.getManager().setPermissions(permissions).submit();
        return scriptContext.handleVoidCompletableFuture(voidCompletableFuture);
    }

    public Promise revokePermissions(Permission... permissions) {
        CompletableFuture<Void> voidCompletableFuture = internalRole.getManager().revokePermissions(permissions).submit();
        return scriptContext.handleVoidCompletableFuture(voidCompletableFuture);
    }

    public Promise move(int position) {
        CompletableFuture<Void> voidCompletableFuture = internalRole.getGuild().modifyRolePositions().selectPosition(internalRole).moveTo(position).submit();
        return scriptContext.handleVoidCompletableFuture(voidCompletableFuture);
    }

    public Promise delete() {
        CompletableFuture<Void> voidCompletableFuture = internalRole.delete().submit();
        return scriptContext.handleVoidCompletableFuture(voidCompletableFuture);
    }

    public void setInternalRole(Role role) {
        this.internalRole = role;
        this.internalPermissionHolder = role;
        this.internalSnowflake = role;
    }

}
