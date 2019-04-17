package org.cascadebot.cascadebot.permissions.objects;

import org.cascadebot.cascadebot.permissions.CascadePermission;

public class Result {

    private CascadePermission permission;
    private PermissionAction action;
    private ResultCause cause;
    private Object causeObject;

    private Result(CascadePermission permission, PermissionAction action) {
        this.permission = permission;
        this.action = action;
    }

    private Result(CascadePermission permission, PermissionAction action, ResultCause cause) {
        this.permission = permission;
        this.action = action;
        this.cause = cause;
    }

    private Result(CascadePermission permission, PermissionAction action, ResultCause cause, Object causeObject) {
        this.permission = permission;
        this.action = action;
        this.cause = cause;
        this.causeObject = causeObject;
    }

    private Result(CascadePermission permission, PermissionAction action, PermissionHolder container) {
        this.permission = permission;
        this.action = action;
        if (container != null) {
            this.cause = ResultCause.valueOf(container.getType().name());
            this.causeObject = container;
        }
    }

    public static Result of(CascadePermission permission, PermissionAction action) {
        return new Result(permission, action);
    }

    public static Result of(CascadePermission permission, PermissionAction action, ResultCause cause) {
        return new Result(permission, action, cause);
    }

    public static Result of(CascadePermission permission, PermissionAction action, ResultCause cause, Object causeObject) {
        return new Result(permission, action, cause, causeObject);
    }

    public static Result of(CascadePermission permission, PermissionAction action, PermissionHolder container) {
        return new Result(permission, action, container);
    }

    public CascadePermission getPermission() {
        return permission;
    }

    public PermissionAction getAction() {
        return action;
    }

    public Object getCauseObject() {
        return causeObject;
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

    @Override
    public String toString() {
        return String.format("Result{action=%s, cause=%s, object=%s}", action.name(), cause.name(), causeObject.toString());
    }

}
