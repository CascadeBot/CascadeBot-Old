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
import org.cascadebot.cascadebot.data.objects.user.CascadeUser;

import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.eq;

public class CascadeUserDataManager {

    private static final String COLLECTION = "bot_users";
    
    private static LoadingCache<Long, CascadeUser> users = Caffeine.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .recordStats()
            .build(id -> {
                CascadeUser user = CascadeBot.INS.getDatabaseManager().getDatabase().getCollection(COLLECTION, CascadeUser.class).find(eq("_id", id)).first();
                if (user == null) {
                    user = new CascadeUser(id);
                    insert(id, user);
                }

                CascadeBot.LOGGER.debug("Loaded data from database for user ID: " + id);
                return user;
            });

    public static void update(long id, Bson update) {
        CascadeBot.INS.getDatabaseManager().runAsyncTask(database -> {
            database.getCollection(COLLECTION, CascadeUser.class).updateOne(eq("_id", id), update, new DebugLogCallback<>("Updated User ID " + id + ":", update));
        });
    }

    public static void insert(long id, CascadeUser data) {
        CascadeBot.INS.getDatabaseManager().runAsyncTask(database -> {
            database.getCollection(COLLECTION, CascadeUser.class).insertOne(data, new DebugLogCallback<>("Inserted User ID " + id));
        });
    }

    public static void replace(long id, CascadeUser data) {
        CascadeBot.INS.getDatabaseManager().runAsyncTask(database -> {
            database.getCollection(COLLECTION, CascadeUser.class).replaceOne(eq("_id", id), data, new DebugLogCallback<>("Replaced User ID " + id));
        });
    }

    public static void replaceSync(long id, CascadeUser data) {
        CascadeBot.INS.getDatabaseManager().runTask(database -> {
            database.getCollection(COLLECTION, CascadeUser.class).replaceOne(eq("_id", id), data);
        });
    }

    public static CascadeUser getUser(long id) {
        return users.get(id);
    }

    public static LoadingCache<Long, CascadeUser> getUsers() {
        return users;
    }

}
