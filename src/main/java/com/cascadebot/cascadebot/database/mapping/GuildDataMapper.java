/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.database.mapping;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.commandmeta.CommandManager;
import com.cascadebot.cascadebot.commandmeta.ICommand;
import com.cascadebot.cascadebot.database.DebugLogCallback;
import com.cascadebot.cascadebot.objects.GuildCommandInfo;
import com.cascadebot.cascadebot.objects.GuildData;
import com.cascadebot.cascadebot.utils.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.mongodb.client.model.Filters.eq;

public final class GuildDataMapper {

    public static final String COLLECTION = "guilds";

    public static void update(long id, Bson update) {
        CascadeBot.instance().getDatabaseManager().runAsyncTask(database -> {
            database.getCollection(COLLECTION).updateOne(eq("_id", id), update, new DebugLogCallback<>("Updated Guild ID " + id + ":", update));
        });
    }

    public static Document processGuildData(GuildData data) {
        Document guildDoc = new Document();

        guildDoc.put("_id", data.getGuildID());
        guildDoc.put("config_version", data.getConfigVersion());
        guildDoc.put("updated_at", new Date());

        Document config = new Document();
        config.put("mention_prefix", data.isMentionPrefix());

        Document commands = new Document();
        for (GuildCommandInfo commandInfo : data.getGuildCommandInfos()) {
            commands.put(commandInfo.getDefaultCommand(), processCommandInfo(commandInfo));
        }
        config.put("commands", commands);

        guildDoc.put("config", config);
        return guildDoc;
    }

    public static Document processCommandInfo(GuildCommandInfo commandInfo) {
        Document commandDoc = new Document();
        commandDoc.put("command", commandInfo.getCommand());
        commandDoc.put("enabled", commandInfo.isEnabled());
        commandDoc.put("aliases", commandInfo.getAliases());
        return commandDoc;
    }

    public static GuildData documentToGuildData(Document document) {
        GuildData.GuildDataBuilder guildDataBuilder = new GuildData.GuildDataBuilder(document.getLong("_id"));
        guildDataBuilder.setConfigVersion(document.getString("config_version"));
        guildDataBuilder.setCreationDate(document.getDate("created_at"));

        Document config = document.get("config", Document.class);
        guildDataBuilder.setMentionPrefix(config.getBoolean("mention_prefix"));

        Document commands = config.get("commands", Document.class);
        for (String key : commands.keySet()) {
            Pair<ICommand, GuildCommandInfo> pair = documentToCommand(key, commands.get(key, Document.class));
            guildDataBuilder.addCommand(pair.getKey(), pair.getValue());
        }
        return guildDataBuilder.build();
    }

    public static Pair<ICommand, GuildCommandInfo> documentToCommand(String defaultCommand, Document document) {
        ICommand command = CommandManager.instance().getCommandByDefault(defaultCommand);
        return Pair.of(command, new GuildCommandInfo(
                document.getString("command"),
                defaultCommand,
                CollectionUtils.hashSet(document.get("aliases", List.class)),
                document.getBoolean("enabled"),
                command.forceDefault()
        ));
    }

}
