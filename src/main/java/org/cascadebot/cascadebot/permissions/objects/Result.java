package org.cascadebot.cascadebot.permissions.objects;

import org.cascadebot.cascadebot.permissions.CascadePermission;

public class Result {

    private PermissionAction action;
    private ResultCause cause;
    private Object causeObject;

    private Result(PermissionAction action) {
        this.action = action;
    }

    private Result(PermissionAction action, ResultCause cause) {
        this.action = action;
        this.cause = cause;
    }

    private Result(PermissionAction action, ResultCause cause, Object causeObject) {
        this.action = action;
        this.cause = cause;
        this.causeObject = causeObject;
    }

    private Result(PermissionAction action, PermissionHolder container) {
        this.action = action;
        if (container != null) {
            this.cause = ResultCause.valueOf(container.getType().name());
            this.causeObject = container;
        }
    }

    public static Result of(PermissionAction action) {
        return new Result(action);
    }

    public static Result of(PermissionAction action, ResultCause cause) {
        return new Result(action, cause);
    }

    public static Result of(PermissionAction action, ResultCause cause, Object causeObject) {
        return new Result(action, cause, causeObject);
    }

    public static Result of(PermissionAction action, PermissionHolder container) {
        return new Result(action, container);
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
