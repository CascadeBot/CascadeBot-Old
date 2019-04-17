package org.cascadebot.cascadebot.permissions.objects;

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
