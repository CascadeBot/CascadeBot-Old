/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.data.mapping;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.data.database.DebugLogCallback;
import com.cascadebot.cascadebot.data.objects.GuildCommandInfo;
import com.cascadebot.cascadebot.data.objects.GuildData;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.RemovalCause;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.eq;

public final class GuildDataMapper {

    public static final String COLLECTION = "guilds";

    private static LoadingCache<Long, GuildData> guilds = Caffeine.newBuilder()
            .refreshAfterWrite(5, TimeUnit.MINUTES)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .removalListener((Long key, GuildData value, RemovalCause cause) -> {
                GuildDataMapper.insert(key, value);
                CascadeBot.logger.debug("Guild ID: " + key + " removed from cache and saved to db because: " + cause.toString());
            })
            .build(id -> {
                GuildData dbData = CascadeBot.INS.getDatabaseManager().getDatabase().getCollection(COLLECTION, GuildData.class).find(eq("guild_id", id)).first();
                if (dbData == null) {
                    CascadeBot.logger.debug("Attempted to load guild data for ID: " + id + ", none was found so creating new data object");
                    GuildData data = new GuildData(id);
                    GuildDataMapper.insert(id, data);
                    return data;
                }

                CascadeBot.logger.debug("Loaded data from database for guild ID: " + id);
                return dbData;
            });


    public static void update(long id, Bson update) {
        CascadeBot.INS.getDatabaseManager().runAsyncTask(database -> {
            database.getCollection(COLLECTION, GuildData.class).updateOne(eq("guild_id", id), update, new DebugLogCallback<>("Updated Guild ID " + id + ":", update));
        });
    }

    public static void insert(long id, GuildData data) {
        CascadeBot.INS.getDatabaseManager().runAsyncTask(database -> {
            database.getCollection(COLLECTION, GuildData.class).insertOne(data, new DebugLogCallback<>("Inserted Guild ID " + id));
        });
    }

    public static GuildData getGuildData(long id) {
        return guilds.get(id);
    }

    public static Document processCommandInfo(GuildCommandInfo commandInfo) {
        Document commandDoc = new Document();
        commandDoc.put("command", commandInfo.getCommand());
        commandDoc.put("enabled", commandInfo.isEnabled());
        commandDoc.put("aliases", commandInfo.getAliases());
        return commandDoc;
    }

}
