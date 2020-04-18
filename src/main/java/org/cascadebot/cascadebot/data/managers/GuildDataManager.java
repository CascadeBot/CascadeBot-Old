/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.managers;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.bson.conversions.Bson;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.data.database.DebugLogCallback;
import org.cascadebot.cascadebot.data.objects.guild.GuildData;
import org.cascadebot.cascadebot.events.GuildSaveListener;

import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.eq;

public final class GuildDataManager {

    private static final String COLLECTION = "guilds";

    private static LoadingCache<Long, GuildData> guilds = Caffeine.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .removalListener(new GuildSaveListener())
            .recordStats()
            .build(id -> {
                GuildData dbData = CascadeBot.INS.getDatabaseManager().getDatabase().getCollection(COLLECTION, GuildData.class).find(eq("_id", id)).first();
                if (dbData == null) {
                    CascadeBot.LOGGER.debug("Attempted to load guild data for ID: " + id + ", none was found so creating new data object");
                    dbData = new GuildData(id);
                    GuildDataManager.insert(id, dbData);
                } else {
                    CascadeBot.LOGGER.debug("Loaded data from database for guild ID: " + id);
                }
                dbData.onGuildLoaded();
                return dbData;
            });


    public static void update(long id, Bson update) {
        CascadeBot.INS.getDatabaseManager().runAsyncTask(database -> {
            database.getCollection(COLLECTION, GuildData.class).updateOne(eq("_id", id), update, new DebugLogCallback<>("Updated Guild ID " + id + ":", update));
        });
    }

    public static void insert(long id, GuildData data) {
        CascadeBot.INS.getDatabaseManager().runAsyncTask(database -> {
            database.getCollection(COLLECTION, GuildData.class).insertOne(data, new DebugLogCallback<>("Inserted Guild ID " + id));
        });
    }

    public static void replace(long id, GuildData data) {
        CascadeBot.INS.getDatabaseManager().runAsyncTask(database -> {
            database.getCollection(COLLECTION, GuildData.class).replaceOne(eq("_id", id), data, new DebugLogCallback<>("Replaced Guild ID " + id));
        });
    }

    public static void replaceSync(long id, GuildData data) {
        CascadeBot.INS.getDatabaseManager().runTask(database -> {
            database.getCollection(COLLECTION, GuildData.class).replaceOne(eq("_id", id), data);
        });
    }

    public static GuildData getGuildData(long id) {
        return guilds.get(id);
    }

    public static LoadingCache<Long, GuildData> getGuilds() {
        return guilds;
    }

}
