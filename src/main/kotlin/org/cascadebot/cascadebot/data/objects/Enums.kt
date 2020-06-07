package org.cascadebot.cascadebot.data.objects

import net.dv8tion.jda.api.entities.User
import org.cascadebot.cascadebot.utils.PurgeUtils

enum class PlaylistType {
    GUILD, USER
}

enum class SearchResultType {
    VIDEO, PLAYLIST
}

/**
 * Criteria for the [PurgeUtils.purge] method that checks
 * which filters you are trying to apply to the message searching
 *
 *
 *  * `Attachment - Clears messages with ` [net.dv8tion.jda.api.entities.Message.Attachment]
 *  * `Bot - Clears messages with ` [User.isBot] `as true`
 *  * `Link - Clears any message with a regex checking for links`
 *  * `Token - Clears any message that contains x`
 *  * `User - Clears any message from a specific ` [User]
 *  * `All - Clears anything`
 *
 * @author DeadlyFirex
 * @see PurgeUtils.purge
 */
enum class PurgeCriteria {
    ATTACHMENT, BOT, LINK, TOKEN, USER, ALL
}

enum class ColorErrorType {
    RGB, BINARY, HEX, DECIMAL, UNRECOGNISED
}


enum class VoteMessageType {
    /**
     * This vote type will use ✅ (\u2705) and ❌ (\u274C) for votes
     */
    YES_NO,

    /**
     * This vote type will use discord regional indicator numbers up to the amount specified in the builder.
     */
    NUMBERS,

    /**
     * This vote type will use discord regional indicator letters up to the amount specified in the builder.
     */
    LETTERS
}

enum class PermissionAction {
    /**
     * This override indicates that the permission action has no effect
     * on the permission access. If every permission action is neutral,
     * the permission will be **implicitly** denied.
     */
    NEUTRAL,

    /**
     * This override indicates that the permission action explicitly
     * allows the permission. In hierarchy mode, the permission will only
     * be subsequently allowed if all actions above the current one in the stack
     * respond with either `NEUTRAL` or `ALLOW`. In most restrictive mode, the
     * permission will be allowed only if there are no actions that deny the permission.
     */
    ALLOW,

    /**
     * This override indicates that the permission action explicitly denies
     * the permission. In hierarchy mode, the permission will only
     * be subsequently denied if all actions above the current one in the stack
     * respond with either `NEUTRAL` or `DENY`. In most restrictive mode, the
     * permission will be denied regardless of the other permission actions.
     */
    DENY
}

enum class LoopMode {
    DISABLED, PLAYLIST, SONG
}

enum class SavePlaylistResultType {
    ALREADY_EXISTS, OVERWRITE, NEW
}

enum class LoadPlaylistResult {
    LOADED_GUILD, LOADED_USER, EXISTS_IN_ALL_SCOPES, DOESNT_EXIST
}

enum class PermissionMode {
    HIERARCHICAL, MOST_RESTRICTIVE
}

enum class ModuleFlag {
    /**
     * Indicates that this module must always be enabled for the proper function
     * of the bot.
     */
    REQUIRED,

    /**
     * Indicates that this module cannot be used by general users of the bot and
     * so is unable to be disabled
     */
    PRIVATE,

    /**
     * Indicates that the module will be enabled by default
     */
    DEFAULT
}

enum class ArgumentType {
    /**
     * Represents an optional parameter.
     */
    OPTIONAL,

    /**
     * Represents an required parameter.
     */
    REQUIRED,

    /**
     * Represents that this is just part of the command and isn't a parameter.
     */
    COMMAND
}