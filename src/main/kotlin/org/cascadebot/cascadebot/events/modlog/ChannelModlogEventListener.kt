/*
 * Copyright (c) 2021 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.events.modlog

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.audit.ActionType
import net.dv8tion.jda.api.audit.AuditLogEntry
import net.dv8tion.jda.api.entities.Category
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.GuildChannel
import net.dv8tion.jda.api.entities.IPermissionHolder
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.channel.category.CategoryCreateEvent
import net.dv8tion.jda.api.events.channel.category.CategoryDeleteEvent
import net.dv8tion.jda.api.events.channel.category.GenericCategoryEvent
import net.dv8tion.jda.api.events.channel.category.update.CategoryUpdateNameEvent
import net.dv8tion.jda.api.events.channel.category.update.CategoryUpdatePositionEvent
import net.dv8tion.jda.api.events.channel.store.GenericStoreChannelEvent
import net.dv8tion.jda.api.events.channel.store.StoreChannelCreateEvent
import net.dv8tion.jda.api.events.channel.store.StoreChannelDeleteEvent
import net.dv8tion.jda.api.events.channel.store.update.StoreChannelUpdateNameEvent
import net.dv8tion.jda.api.events.channel.store.update.StoreChannelUpdatePositionEvent
import net.dv8tion.jda.api.events.channel.text.GenericTextChannelEvent
import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent
import net.dv8tion.jda.api.events.channel.text.update.TextChannelUpdateNSFWEvent
import net.dv8tion.jda.api.events.channel.text.update.TextChannelUpdateNameEvent
import net.dv8tion.jda.api.events.channel.text.update.TextChannelUpdateParentEvent
import net.dv8tion.jda.api.events.channel.text.update.TextChannelUpdatePositionEvent
import net.dv8tion.jda.api.events.channel.text.update.TextChannelUpdateSlowmodeEvent
import net.dv8tion.jda.api.events.channel.text.update.TextChannelUpdateTopicEvent
import net.dv8tion.jda.api.events.channel.voice.GenericVoiceChannelEvent
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelCreateEvent
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelDeleteEvent
import net.dv8tion.jda.api.events.channel.voice.update.VoiceChannelUpdateBitrateEvent
import net.dv8tion.jda.api.events.channel.voice.update.VoiceChannelUpdateNameEvent
import net.dv8tion.jda.api.events.channel.voice.update.VoiceChannelUpdateParentEvent
import net.dv8tion.jda.api.events.channel.voice.update.VoiceChannelUpdatePositionEvent
import net.dv8tion.jda.api.events.channel.voice.update.VoiceChannelUpdateUserLimitEvent
import net.dv8tion.jda.api.events.guild.override.GenericPermissionOverrideEvent
import net.dv8tion.jda.api.events.guild.override.PermissionOverrideCreateEvent
import net.dv8tion.jda.api.events.guild.override.PermissionOverrideDeleteEvent
import net.dv8tion.jda.api.events.guild.override.PermissionOverrideUpdateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.apache.commons.lang3.StringUtils
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.UnicodeConstants
import org.cascadebot.cascadebot.data.Config
import org.cascadebot.cascadebot.data.language.Language.i18n
import org.cascadebot.cascadebot.data.managers.GuildDataManager
import org.cascadebot.cascadebot.data.objects.ModlogEventData
import org.cascadebot.cascadebot.events.EventCollector
import org.cascadebot.cascadebot.moderation.ModlogEmbedField
import org.cascadebot.cascadebot.moderation.ModlogEmbedPart
import org.cascadebot.cascadebot.moderation.ModlogEvent
import org.cascadebot.cascadebot.utils.DiscordUtils
import org.cascadebot.cascadebot.utils.ModlogUtils.getAuditLogFromType
import org.cascadebot.shared.utils.ThreadPoolExecutorLogged
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Consumer
import kotlin.math.abs

class ChannelModlogEventListener : ListenerAdapter() {

    private val moveRunnableMap: MutableMap<Long, EventCollector<ChannelMoveData>> = HashMap()

    private val threadCounter = AtomicInteger(0)
    private val channelEventCollectorPool = ThreadPoolExecutorLogged.newCachedThreadPool({ runnable: Runnable? ->
        Thread(
            runnable,
            "channel-event-collector-" + threadCounter.getAndIncrement()
        )
    }, CascadeBot.LOGGER)

    //region Channels
    override fun onGenericStoreChannel(event: GenericStoreChannelEvent) {
        when (event) {
            is StoreChannelCreateEvent -> {
                handleChannelCreateEvents(event.getChannel().guild, ChannelType.STORE, event.getChannel())
            }

            is StoreChannelDeleteEvent -> {
                handleChannelDeleteEvents(event.getChannel().guild, ChannelType.STORE, event.getChannel())
            }

            is StoreChannelUpdateNameEvent -> {
                handleChannelUpdateNameEvents(
                    event.getChannel().guild,
                    ChannelType.STORE,
                    event.oldName,
                    event.getChannel()
                )
            }

            is StoreChannelUpdatePositionEvent -> {
                handleChannelUpdatePositionEvents(
                    event.getChannel().guild,
                    ChannelType.STORE,
                    event.oldPosition,
                    event.getChannel()
                )
            }
        }
    }

    override fun onGenericTextChannel(event: GenericTextChannelEvent) {
        when (event) {
            is TextChannelCreateEvent -> {
                handleChannelCreateEvents(event.getGuild(), ChannelType.TEXT, event.getChannel())
                return
            }

            is TextChannelDeleteEvent -> {
                handleChannelDeleteEvents(event.getGuild(), ChannelType.TEXT, event.getChannel())
                return
            }

            is TextChannelUpdateNameEvent -> {
                handleChannelUpdateNameEvents(event.getGuild(), ChannelType.TEXT, event.oldName, event.getChannel())
                return
            }

            is TextChannelUpdatePositionEvent -> {
                handleChannelUpdatePositionEvents(
                    event.getGuild(),
                    ChannelType.TEXT,
                    event.oldPosition,
                    event.getChannel()
                )
                return
            }

            is TextChannelUpdateParentEvent -> {
                handleChannelUpdateParentEvents(
                    event.getGuild(),
                    ChannelType.TEXT,
                    event.oldParent,
                    event.newParent,
                    event.getChannel()
                )
                return
            }

            else -> {
                val guildData = GuildDataManager.getGuildData(event.guild.idLong)
                getAuditLogFromType(
                    event.guild, event.channel.idLong,
                    Consumer { auditLogEntry: AuditLogEntry? ->
                        val trigger: ModlogEvent
                        val embedFieldList: MutableList<ModlogEmbedPart> = mutableListOf()
                        var descriptionParts: MutableList<String> = mutableListOf()
                        var responsible: User? = null
                        if (auditLogEntry != null) {
                            responsible = auditLogEntry.user
                        } else {
                            CascadeBot.LOGGER.warn("Modlog: Failed to find channel update entry")
                        }
                        when (event) {
                            is TextChannelUpdateNSFWEvent -> {
                                trigger = ModlogEvent.TEXT_CHANNEL_NSFW_UPDATED
                                val newValue = event.newValue
                                val emote = if (newValue != null && newValue) CascadeBot.INS.shardManager.getEmoteById(
                                    Config.INS.globalEmotes["tick"]!!
                                ) else CascadeBot.INS.shardManager.getEmoteById(
                                    Config.INS.globalEmotes["cross"]!!
                                )
                                descriptionParts = mutableListOf(
                                    emote?.asMention ?: "",
                                    newValue.toString()
                                )
                            }

                            is TextChannelUpdateSlowmodeEvent -> {
                                trigger = ModlogEvent.TEXT_CHANNEL_SLOWMODE_UPDATED
                                embedFieldList.add(
                                    ModlogEmbedField(
                                        false,
                                        "modlog.channel.slowmode",
                                        "modlog.general.small_change",
                                        event.oldSlowmode,
                                        event.newSlowmode
                                    )
                                )
                            }

                            is TextChannelUpdateTopicEvent -> {
                                trigger = ModlogEvent.TEXT_CHANNEL_TOPIC_UPDATED
                                val oldTopic = event.oldTopic
                                val newTopic = event.newTopic
                                embedFieldList.add(
                                    ModlogEmbedField(
                                        false,
                                        "modlog.channel.old_topic",
                                        null,
                                        if (StringUtils.isBlank(oldTopic)) "-" else oldTopic!!
                                    )
                                )
                                embedFieldList.add(
                                    ModlogEmbedField(
                                        false,
                                        "modlog.channel.new_topic",
                                        null,
                                        if (StringUtils.isBlank(newTopic)) "-" else newTopic!!
                                    )
                                )
                            }

                            else -> {
                                return@Consumer
                            }
                        }
                        val eventStore = ModlogEventData(trigger, responsible, event.channel, embedFieldList)
                        eventStore.extraDescriptionInfo = descriptionParts
                        guildData.moderation.sendModlogEvent(event.guild.idLong, eventStore)
                    }, ActionType.CHANNEL_UPDATE
                )
            }
        }
    }

    override fun onGenericVoiceChannel(event: GenericVoiceChannelEvent) {
        when (event) {
            is VoiceChannelCreateEvent -> {
                handleChannelCreateEvents(event.getGuild(), ChannelType.VOICE, event.getChannel())
            }

            is VoiceChannelDeleteEvent -> {
                handleChannelDeleteEvents(event.getGuild(), ChannelType.VOICE, event.getChannel())
            }

            is VoiceChannelUpdateNameEvent -> {
                handleChannelUpdateNameEvents(event.getGuild(), ChannelType.VOICE, event.oldName, event.getChannel())
            }

            is VoiceChannelUpdatePositionEvent -> {
                handleChannelUpdatePositionEvents(
                    event.getGuild(),
                    ChannelType.VOICE,
                    event.oldPosition,
                    event.getChannel()
                )
            }

            is VoiceChannelUpdateParentEvent -> {
                handleChannelUpdateParentEvents(
                    event.getGuild(),
                    ChannelType.VOICE,
                    event.oldParent,
                    event.newParent,
                    event.getChannel()
                )
            }
        }

        val guildData = GuildDataManager.getGuildData(event.guild.idLong)
        getAuditLogFromType(
            event.guild, event.channel.idLong,
            Consumer { auditLogEntry: AuditLogEntry? ->
                val trigger: ModlogEvent
                val embedFieldList: MutableList<ModlogEmbedPart> = ArrayList()
                var responsible: User? = null
                if (auditLogEntry != null) {
                    responsible = auditLogEntry.user
                } else {
                    CascadeBot.LOGGER.warn("Modlog: Failed to find channel update entry")
                }
                trigger = when (event) {
                    is VoiceChannelUpdateBitrateEvent -> {
                        val oldBitrate = i18n(
                            event.getGuild().idLong,
                            "modlog.channel.kbps",
                            event.oldBitrate / 1000
                        )
                        val newBitrate = i18n(
                            event.getGuild().idLong,
                            "modlog.channel.kbps",
                            event.newBitrate / 1000
                        )
                        embedFieldList.add(
                            ModlogEmbedField(
                                false,
                                "modlog.channel.bitrate",
                                "modlog.general.small_change",
                                oldBitrate,
                                newBitrate
                            )
                        )
                        ModlogEvent.VOICE_CHANNEL_BITRATE_UPDATED
                    }

                    is VoiceChannelUpdateUserLimitEvent -> {
                        val oldLimit =
                            if (event.oldUserLimit == 0) UnicodeConstants.INFINITY_SYMBOL else event.oldUserLimit
                                .toString()
                        val newLimit =
                            if (event.newUserLimit == 0) UnicodeConstants.INFINITY_SYMBOL else event.newUserLimit
                                .toString()
                        embedFieldList.add(
                            ModlogEmbedField(
                                false,
                                "modlog.channel.users",
                                "modlog.general.small_change",
                                oldLimit,
                                newLimit
                            )
                        )
                        ModlogEvent.VOICE_CHANNEL_USER_LIMIT_UPDATED
                    }

                    else -> {
                        return@Consumer
                    }
                }
                val eventStore = ModlogEventData(trigger, responsible, event.channel, embedFieldList)
                guildData.moderation.sendModlogEvent(event.guild.idLong, eventStore)
            }, ActionType.CHANNEL_UPDATE
        )
    }

    override fun onGenericCategory(event: GenericCategoryEvent) {
        if (event is CategoryCreateEvent) {
            handleChannelCreateEvents(event.getGuild(), ChannelType.CATEGORY, event.getCategory())
        } else if (event is CategoryDeleteEvent) {
            handleChannelDeleteEvents(event.getGuild(), ChannelType.CATEGORY, event.getCategory())
        } else if (event is CategoryUpdateNameEvent) {
            handleChannelUpdateNameEvents(event.getGuild(), ChannelType.CATEGORY, event.oldName, event.getCategory())
        } else if (event is CategoryUpdatePositionEvent) {
            handleChannelUpdatePositionEvents(
                event.getGuild(),
                ChannelType.CATEGORY,
                event.oldPosition,
                event.getCategory()
            )
        }
    }

    override fun onGenericPermissionOverride(event: GenericPermissionOverrideEvent) {
        val guild = event.guild
        val guildData = GuildDataManager.getGuildData(guild.idLong)
        getAuditLogFromType(
            event.guild, event.channel.idLong,
            Consumer { auditLogEntry: AuditLogEntry? ->
                val embedFieldList: MutableList<ModlogEmbedPart> = ArrayList()
                val modlogEvent: ModlogEvent
                var responsible: User? = null
                if (auditLogEntry != null) {
                    responsible = auditLogEntry.user
                } else {
                    CascadeBot.LOGGER.warn("Modlog: Failed to find channel update entry")
                }
                when (event) {
                    is PermissionOverrideCreateEvent -> {
                        modlogEvent = ModlogEvent.DUMMY
                    }

                    is PermissionOverrideDeleteEvent -> {
                        modlogEvent = ModlogEvent.DUMMY
                    }

                    is PermissionOverrideUpdateEvent -> {
                        modlogEvent = ModlogEvent.CHANNEL_PERMISSIONS_UPDATED
                        val stringBuilder = StringBuilder()
                        for (permission in event.oldDeny) {
                            val current =
                                getCurrentPermissionStateForHolder(
                                    permission,
                                    event.getChannel(),
                                    event.getPermissionHolder()
                                )
                            if (current != null && current != OverrideStatus.DENY) {
                                stringBuilder.append(OverrideStatus.DENY.emote)
                                    .append(" ➜ ")
                                    .append(current.emote)
                                    .append(": ")
                                    .append(permission.getName())
                                    .append('\n')
                            }
                        }
                        for (permission in event.oldInherited) {
                            val current =
                                getCurrentPermissionStateForHolder(
                                    permission,
                                    event.getChannel(),
                                    event.getPermissionHolder()
                                )
                            if (current != null && current != OverrideStatus.INHERIT) {
                                stringBuilder.append(OverrideStatus.INHERIT.emote)
                                    .append(" ➜ ")
                                    .append(current.emote)
                                    .append(": ")
                                    .append(permission.getName())
                                    .append('\n')
                            }
                        }
                        for (permission in event.oldAllow) {
                            val current =
                                getCurrentPermissionStateForHolder(
                                    permission,
                                    event.getChannel(),
                                    event.getPermissionHolder()
                                )
                            if (current != null && current != OverrideStatus.ALLOW) {
                                stringBuilder.append(OverrideStatus.ALLOW.emote)
                                    .append(" ➜ ")
                                    .append(current.emote)
                                    .append(": ")
                                    .append(permission.getName())
                                    .append('\n')
                            }
                        }
                        embedFieldList.add(
                            ModlogEmbedField(
                                false,
                                "modlog.channel.perm.changed",
                                "modlog.general.variable",
                                stringBuilder.toString()
                            )
                        )
                    }

                    else -> {
                        return@Consumer
                    }
                }
                val modlogEventData = ModlogEventData(modlogEvent, responsible, event.channel, embedFieldList)
                var holderMention = ""
                if (event.permissionHolder is User) {
                    holderMention = (event.permissionHolder as User?)!!.asMention
                } else if (event.permissionHolder is Role) {
                    holderMention = (event.permissionHolder as Role?)!!.asMention
                }
                modlogEventData.extraDescriptionInfo = mutableListOf(holderMention)
                guildData.moderation.sendModlogEvent(event.guild.idLong, modlogEventData)
            }, ActionType.CHANNEL_OVERRIDE_UPDATE, ActionType.CHANNEL_UPDATE
        )
    }

    private fun getCurrentPermissionStateForHolder(
        permission: Permission,
        channel: GuildChannel,
        holder: IPermissionHolder?
    ): OverrideStatus? {
        val permissionOverride = channel.getPermissionOverride(holder!!) ?: return null
        return when {
            permissionOverride.allowed.contains(permission) -> {
                OverrideStatus.ALLOW
            }

            permissionOverride.inherit.contains(permission) -> {
                OverrideStatus.INHERIT
            }

            permissionOverride.denied.contains(permission) -> {
                OverrideStatus.DENY
            }

            else -> {
                null
            }
        }
    }

    enum class OverrideStatus(val emoteName: String, val text: String) {
        ALLOW("perm_tick", "+"),
        DENY("perm_cross", "-"),
        INHERIT("perm_neutral", "\\\\");

        val emote: String by lazy {
            val emoteId = Config.INS.globalEmotes[emoteName] ?: return@lazy text
            val emote = CascadeBot.INS.shardManager.getEmoteById(emoteId) ?: return@lazy text
            return@lazy emote.asMention
        }
    }

    //endregion

    //endregion
    //region Channel handlers
    private fun handleChannelCreateEvents(guild: Guild, type: ChannelType, channel: GuildChannel) {
        val event = ModlogEvent.CHANNEL_CREATED
        val guildData = GuildDataManager.getGuildData(guild.idLong)
        getAuditLogFromType(guild, channel.idLong, Consumer { auditLogEntry: AuditLogEntry? ->
            val embedFieldList: MutableList<ModlogEmbedPart> = ArrayList()
            var responsible: User? = null
            if (auditLogEntry != null) {
                responsible = auditLogEntry.user
            } else {
                CascadeBot.LOGGER.warn("Modlog: Failed to find channel create entry")
            }
            embedFieldList.add(
                ModlogEmbedField(
                    false,
                    "modlog.channel.type.name",
                    "modlog.channel.type." + type.name.toLowerCase()
                )
            )
            val modlogEventData = ModlogEventData(event, responsible, channel, embedFieldList)
            modlogEventData.extraDescriptionInfo = mutableListOf(type.name.toLowerCase())
            guildData.moderation.sendModlogEvent(guild.idLong, modlogEventData)
        }, ActionType.CHANNEL_CREATE)
    }

    private fun handleChannelDeleteEvents(guild: Guild, type: ChannelType, channel: GuildChannel) {
        val event = ModlogEvent.CHANNEL_DELETED
        val guildData = GuildDataManager.getGuildData(guild.idLong)
        getAuditLogFromType(guild, channel.idLong, Consumer { auditLogEntry: AuditLogEntry? ->
            val embedFieldList: MutableList<ModlogEmbedPart> = ArrayList()
            var responsible: User? = null
            if (auditLogEntry != null) {
                responsible = auditLogEntry.user
            } else {
                CascadeBot.LOGGER.warn("Modlog: Failed to find channel delete entry")
            }
            embedFieldList.add(
                ModlogEmbedField(
                    false,
                    "modlog.channel.type.name",
                    "modlog.channel.type." + type.name.toLowerCase()
                )
            )
            val modlogEventData = ModlogEventData(event, responsible, channel, embedFieldList)
            modlogEventData.extraDescriptionInfo = mutableListOf(type.name.toLowerCase())
            guildData.moderation.sendModlogEvent(guild.idLong, modlogEventData)
        }, ActionType.CHANNEL_DELETE)
    }

    private fun handleChannelUpdateNameEvents(guild: Guild, type: ChannelType, oldName: String, channel: GuildChannel) {
        val event = ModlogEvent.CHANNEL_NAME_UPDATED
        val guildData = GuildDataManager.getGuildData(guild.idLong)
        getAuditLogFromType(guild, channel.idLong, Consumer { auditLogEntry: AuditLogEntry? ->
            val embedFieldList: MutableList<ModlogEmbedPart> = ArrayList()
            var responsible: User? = null
            if (auditLogEntry != null) {
                responsible = auditLogEntry.user
            } else {
                CascadeBot.LOGGER.warn("Modlog: Failed to find channel update entry")
            }
            embedFieldList.add(
                ModlogEmbedField(
                    true,
                    "modlog.general.name",
                    "modlog.general.small_change",
                    oldName,
                    channel.name
                )
            )
            val modlogEventData = ModlogEventData(event, responsible, channel, embedFieldList)
            guildData.moderation.sendModlogEvent(guild.idLong, modlogEventData)
        }, ActionType.CHANNEL_UPDATE)
    }

    private fun handleChannelUpdatePositionEvents(guild: Guild, type: ChannelType, oldPos: Int, channel: GuildChannel) {
        val moveData = ChannelMoveData(type, oldPos, channel)
        val channelCollector = moveRunnableMap[guild.idLong]
        if (channelCollector != null) {
            channelCollector.queue.add(moveData)
        } else {
            val moveCollector = EventCollector(
                guild,
                { callbackGuild: Guild, channelMoveDataList: List<ChannelMoveData> ->
                    channelMove(
                        callbackGuild,
                        channelMoveDataList
                    )
                }, 500
            )
            moveCollector.queue.add(moveData)
            moveRunnableMap[guild.idLong] = moveCollector
            channelEventCollectorPool.submit(moveCollector)
        }
    }

    private fun channelMove(guild: Guild, channelMoveDataList: List<ChannelMoveData>) {
        moveRunnableMap.remove(guild.idLong)
        getAuditLogFromType(guild, Consumer { auditLogEntry: AuditLogEntry? ->
            var responsible: User? = null
            if (auditLogEntry != null) {
                responsible = auditLogEntry.user
            }
            var maxDistance = 0
            var maxMoveData: MutableList<ChannelMoveData> = ArrayList()
            for (moveData in channelMoveDataList) {
                val distance = abs(moveData.channel.position - moveData.oldPos)
                if (distance == maxDistance) {
                    maxMoveData.add(moveData)
                } else if (distance > maxDistance) {
                    maxMoveData = ArrayList()
                    maxDistance = distance
                    maxMoveData.add(moveData)
                }
            }
            val guildData = GuildDataManager.getGuildData(guild.idLong)
            val embedParts: MutableList<ModlogEmbedPart> = ArrayList()
            for (data in maxMoveData) {
                val field = ModlogEmbedField(
                    false, "modlog.channel.position.title",
                    "modlog.general.small_change",
                    DiscordUtils.calcChannelPosition(data.oldPos, data.channel.type, data.channel.guild),
                    DiscordUtils.calcChannelPosition(data.channel)
                )
                field.addTitleObjects(data.channel.name)
                embedParts.add(field)
            }
            val event =
                if (maxMoveData.size <= 1) ModlogEvent.CHANNEL_POSITION_UPDATED else ModlogEvent.MULTIPLE_CHANNEL_POSITION_UPDATED
            val eventStore = ModlogEventData(event, responsible, maxMoveData[0].channel, embedParts)
            guildData.moderation.sendModlogEvent(guild.idLong, eventStore)
        }, ActionType.CHANNEL_UPDATE)
    }

    class ChannelMoveData(val type: ChannelType, val oldPos: Int, val channel: GuildChannel)

    private fun handleChannelUpdateParentEvents(
        guild: Guild,
        type: ChannelType,
        oldParent: Category?,
        newParent: Category?,
        channel: GuildChannel
    ) {
        val event = ModlogEvent.CHANNEL_PARENT_UPDATED
        val guildData = GuildDataManager.getGuildData(guild.idLong)
        getAuditLogFromType(guild, channel.idLong, Consumer { auditLogEntry: AuditLogEntry? ->
            val embedFieldList: MutableList<ModlogEmbedPart> = java.util.ArrayList()
            var responsible: User? = null
            if (auditLogEntry != null) {
                responsible = auditLogEntry.user
            } else {
                CascadeBot.LOGGER.warn("Modlog: Failed to find channel update entry")
            }
            embedFieldList.add(
                ModlogEmbedField(
                    true,
                    "modlog.channel.parent",
                    "modlog.general.small_change",
                    oldParent?.name ?: "None",
                    newParent?.name ?: "None"
                )
            ) // TODO language string for none
            val modlogEventData = ModlogEventData(event, responsible, channel, embedFieldList)
            guildData.moderation.sendModlogEvent(guild.idLong, modlogEventData)
        }, ActionType.CHANNEL_UPDATE)
    }

}