/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.permissions.objects;

public enum PermissionAction {

    /**
     * This override indicates that the permission action has no effect
     * on the permission access. If every permission action is neutral,
     * the permission will be <strong>implicitly</strong> denied.
     */
    NEUTRAL,
    /**
     * This override indicates that the permission action explicitly
     * allows the permission. In hierarchy mode, the permission will only
     * be subsequently allowed if all actions above the current one in the stack
     * respond with either {@code NEUTRAL} or {@code ALLOW}. In most restrictive mode, the
     * permission will be allowed only if there are no actions that deny the permission.
     */
    ALLOW,
    /**
     * This override indicates that the permission action explicitly denies
     * the permission. In hierarchy mode, the permission will only
     * be subsequently denied if all actions above the current one in the stack
     * respond with either {@code NEUTRAL} or {@code DENY}. In most restrictive mode, the
     * permission will be denied regardless of the other permission actions.
     */
    DENY;

    public Result toResult(PermissionHolder holder) {
        return Result.of(this, holder);
    }

}
