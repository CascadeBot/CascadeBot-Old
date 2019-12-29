package org.cascadebot.cascadebot.moderation;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public enum ModlogEvent {

    EMOTE_CREATED(Category.EMOTE),
    EMOTE_DELETED(Category.EMOTE),
    EMOTE_UPDATED_NAME(Category.EMOTE),
    EMOTE_UPDATED_ROLES(Category.EMOTE),

    GUILD_MEMBER_JOINED(Category.GUILD, Category.GUILD_MEMBER),
    GUILD_MEMBER_LEFT(Category.GUILD, Category.GUILD_MEMBER),
    GUILD_MEMBER_KICKED(Category.GUILD, Category.GUILD_MEMBER, Category.MODERATION),
    GUILD_MEMBER_BANNED(Category.GUILD, Category.GUILD_MEMBER, Category.MODERATION),
    GUILD_MEMBER_ROLE_ADDED(Category.GUILD, Category.GUILD_MEMBER),
    GUILD_MEMBER_NICKNAME_UPDATED(Category.GUILD, Category.GUILD_MEMBER),
    GUILD_USER_UNBANNED(Category.GUILD, Category.GUILD_MEMBER, Category.MODERATION),

//    GUILD_MESSAGE_DELETED(Category.GUILD, Category.GUILD_MESSAGE),
//    GUILD_MESSAGE_UPDATED(Category.GUILD, Category.GUILD_MESSAGE),

    GUILD_BOOST_COUNT_UPDATED(Category.GUILD, Category.GUILD_BOOST),
    GUILD_BOOST_TIER_UPDATED(Category.GUILD, Category.GUILD_BOOST),

    GUILD_UPDATE_AFK_CHANNEL(Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_AFK_TIMEOUT(Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_BANNER(Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_DESCRIPTION(Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_EXPLICIT_FILTER(Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_FEATURES(Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_ICON(Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_MAX_MEMBERS(Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_MAX_PRESENCES(Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_MFA_LEVEL(Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_NAME(Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_NOTIFICATION_LEVEL(Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_REGION(Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_SPLASH(Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_SYSTEM_CHANNEL(Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_VANITY_CODE(Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_VERIFICATION_LEVEL(Category.GUILD, Category.GUILD_SETTINGS),

    VOICE_DEAFEN(Category.VOICE),
    VOICE_MUTE(Category.VOICE),
    VOICE_SERVER_DEAFEN(Category.VOICE, Category.MODERATION),
    VOICE_SERVER_MUTE(Category.VOICE, Category.MODERATION),
    VOICE_JOIN(Category.VOICE),
    VOICE_LEAVE(Category.VOICE),
    VOICE_MOVE(Category.VOICE),
    VOICE_DISCONNECT(Category.VOICE, Category.MODERATION),

    ROLE_CREATED(Category.ROLE),
    ROLE_DELETED(Category.ROLE),
    ROLE_COLOR_UPDATED(Category.ROLE),
    ROLE_HOIST_UPDATED(Category.ROLE),
    ROLE_MENTIONABLE_UPDATED(Category.ROLE),
    ROLE_NAME_UPDATED(Category.ROLE),
    ROLE_PERMISSIONS_UPDATED(Category.ROLE),
    ROLE_POSITION_UPDATED(Category.ROLE),

    USER_DISCRIMINATOR_UPDATED(Category.USER),
    USER_NAME_UPDATED(Category.USER),
    
    CHANNEL_CREATED(Category.CHANNEL),
    CHANNEL_DELETED(Category.CHANNEL),
    CHANNEL_NAME_UPDATED(Category.CHANNEL),
    CHANNEL_PERMISSIONS_UPDATED(Category.CHANNEL),
    CHANNEL_POSITION_UPDATED(Category.CHANNEL),
    CHANNEL_PARENT_UPDATED(Category.CHANNEL),
    
    VOICE_CHANNEL_BITRATE_UPDATED(Category.CHANNEL),
    VOICE_CHANNEL_USER_LIMIT_UPDATED(Category.CHANNEL),
    
    TEXT_CHANNEL_NSFW_UPDATED(Category.CHANNEL),
    TEXT_CHANNEL_SLOWMODE_UPDATED(Category.CHANNEL),
    TEXT_CHANNEL_TOPIC_UPDATED(Category.CHANNEL),

    CASCADE_PERMISSIONS_GROUP_CREATED(Category.CASCADE, Category.CASCADE_PERMISSIONS),
    CASCADE_PERMISSIONS_GROUP_DELETED(Category.CASCADE, Category.CASCADE_PERMISSIONS),
    CASCADE_PERMISSIONS_GROUP_PERMISSION_ADD(Category.CASCADE, Category.CASCADE_PERMISSIONS),
    CASCADE_PERMISSIONS_GROUP_PERMISSION_REMOVE(Category.CASCADE, Category.CASCADE_PERMISSIONS),
    CASCADE_PERMISSIONS_GROUP_LINK(Category.CASCADE, Category.CASCADE_PERMISSIONS),

    CASCADE_PERMISSIONS_USER_PERMISSION_ADD(Category.CASCADE, Category.CASCADE_PERMISSIONS),
    CASCADE_PERMISSIONS_USER_PERMISSION_REMOVE(Category.CASCADE, Category.CASCADE_PERMISSIONS),
    CASCADE_PERMISSIONS_USER_GROUP_ADD(Category.CASCADE, Category.CASCADE_PERMISSIONS),
    CASCADE_PERMISSIONS_USER_GROUP_REMOVE(Category.CASCADE, Category.CASCADE_PERMISSIONS),

    CASCADE_SETTINGS_UPDATED(Category.CASCADE),

    CASCADE_MODULE_UPDATED(Category.CASCADE),

    CASCADE_COMMAND_RUN(Category.CASCADE),
    CASCADE_COMMAND_RUN_ERROR(Category.CASCADE),

    CASCADE_PLAYLIST_CREATED(Category.CASCADE),
    CASCADE_PLAYLIST_DELETED(Category.CASCADE),

    CASCADE_TEMP_MUTE(Category.CASCADE, Category.CASCADE_MODERATION, Category.MODERATION),
    CASCADE_TEMP_BAN(Category.CASCADE, Category.CASCADE_MODERATION, Category.MODERATION),
    CASCADE_SOFT_BAN(Category.CASCADE, Category.CASCADE_MODERATION, Category.MODERATION),
    CASCADE_PURGE(Category.CASCADE, Category.CASCADE_MODERATION, Category.MODERATION),

    CASCADE_WHITELIST(Category.CASCADE, Category.CASCADE_MODERATION, Category.MODERATION),
    CASCADE_BLACKLIST(Category.CASCADE, Category.CASCADE_MODERATION, Category.MODERATION),

    CASCADE_SCRIPT_CREATED(Category.CASCADE, Category.CASCADE_CUSTOM_COMMANDS),
    CASCADE_SCRIPT_DELETED(Category.CASCADE, Category.CASCADE_CUSTOM_COMMANDS),
    CASCADE_SCRIPT_UPDATED(Category.CASCADE, Category.CASCADE_CUSTOM_COMMANDS),

    CASCADE_TAG_CREATED(Category.CASCADE, Category.CASCADE_CUSTOM_COMMANDS),
    CASCADE_TAG_DELETED(Category.CASCADE, Category.CASCADE_CUSTOM_COMMANDS),
    CASCADE_TAG_UPDATED(Category.CASCADE, Category.CASCADE_CUSTOM_COMMANDS);

    @Getter
    private static Map<Category, List<ModlogEvent>> modlogCategoryMap = new HashMap<>();

    static {
        modlogCategoryMap.put(Category.ALL, Arrays.asList(ModlogEvent.values()));
        for (ModlogEvent event : ModlogEvent.values()) {
            for (Category category : event.getCategories()) {
                if (modlogCategoryMap.containsKey(category)) {
                    modlogCategoryMap.get(category).add(event);
                } else {
                    modlogCategoryMap.put(category, new ArrayList<>(Set.of(event)));
                }
            }
        }
    }

    @Getter
    private final List<Category> categories;

    ModlogEvent(Category... categories) {
        this.categories = Arrays.asList(categories);
    }

    public enum Category {
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


}
