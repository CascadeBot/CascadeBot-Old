package org.cascadebot.cascadebot.moderation

import de.bild.codec.annotations.Transient
import org.cascadebot.cascadebot.data.objects.AffectedDisplayType
import org.cascadebot.cascadebot.messaging.MessageType
import java.util.EnumMap

enum class ModlogEvent(@Transient val messageType: MessageType, val displayType: ModlogDisplayType, val affectedDisplayType: AffectedDisplayType, vararg categories: Category) {

    EMOTE_CREATED				                (MessageType.INFO, ModlogDisplayType.AFFECTED_THUMBNAIL, AffectedDisplayType.MENTION, Category.EMOTE),
    EMOTE_DELETED				                (MessageType.INFO, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.EMOTE),
    EMOTE_UPDATED_NAME			                (MessageType.INFO, ModlogDisplayType.AFFECTED_THUMBNAIL, AffectedDisplayType.MENTION, Category.EMOTE),
    EMOTE_UPDATED_ROLES			                (MessageType.INFO, ModlogDisplayType.AFFECTED_THUMBNAIL, AffectedDisplayType.MENTION, Category.EMOTE),

    GUILD_MEMBER_JOINED			                (MessageType.INFO, ModlogDisplayType.AFFECTED_THUMBNAIL, AffectedDisplayType.MENTION, Category.GUILD, Category.GUILD_MEMBER),
    GUILD_MEMBER_LEFT			                (MessageType.INFO, ModlogDisplayType.AFFECTED_THUMBNAIL, AffectedDisplayType.NAME, Category.GUILD, Category.GUILD_MEMBER),
    GUILD_MEMBER_KICKED			                (MessageType.WARNING, ModlogDisplayType.AFFECTED_THUMBNAIL, AffectedDisplayType.NAME, Category.GUILD, Category.GUILD_MEMBER, Category.MODERATION),
    GUILD_MEMBER_ROLE_ADDED		                (MessageType.WARNING, ModlogDisplayType.AFFECTED_AUTHOR, AffectedDisplayType.MENTION, Category.GUILD, Category.GUILD_MEMBER),
    GUILD_MEMBER_ROLE_REMOVED	                (MessageType.WARNING, ModlogDisplayType.AFFECTED_AUTHOR, AffectedDisplayType.MENTION, Category.GUILD, Category.GUILD_MEMBER),
    GUILD_MEMBER_NICKNAME_UPDATED	            (MessageType.INFO, ModlogDisplayType.AFFECTED_AUTHOR, AffectedDisplayType.MENTION, Category.GUILD, Category.GUILD_MEMBER),

    GUILD_USER_BANNED				            (MessageType.DANGER, ModlogDisplayType.AFFECTED_THUMBNAIL, AffectedDisplayType.NAME, Category.GUILD, Category.GUILD_MEMBER, Category.MODERATION),
    GUILD_USER_UNBANNED				            (MessageType.DANGER, ModlogDisplayType.AFFECTED_THUMBNAIL, AffectedDisplayType.NAME, Category.GUILD, Category.GUILD_MEMBER, Category.MODERATION),

    GUILD_MESSAGE_DELETED			            (MessageType.WARNING, ModlogDisplayType.AFFECTED_AUTHOR, AffectedDisplayType.MENTION, Category.GUILD, Category.GUILD_MESSAGE),
    GUILD_MESSAGE_DELETED_MENTION	            (MessageType.INFO, ModlogDisplayType.AFFECTED_AUTHOR, AffectedDisplayType.MENTION, Category.GUILD, Category.GUILD_MESSAGE),
    GUILD_MESSAGE_UPDATED		    	        (MessageType.INFO, ModlogDisplayType.AFFECTED_AUTHOR, AffectedDisplayType.MENTION, Category.GUILD, Category.GUILD_MESSAGE),

    GUILD_BOOST_COUNT_UPDATED		            (MessageType.INFO, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.GUILD, Category.GUILD_BOOST),
    GUILD_BOOST_TIER_UPDATED		            (MessageType.INFO, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.GUILD, Category.GUILD_BOOST),

