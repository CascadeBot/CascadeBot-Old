/*
 * Copyright (c) 2021 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils

import net.dv8tion.jda.api.audit.ActionType
import net.dv8tion.jda.api.audit.AuditLogEntry
import net.dv8tion.jda.api.entities.Guild
import java.time.Duration
import java.time.OffsetDateTime
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

object ModlogUtils {

    @JvmStatic
    fun getAuditLogFromType(guild: Guild, entryConsumer: Consumer<AuditLogEntry?>, vararg actionTypes: ActionType) {
        getAuditLogFromType(guild, -1, entryConsumer, *actionTypes)
    }

    @JvmStatic
    fun getAuditLogFromType(
        guild: Guild,
        targetId: Long,
        entryConsumer: Consumer<AuditLogEntry?>,
        vararg actionTypes: ActionType
    ) {
        guild.retrieveAuditLogs().limit(5 /* Get last 5 entries in case something else happens in the 500 ms */)
            .queueAfter(500, TimeUnit.MILLISECONDS)  // Wait 500ms so we have a better chance of it being in the modlog
            { auditLogEntries ->
                val startTime = OffsetDateTime.now()
                for (entry in auditLogEntries) {
                    val millis = Duration.between(entry.timeCreated, startTime).toMillis()
                    if (!actionTypes.contains(entry.type)) {
                        continue
                    }
                    if (millis > 5000L) {
                        continue
                    }
                    if (targetId != -1L) {
                        if (entry.targetIdLong != targetId) {
                            continue
                        }
                    }
                    entryConsumer.accept(entry)
                    return@queueAfter
                }
                entryConsumer.accept(null)
            }
    }
}
