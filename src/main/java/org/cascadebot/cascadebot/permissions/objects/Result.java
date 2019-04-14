package org.cascadebot.cascadebot.permissions.objects;

public class Result {

    private PermissionAction action;
    private PermissionHolder container;
    private ResultCause cause;

    public Result(PermissionAction action) {
        this.action = action;
    }

    public Result(PermissionAction action, ResultCause cause) {
        this.action = action;
        this.cause = cause;
    }

    public Result(PermissionAction action, PermissionHolder container) {
        this.action = action;
        if (container != null) {
            this.container = container;
            this.cause = ResultCause.valueOf(container.getType().name());
        }
    }

    public PermissionAction getAction() {
        return action;
    }

    public PermissionHolder getContainer() {
        return container;
    }

    public PermissionHolder.HolderType getHolderType() {
        return container.getType();
    }

    public ResultCause getCause() {
        return cause;
    }

    public boolean isDenied() {
        return action == PermissionAction.DENY;
    }

    public boolean isAllowed() {
        return action == PermissionAction.ALLOW;
    }

    public boolean isNeutral() {
        return action == PermissionAction.NEUTRAL;
    }

}
