package org.cascadebot.cascadebot.moderation

import lombok.Getter
import org.cascadebot.cascadebot.messaging.MessageType
import java.util.ArrayList
import java.util.Arrays
import java.util.HashMap

enum class ModlogEvent(messageType: MessageType, vararg categories: Category) {

    EMOTE_CREATED(MessageType.INFO, Category.EMOTE),
    EMOTE_DELETED(MessageType.INFO, Category.EMOTE),
    EMOTE_UPDATED_NAME(MessageType.INFO, Category.EMOTE),
    EMOTE_UPDATED_ROLES(MessageType.INFO, Category.EMOTE),

    GUILD_MEMBER_JOINED(MessageType.INFO, Category.GUILD, Category.GUILD_MEMBER),
    GUILD_MEMBER_LEFT(MessageType.INFO, Category.GUILD, Category.GUILD_MEMBER),
    GUILD_MEMBER_KICKED(MessageType.WARNING, Category.GUILD, Category.GUILD_MEMBER, Category.MODERATION),
    GUILD_MEMBER_ROLE_ADDED(MessageType.WARNING, Category.GUILD, Category.GUILD_MEMBER),
    GUILD_MEMBER_ROLE_REMOVED(MessageType.WARNING, Category.GUILD, Category.GUILD_MEMBER),
    GUILD_MEMBER_NICKNAME_UPDATED(MessageType.INFO, Category.GUILD, Category.GUILD_MEMBER),

    GUILD_USER_BANNED(MessageType.DANGER, Category.GUILD, Category.GUILD_MEMBER, Category.MODERATION),
    GUILD_USER_UNBANNED(MessageType.DANGER, Category.GUILD, Category.GUILD_MEMBER, Category.MODERATION),

    GUILD_MESSAGE_DELETED(MessageType.WARNING, Category.GUILD, Category.GUILD_MESSAGE),
    GUILD_MESSAGE_DELETED_SELF(MessageType.INFO, Category.GUILD, Category.GUILD_MESSAGE),
    GUILD_MESSAGE_UPDATED(MessageType.INFO, Category.GUILD, Category.GUILD_MESSAGE),

    GUILD_BOOST_COUNT_UPDATED(MessageType.INFO, Category.GUILD, Category.GUILD_BOOST),
    GUILD_BOOST_TIER_UPDATED(MessageType.INFO, Category.GUILD, Category.GUILD_BOOST),

