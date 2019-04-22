/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.permissions.objects;

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

    public enum ResultCause {

        /**
         * Indicates that this result originated from a permission in a group.
         */
        GROUP,
        /**
         * Indicates that this result originated from a user permission.
         */
        USER,
        /**
         * Indicates that this result was allowed because of a discord permission.
         */
        DISCORD,
        /**
         * Indicates that this result was allowed because of official authorisation.
         * i.e. a developer or owner where all permissions are allowed.
         */
        OFFICIAL,
        /**
         * Indicates that this result is directly from the guild. At the time of writing
         * this is only used for permissions being checked against an administrator/owner
         */
        GUILD,
        /**
         * Indicates that this result is generated from default permissions
         */
        DEFAULT

    }

}
