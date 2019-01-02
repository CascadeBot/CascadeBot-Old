/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.data.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.netty.NettyStreamFactory;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseManager {

    private final MongoClient SYNC_CLIENT;
    private final com.mongodb.async.client.MongoClient ASYNC_CLIENT;
    private final CodecRegistry CODEC_REGISTRY = CodecRegistries.fromRegistries(
            com.mongodb.MongoClient.getDefaultCodecRegistry(),
            CodecRegistries.fromProviders(PojoCodecProvider.builder().register(
                    "com.cascadebot.cascadebot.data.objects"
            ).build())
    );


    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseManager.class);
    private String database;

    public DatabaseManager(String username, char[] password, String database, List<String> hosts, boolean ssl) {
        MongoClientSettings.Builder settingsBuilder = MongoClientSettings.builder();

        if (!StringUtils.isBlank(username) && password.length != 0) {
            settingsBuilder.credential(MongoCredential.createCredential(
                    username, database, password.clone()
            ));
            Arrays.fill(password, '\r'); // Fill array with invalid char for security
        }

        settingsBuilder.applyToClusterSettings(clusterBuilder -> clusterBuilder.hosts(
                hosts.stream().map(host -> {
                    if (host.contains(":")) {
                        return new ServerAddress(host.split(":")[0], Integer.valueOf(host.split(":")[1]));
                    } else {
                        return new ServerAddress(host);
                    }
                }).collect(Collectors.toList())
        ));
        settingsBuilder.streamFactoryFactory(NettyStreamFactory::new);
        settingsBuilder.applyToSslSettings(sslBuilder -> sslBuilder.enabled(ssl));
        settingsBuilder.retryWrites(true);

        settingsBuilder.codecRegistry(CODEC_REGISTRY);
        SYNC_CLIENT = MongoClients.create(settingsBuilder.build());
        ASYNC_CLIENT = com.mongodb.async.client.MongoClients.create(settingsBuilder.build());
    }

    public DatabaseManager(String connectionString) {

        MongoClientSettings.Builder builder = MongoClientSettings.builder();
        ConnectionString connString = new ConnectionString(connectionString);
        builder.applyConnectionString(connString);
        builder.streamFactoryFactory(NettyStreamFactory::new);

        setDatabase(connString.getDatabase());

        builder.codecRegistry(CODEC_REGISTRY);

        SYNC_CLIENT = MongoClients.create(builder.build());
        ASYNC_CLIENT = com.mongodb.async.client.MongoClients.create(builder.build());
    }

    public MongoClient getSyncClient() {
        return SYNC_CLIENT;
    }

    public com.mongodb.async.client.MongoClient getAsyncClient() {
        return ASYNC_CLIENT;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public void runTask(IMongoTask task) {
        task.run(SYNC_CLIENT.getDatabase(database));
    }

    public void runAsyncTask(IAsyncMongoTask task) {
        task.run(ASYNC_CLIENT.getDatabase(database));
    }

    public void insertDocument(String collection, Document document) {
        runTask(db -> {
            db.getCollection(collection).insertOne(document);
        });
    }

}
