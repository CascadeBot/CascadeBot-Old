/*
 * Copyright (c) 2021 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.events.modlog

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.audit.ActionType
import net.dv8tion.jda.api.audit.AuditLogEntry
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.role.GenericRoleEvent
import net.dv8tion.jda.api.events.role.RoleCreateEvent
import net.dv8tion.jda.api.events.role.RoleDeleteEvent
import net.dv8tion.jda.api.events.role.update.RoleUpdateColorEvent
import net.dv8tion.jda.api.events.role.update.RoleUpdateHoistedEvent
import net.dv8tion.jda.api.events.role.update.RoleUpdateMentionableEvent
import net.dv8tion.jda.api.events.role.update.RoleUpdateNameEvent
import net.dv8tion.jda.api.events.role.update.RoleUpdatePermissionsEvent
import net.dv8tion.jda.api.events.role.update.RoleUpdatePositionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.data.Config
import org.cascadebot.cascadebot.data.managers.GuildDataManager
import org.cascadebot.cascadebot.data.objects.ModlogEventData
import org.cascadebot.cascadebot.moderation.ModlogEmbedField
import org.cascadebot.cascadebot.moderation.ModlogEmbedPart
import org.cascadebot.cascadebot.moderation.ModlogEvent
import org.cascadebot.cascadebot.utils.ColorUtils.getHex
import org.cascadebot.cascadebot.utils.ModlogUtils.getAuditLogFromType
import org.cascadebot.cascadebot.utils.lists.CollectionDiff
import java.util.function.Consumer
import java.util.stream.Collectors

class RoleModlogEventListener : ListenerAdapter() {

    override fun onGenericRole(event: GenericRoleEvent) {
        val guildData = GuildDataManager.getGuildData(event.guild.idLong)
        getAuditLogFromType(event.guild, event.role.idLong,
            Consumer { auditLogEntry: AuditLogEntry? ->
                val embedFieldList: MutableList<ModlogEmbedPart> = ArrayList()
                val descriptionStuff: MutableList<String> = ArrayList()
                var responsible: User? = null
                val modlogEvent: ModlogEvent
                if (auditLogEntry != null) {
                    responsible = auditLogEntry.user
                } else {
                    CascadeBot.LOGGER.warn("Modlog: Failed to find role audit log entry")
                }
                val affected = event.role
                when (event) {
                    is RoleCreateEvent -> {
                        modlogEvent = ModlogEvent.ROLE_CREATED
                    }

                    is RoleDeleteEvent -> {
                        modlogEvent = ModlogEvent.ROLE_DELETED
                    }

                    is RoleUpdateColorEvent -> {
                        modlogEvent = ModlogEvent.ROLE_COLOR_UPDATED
                        roleColorUpdateEvent(event, embedFieldList)
                    }

                    is RoleUpdateHoistedEvent -> {
                        modlogEvent = ModlogEvent.ROLE_HOIST_UPDATED
                        roleHoistedUpdateEvent(event, descriptionStuff)
                    }

                    is RoleUpdateMentionableEvent -> {
                        modlogEvent = ModlogEvent.ROLE_MENTIONABLE_UPDATED
                        roleMentionableUpdateEvent(event, descriptionStuff)
                    }

                    is RoleUpdateNameEvent -> {
                        modlogEvent = ModlogEvent.ROLE_NAME_UPDATED
                        roleNameUpdateEvent(event, embedFieldList)
                    }

                    is RoleUpdatePermissionsEvent -> {
                        modlogEvent = ModlogEvent.ROLE_PERMISSIONS_UPDATED
                        rolePermissionUpdateEvent(event, embedFieldList)
                    }

                    is RoleUpdatePositionEvent -> {
                        modlogEvent = ModlogEvent.ROLE_POSITION_UPDATED
                        roleUpdatePositionEvent(event, embedFieldList)
                    }

                    else -> {
                        return@Consumer
                    }
                }
                val modlogEventData = ModlogEventData(modlogEvent, responsible, affected, embedFieldList)
                modlogEventData.extraDescriptionInfo = descriptionStuff
                guildData.moderation.sendModlogEvent(event.guild.idLong, modlogEventData)
            }, ActionType.ROLE_CREATE, ActionType.ROLE_DELETE, ActionType.ROLE_UPDATE
        )
    }

    private fun roleMentionableUpdateEvent(
        event: RoleUpdateMentionableEvent,
        descriptionStuff: MutableList<String>
    ) {
        val emote =
            if (event.newValue) CascadeBot.INS.shardManager
                .getEmoteById(
                    Config.INS.globalEmotes["tick"]!!
                ) else CascadeBot.INS.shardManager
                .getEmoteById(Config.INS.globalEmotes["cross"]!!)
        descriptionStuff.addAll(
            mutableListOf(
                emote?.asMention ?: "",
                event.newValue.toString()
            )
        )
    }

    private fun roleHoistedUpdateEvent(
        event: RoleUpdateHoistedEvent,
        descriptionStuff: MutableList<String>
    ) {
        val wasHoisted = event.wasHoisted()
        val emote = if (!wasHoisted) CascadeBot.INS.shardManager.getEmoteById(
            Config.INS.globalEmotes["tick"]!!
        ) else CascadeBot.INS.shardManager.getEmoteById(
            Config.INS.globalEmotes["cross"]!!
        )
        descriptionStuff.addAll(
            listOf(
                emote?.asMention ?: "",
                (!wasHoisted).toString()
            )
        )
    }

    private fun roleNameUpdateEvent(
        event: RoleUpdateNameEvent,
        embedFieldList: MutableList<ModlogEmbedPart>
    ) {
        embedFieldList.add(
            ModlogEmbedField(
                false,
                "modlog.general.old_name",
                null,
                event.oldName
            )
        )
        embedFieldList.add(
            ModlogEmbedField(
                false,
                "modlog.general.new_name",
                null,
                event.newValue
            )
        )
    }

    private fun roleColorUpdateEvent(
        event: RoleUpdateColorEvent,
        embedFieldList: MutableList<ModlogEmbedPart>
    ) {
        val oldColor = event.oldColor
        val newColor = event.newColor
        embedFieldList.add(
            ModlogEmbedField(
                false,
                "modlog.role.old_color",
                if (oldColor != null) null else "words.default",
                if (oldColor != null) getHex(
                    oldColor.red,
                    oldColor.green,
                    oldColor.blue
                ) else "-"
            )
        )
        embedFieldList.add(
            ModlogEmbedField(
                false,
                "modlog.role.new_color",
                if (newColor != null) null else "words.default",
                if (newColor != null) getHex(
                    newColor.red,
                    newColor.green,
                    newColor.blue
                ) else "-"
            )
        )
    }

    private fun rolePermissionUpdateEvent(
        event: RoleUpdatePermissionsEvent,
        embedFieldList: MutableList<ModlogEmbedPart>
    ) {
        val oldPermissions =
            event.oldPermissions
        val newPermissions =
            event.newPermissions
        val permissionListChanges =
            CollectionDiff(oldPermissions, newPermissions)
        if (permissionListChanges.added.isNotEmpty()) {
            embedFieldList.add(ModlogEmbedField(false,
                "modlog.role.added_perm",
                null,
                permissionListChanges.added.stream().map { obj: Permission -> obj.getName() }
                    .collect(Collectors.joining("\n"))
            )
            )
        }
        if (permissionListChanges.removed.isNotEmpty()) {
            embedFieldList.add(ModlogEmbedField(false,
                "modlog.role.removed_perm",
                null,
                permissionListChanges.removed.stream().map { obj: Permission -> obj.getName() }
                    .collect(Collectors.joining("\n"))
            )
            )
        }
    }

    private fun roleUpdatePositionEvent(
        event: RoleUpdatePositionEvent,
        embedFieldList: MutableList<ModlogEmbedPart>
    ) {
        if (event.newPosition == event.oldPosition) {
            // If the position stays the same, we have no reason to log the event
            return
        }
        embedFieldList.add(
            ModlogEmbedField(
                false,
                "modlog.general.position",
                "modlog.general.small_change",
                event.oldPosition + 1,
                event.newPosition + 1
            )
        )
    }

}