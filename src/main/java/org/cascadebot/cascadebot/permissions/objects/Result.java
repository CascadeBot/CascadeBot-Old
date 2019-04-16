package org.cascadebot.cascadebot.permissions.objects;

import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

public class Result {

    private PermissionAction action;
    private PermissionHolder container;
    private ResultCause cause;

    private static Map<Pair<PermissionAction, ResultCause>, Result> cache = new HashMap<>();

    private Result(PermissionAction action) {
        this.action = action;
    }

    private Result(PermissionAction action, ResultCause cause) {
        this.action = action;
        this.cause = cause;
    }

    private Result(PermissionAction action, PermissionHolder container) {
        this.action = action;
        if (container != null) {
            this.container = container;
            this.cause = ResultCause.valueOf(container.getType().name());
        }
    }

    public static Result of(PermissionAction action) {
        var key = Pair.of(action, (ResultCause) null);
        return cache.computeIfAbsent(key, pairKey -> new Result(pairKey.getKey()));
    }

    public static Result of(PermissionAction action, ResultCause cause) {
        var key = Pair.of(action, cause);
        return cache.computeIfAbsent(key, pairKey -> new Result(pairKey.getKey(), pairKey.getValue()));
    }

    public static Result of(PermissionAction action, PermissionHolder container) {
        return new Result(action, container);
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
