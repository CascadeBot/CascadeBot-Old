/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.managers;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.mongodb.Block;
import com.mongodb.async.client.ChangeStreamIterable;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.data.database.DebugLogCallback;
import org.cascadebot.cascadebot.data.objects.GuildData;
import org.cascadebot.cascadebot.events.GuildSaveListener;
import org.cascadebot.cascadebot.utils.diff.Diff;
import org.cascadebot.cascadebot.utils.diff.Difference;
import org.cascadebot.cascadebot.utils.diff.DifferenceChanged;
import org.cascadebot.cascadebot.utils.lists.CollectionDiff;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.eq;

public final class GuildDataManager {

    public static final String COLLECTION = "guilds";

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
        });;
    }

    public static void updateDiff(long id, Difference difference, GuildData newData) {
        List<Bson> bsonList = new ArrayList<>();
        for (Map.Entry<String, Object> added : difference.getAdded().entrySet()) {
            bsonList.add(Updates.set(added.getKey(), added.getValue()));
        }
        for (Map.Entry<String, Diff> changed : difference.getChanged().entrySet()) {
            if (changed.getValue() instanceof CollectionDiff) {
                for (Object obj : ((CollectionDiff<?>) changed.getValue()).getAdded()) {
                    bsonList.add(Updates.addToSet(changed.getKey(), obj));
                }
                for (Object obj : ((CollectionDiff<?>) changed.getValue()).getRemoved()) {
                    bsonList.add(Updates.pull(changed.getKey(), obj));
                }
            } else if (changed.getValue() instanceof DifferenceChanged) {
                bsonList.add(Updates.set(changed.getKey(), ((DifferenceChanged<?>) changed.getValue()).getNewObj()));
            }
        }
        for (String removed : difference.getRemoved().keySet()) {
            bsonList.add(Updates.unset(removed));
        }
        CascadeBot.INS.getDatabaseManager().runAsyncTask(database -> {
            database.getCollection(COLLECTION, GuildData.class).updateMany(eq("_id", id), Updates.combine(bsonList), new DebugLogCallback<>("Updated Guild ID " + id));
        });;
        guilds.put(newData.getGuildId(), newData);
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

    public static void replaceInternal(GuildData guildData) {
        guilds.put(guildData.getGuildId(), guildData);
    }

    public static void replaceSync(long id, GuildData data) {
        CascadeBot.INS.getDatabaseManager().runTask(database -> {
            database.getCollection(COLLECTION, GuildData.class).replaceOne(eq("_id", id), data);
        });
    }

    public static GuildData getGuildData(long id) {
        if (id == 0) {
            throw new UnsupportedOperationException("Cannot load guild with id 0");
        }
        return guilds.get(id);
    }

    public static LoadingCache<Long, GuildData> getGuilds() {
        return guilds;
    }

}