    GUILD_UPDATE_AFK_CHANNEL		            (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_AFK_TIMEOUT		            (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_BANNER				            (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_DESCRIPTION		            (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_EXPLICIT_FILTER	            (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_FEATURES			            (MessageType.INFO, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_ICON				            (MessageType.WARNING, ModlogDisplayType.AFFECTED_THUMBNAIL, AffectedDisplayType.MENTION, Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_MAX_MEMBERS		            (MessageType.INFO, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_MAX_PRESENCES		            (MessageType.INFO, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_MFA_LEVEL			            (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_NAME				            (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_NOTIFICATION_LEVEL	            (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_REGION				            (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_SPLASH				            (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_SYSTEM_CHANNEL		            (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_VANITY_CODE		            (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.GUILD, Category.GUILD_SETTINGS),
    GUILD_UPDATE_VERIFICATION_LEVEL	            (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.GUILD, Category.GUILD_SETTINGS),

    VOICE_DEAFEN		       		            (MessageType.INFO, ModlogDisplayType.AFFECTED_AUTHOR, AffectedDisplayType.MENTION, Category.VOICE),
    VOICE_MUTE				                    (MessageType.INFO, ModlogDisplayType.AFFECTED_AUTHOR, AffectedDisplayType.MENTION, Category.VOICE),
    VOICE_SERVER_DEAFEN				            (MessageType.WARNING, ModlogDisplayType.AFFECTED_AUTHOR, AffectedDisplayType.MENTION, Category.VOICE, Category.MODERATION),
    VOICE_SERVER_MUTE				            (MessageType.WARNING, ModlogDisplayType.AFFECTED_AUTHOR, AffectedDisplayType.MENTION, Category.VOICE, Category.MODERATION),
    VOICE_JOIN			        	            (MessageType.INFO, ModlogDisplayType.AFFECTED_AUTHOR, AffectedDisplayType.MENTION, Category.VOICE),
    VOICE_LEAVE			        	            (MessageType.INFO, ModlogDisplayType.AFFECTED_AUTHOR, AffectedDisplayType.MENTION, Category.VOICE),
    VOICE_MOVE			        	            (MessageType.INFO, ModlogDisplayType.AFFECTED_AUTHOR, AffectedDisplayType.MENTION, Category.VOICE),
    VOICE_FORCE_MOVE				            (MessageType.WARNING, ModlogDisplayType.AFFECTED_AUTHOR, AffectedDisplayType.MENTION, Category.VOICE, Category.MODERATION),
    VOICE_DISCONNECT				            (MessageType.WARNING, ModlogDisplayType.AFFECTED_AUTHOR, AffectedDisplayType.MENTION, Category.VOICE, Category.MODERATION),

    ROLE_CREATED			       	            (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.ROLE),
    ROLE_DELETED			    	            (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.NAME, Category.ROLE),
    ROLE_COLOR_UPDATED				            (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.ROLE),
    ROLE_HOIST_UPDATED				            (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.ROLE),
    ROLE_MENTIONABLE_UPDATED		            (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.ROLE),
    ROLE_NAME_UPDATED				            (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.ROLE),
    ROLE_PERMISSIONS_UPDATED		            (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.ROLE),
    ROLE_POSITION_UPDATED			            (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.ROLE),

    USER_DISCRIMINATOR_UPDATED 		            (MessageType.INFO, ModlogDisplayType.AFFECTED_THUMBNAIL, AffectedDisplayType.MENTION, Category.USER),
    USER_NAME_UPDATED				            (MessageType.INFO, ModlogDisplayType.AFFECTED_THUMBNAIL, AffectedDisplayType.MENTION, Category.USER),

    CHANNEL_CREATED		    		            (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CHANNEL),
    CHANNEL_DELETED			    	            (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.NAME, Category.CHANNEL),
    CHANNEL_NAME_UPDATED			            (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CHANNEL),
    CHANNEL_PERMISSIONS_UPDATED		            (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CHANNEL),
    CHANNEL_POSITION_UPDATED		            (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CHANNEL),
    MULTIPLE_CHANNEL_POSITION_UPDATED           (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CHANNEL),
    CHANNEL_PARENT_UPDATED				        (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CHANNEL),

    VOICE_CHANNEL_BITRATE_UPDATED		        (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CHANNEL),
    VOICE_CHANNEL_USER_LIMIT_UPDATED	        (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CHANNEL),

    TEXT_CHANNEL_NSFW_UPDATED			        (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CHANNEL),
    TEXT_CHANNEL_SLOWMODE_UPDATED		        (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CHANNEL),
    TEXT_CHANNEL_TOPIC_UPDATED			        (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CHANNEL),

    CASCADE_PERMISSIONS_GROUP_CREATED	        (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CASCADE, Category.CASCADE_PERMISSIONS),
    CASCADE_PERMISSIONS_GROUP_DELETED	        (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CASCADE, Category.CASCADE_PERMISSIONS),
    CASCADE_PERMISSIONS_GROUP_PERMISSION_ADD    (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CASCADE, Category.CASCADE_PERMISSIONS),
    CASCADE_PERMISSIONS_GROUP_PERMISSION_REMOVE (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CASCADE, Category.CASCADE_PERMISSIONS),
    CASCADE_PERMISSIONS_GROUP_LINK		        (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CASCADE, Category.CASCADE_PERMISSIONS),
    CASCADE_PERMISSIONS_GROUP_UNLINK		    (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CASCADE, Category.CASCADE_PERMISSIONS),

    CASCADE_PERMISSIONS_USER_PERMISSION_ADD     (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CASCADE, Category.CASCADE_PERMISSIONS),
    CASCADE_PERMISSIONS_USER_PERMISSION_REMOVE  (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CASCADE, Category.CASCADE_PERMISSIONS),
    CASCADE_PERMISSIONS_USER_GROUP_ADD	        (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CASCADE, Category.CASCADE_PERMISSIONS),
    CASCADE_PERMISSIONS_USER_GROUP_REMOVE       (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CASCADE, Category.CASCADE_PERMISSIONS),

    CASCADE_SETTINGS_UPDATED			        (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CASCADE),

    CASCADE_MODULE_UPDATED				        (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CASCADE),

    CASCADE_COMMAND_RUN				            (MessageType.INFO, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CASCADE),

    CASCADE_PLAYLIST_CREATED			        (MessageType.INFO, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CASCADE),

    CASCADE_TEMP_MUTE				            (MessageType.DANGER, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CASCADE, Category.CASCADE_MODERATION, Category.MODERATION),
    CASCADE_TEMP_BAN				            (MessageType.DANGER, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CASCADE, Category.CASCADE_MODERATION, Category.MODERATION),
    CASCADE_SOFT_BAN				            (MessageType.DANGER, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CASCADE, Category.CASCADE_MODERATION, Category.MODERATION),
    CASCADE_PURGE				                (MessageType.DANGER, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CASCADE, Category.CASCADE_MODERATION, Category.MODERATION),

    CASCADE_WHITELIST				            (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CASCADE, Category.CASCADE_MODERATION, Category.MODERATION),
    CASCADE_BLACKLIST			    	        (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CASCADE, Category.CASCADE_MODERATION, Category.MODERATION),

    CASCADE_SCRIPT_CREATED				        (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CASCADE, Category.CASCADE_CUSTOM_COMMANDS),
    CASCADE_SCRIPT_DELETED				        (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CASCADE, Category.CASCADE_CUSTOM_COMMANDS),
    CASCADE_SCRIPT_UPDATED				        (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CASCADE, Category.CASCADE_CUSTOM_COMMANDS),

    CASCADE_TAG_CREATED			    	        (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CASCADE, Category.CASCADE_CUSTOM_COMMANDS),
    CASCADE_TAG_DELETED			    	        (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CASCADE, Category.CASCADE_CUSTOM_COMMANDS),
    CASCADE_TAG_UPDATED			    	        (MessageType.WARNING, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CASCADE, Category.CASCADE_CUSTOM_COMMANDS),
    // Here temporarily for testing purposes
    DEBUG                                       (MessageType.DANGER, ModlogDisplayType.PLAIN, AffectedDisplayType.MENTION, Category.CASCADE, Category.DEBUG);

    companion object {
        private val modlogCategoryMap: MutableMap<Category, MutableList<ModlogEvent>> = EnumMap(Category::class.java)

        fun getModlogCategoryMap(): Map<Category, MutableList<ModlogEvent>> {
            return modlogCategoryMap
        }

        fun getEventsFromCategory(category: Category): List<ModlogEvent> {
            return modlogCategoryMap[category]!!
        }

        init {
            for (cat in Category.values()) {
                modlogCategoryMap[cat] = mutableListOf()
            }
            modlogCategoryMap[Category.ALL] = mutableListOf(*values())
            for (event in values()) {
                for (category in event.categories) {
                    modlogCategoryMap[category]?.add(event)
                }
            }
        }
    }

    val categories: List<Category> = listOf(*categories)

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
        CASCADE_CUSTOM_COMMANDS,
        DEBUG
    }

    enum class ModlogDisplayType {
        AFFECTED_THUMBNAIL,
        AFFECTED_AUTHOR,
        PLAIN
    }

}