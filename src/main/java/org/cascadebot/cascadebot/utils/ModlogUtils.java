package org.cascadebot.cascadebot.utils;

import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ModlogUtils {

    public static void getAuditLogFromType(Guild guild, Consumer<AuditLogEntry> entryConsumer, ActionType... actionTypes) {
        getAuditLogFromType(guild, -1, entryConsumer, actionTypes);
    }

    public static void getAuditLogFromType(Guild guild, long targetId, Consumer<AuditLogEntry> entryConsumer, ActionType... actionTypes) {
        List<ActionType> actionTypeList = Arrays.asList(actionTypes);
        guild.retrieveAuditLogs().limit(5 /* Get last 5 entries in case something else happens in the 500 ms */)
                .queueAfter(500, TimeUnit.MILLISECONDS, // Wait 500ms so we have a better chance of it being in the modlog
                        auditLogEntries -> {
            for (AuditLogEntry entry : auditLogEntries) {
                long millis = Duration.between(entry.getTimeCreated(), OffsetDateTime.now()).toMillis();
                if (!actionTypeList.contains(entry.getType())) {
                    continue;
                }
                if (millis > 1000l) {
                    continue;
                }
                if (targetId != -1) {
                    if (entry.getTargetIdLong() != targetId) {
                        continue;
                    }
                }
                entryConsumer.accept(entry);
                return;
            }
            entryConsumer.accept(null);
        });
    }

}