    GUILD_UPDATE_AFK_CHANNEL(MessageType.WARNING, Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_AFK_TIMEOUT(MessageType.WARNING, Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_BANNER(MessageType.WARNING, Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_DESCRIPTION(MessageType.WARNING, Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_EXPLICIT_FILTER(MessageType.WARNING, Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_FEATURES(MessageType.INFO, Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_ICON(MessageType.WARNING, Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_MAX_MEMBERS(MessageType.INFO, Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_MAX_PRESENCES(MessageType.INFO, Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_MFA_LEVEL(MessageType.WARNING, Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_NAME(MessageType.WARNING, Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_NOTIFICATION_LEVEL(MessageType.WARNING, Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_REGION(MessageType.WARNING, Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_SPLASH(MessageType.WARNING, Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_SYSTEM_CHANNEL(MessageType.WARNING, Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_VANITY_CODE(MessageType.WARNING, Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_VERIFICATION_LEVEL(MessageType.WARNING, Category.GUILD, Category.GUILD_SETTINGS),

    VOICE_DEAFEN(MessageType.INFO, Category.VOICE),
    VOICE_MUTE(MessageType.INFO, Category.VOICE),
    VOICE_SERVER_DEAFEN(MessageType.WARNING, Category.VOICE, Category.MODERATION),
    VOICE_SERVER_MUTE(MessageType.WARNING, Category.VOICE, Category.MODERATION),
    VOICE_JOIN(MessageType.INFO, Category.VOICE),
    VOICE_LEAVE(MessageType.INFO, Category.VOICE),
    VOICE_MOVE(MessageType.WARNING, Category.VOICE),
    VOICE_DISCONNECT(MessageType.WARNING, Category.VOICE, Category.MODERATION),

    ROLE_CREATED(MessageType.WARNING, Category.ROLE),
    ROLE_DELETED(MessageType.WARNING, Category.ROLE),
    ROLE_COLOR_UPDATED(MessageType.WARNING, Category.ROLE),
    ROLE_HOIST_UPDATED(MessageType.WARNING, Category.ROLE),
    ROLE_MENTIONABLE_UPDATED(MessageType.WARNING, Category.ROLE),
    ROLE_NAME_UPDATED(MessageType.WARNING, Category.ROLE),
    ROLE_PERMISSIONS_UPDATED(MessageType.WARNING, Category.ROLE),
    ROLE_POSITION_UPDATED(MessageType.WARNING, Category.ROLE),

    USER_DISCRIMINATOR_UPDATED(MessageType.INFO, Category.USER),
    USER_NAME_UPDATED(MessageType.INFO, Category.USER),

    CHANNEL_CREATED(MessageType.WARNING, Category.CHANNEL),
    CHANNEL_DELETED(MessageType.WARNING, Category.CHANNEL),
    CHANNEL_NAME_UPDATED(MessageType.WARNING, Category.CHANNEL),
    CHANNEL_PERMISSIONS_UPDATED(MessageType.WARNING, Category.CHANNEL),
    CHANNEL_POSITION_UPDATED(MessageType.WARNING, Category.CHANNEL),
    CHANNEL_PARENT_UPDATED(MessageType.WARNING, Category.CHANNEL),

    VOICE_CHANNEL_BITRATE_UPDATED(MessageType.WARNING, Category.CHANNEL),
    VOICE_CHANNEL_USER_LIMIT_UPDATED(MessageType.WARNING, Category.CHANNEL),

    TEXT_CHANNEL_NSFW_UPDATED(MessageType.WARNING, Category.CHANNEL),
    TEXT_CHANNEL_SLOWMODE_UPDATED(MessageType.WARNING, Category.CHANNEL),
    TEXT_CHANNEL_TOPIC_UPDATED(MessageType.WARNING, Category.CHANNEL),

    CASCADE_PERMISSIONS_GROUP_CREATED(MessageType.WARNING, Category.CASCADE, Category.CASCADE_PERMISSIONS),
    CASCADE_PERMISSIONS_GROUP_DELETED(MessageType.WARNING, Category.CASCADE, Category.CASCADE_PERMISSIONS),
    CASCADE_PERMISSIONS_GROUP_PERMISSION_ADD(MessageType.WARNING, Category.CASCADE, Category.CASCADE_PERMISSIONS),
    CASCADE_PERMISSIONS_GROUP_PERMISSION_REMOVE(MessageType.WARNING, Category.CASCADE, Category.CASCADE_PERMISSIONS),
    CASCADE_PERMISSIONS_GROUP_LINK(MessageType.WARNING, Category.CASCADE, Category.CASCADE_PERMISSIONS),

    CASCADE_PERMISSIONS_USER_PERMISSION_ADD(MessageType.WARNING, Category.CASCADE, Category.CASCADE_PERMISSIONS),
    CASCADE_PERMISSIONS_USER_PERMISSION_REMOVE(MessageType.WARNING, Category.CASCADE, Category.CASCADE_PERMISSIONS),
    CASCADE_PERMISSIONS_USER_GROUP_ADD(MessageType.WARNING, Category.CASCADE, Category.CASCADE_PERMISSIONS),
    CASCADE_PERMISSIONS_USER_GROUP_REMOVE(MessageType.WARNING, Category.CASCADE, Category.CASCADE_PERMISSIONS),

    CASCADE_SETTINGS_UPDATED(MessageType.WARNING, Category.CASCADE),

    CASCADE_MODULE_UPDATED(MessageType.WARNING, Category.CASCADE),

    CASCADE_COMMAND_RUN(MessageType.INFO, Category.CASCADE),
    CASCADE_COMMAND_RUN_ERROR(MessageType.WARNING, Category.CASCADE),

    CASCADE_PLAYLIST_CREATED(MessageType.INFO, Category.CASCADE),

    CASCADE_TEMP_MUTE(MessageType.DANGER, Category.CASCADE, Category.CASCADE_MODERATION, Category.MODERATION),
    CASCADE_TEMP_BAN(MessageType.DANGER, Category.CASCADE, Category.CASCADE_MODERATION, Category.MODERATION),
    CASCADE_SOFT_BAN(MessageType.DANGER, Category.CASCADE, Category.CASCADE_MODERATION, Category.MODERATION),
    CASCADE_PURGE(MessageType.DANGER, Category.CASCADE, Category.CASCADE_MODERATION, Category.MODERATION),

    CASCADE_WHITELIST(MessageType.WARNING, Category.CASCADE, Category.CASCADE_MODERATION, Category.MODERATION),
    CASCADE_BLACKLIST(MessageType.WARNING, Category.CASCADE, Category.CASCADE_MODERATION, Category.MODERATION),

    CASCADE_SCRIPT_CREATED(MessageType.WARNING, Category.CASCADE, Category.CASCADE_CUSTOM_COMMANDS),
    CASCADE_SCRIPT_DELETED(MessageType.WARNING, Category.CASCADE, Category.CASCADE_CUSTOM_COMMANDS),
    CASCADE_SCRIPT_UPDATED(MessageType.WARNING, Category.CASCADE, Category.CASCADE_CUSTOM_COMMANDS),

    CASCADE_TAG_CREATED(MessageType.WARNING, Category.CASCADE, Category.CASCADE_CUSTOM_COMMANDS),
    CASCADE_TAG_DELETED(MessageType.WARNING, Category.CASCADE, Category.CASCADE_CUSTOM_COMMANDS),
    CASCADE_TAG_UPDATED(MessageType.WARNING, Category.CASCADE, Category.CASCADE_CUSTOM_COMMANDS);

    companion object {
        private val modlogCategoryMap: MutableMap<Category, MutableList<ModlogEvent>> = HashMap()

        fun getModlogCategoryMap(): Map<Category, MutableList<ModlogEvent>> {
            return modlogCategoryMap
        }

        fun getEventsFromCategory(category: Category): List<ModlogEvent> {
            return modlogCategoryMap[category]!!
        }

        init {
            modlogCategoryMap[Category.ALL] = Arrays.asList(*values())
            for (cat in Category.values()) {
                modlogCategoryMap[cat] = ArrayList()
            }
            for (event in values()) {
                for (category in event.categories) {
                    modlogCategoryMap[category]?.add(event)
                }
            }
        }
    }

    val categories: List<Category>
    val messageType: MessageType

    enum class Category {
        ALL,
        MODERATION,
        EMOTE,
        GUILD,
        GUILD_MEMBER,
        GUILD_MESSAGE,
        GUILD_BOOST,
        GUILD_SETTINGS,
        VOICE,
        ROLE,
        USER,
        CHANNEL,
        CASCADE,
        CASCADE_PERMISSIONS,
        CASCADE_MODERATION,
        CASCADE_CUSTOM_COMMANDS
    }

    init {
        this.categories = Arrays.asList(*categories)
        this.messageType = messageType
    }
}