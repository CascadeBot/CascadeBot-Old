/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.connection.netty.NettyStreamFactory;
import de.bild.codec.PojoCodecProvider;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.cascadebot.cascadebot.utils.WeightPair;
import org.cascadebot.cascadebot.utils.LanguageEmbedField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseManager.class);

    private final MongoClient SYNC_CLIENT;
    private final com.mongodb.async.client.MongoClient ASYNC_CLIENT;
    private final CodecRegistry CODEC_REGISTRY = CodecRegistries.fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            CodecRegistries.fromProviders(PojoCodecProvider.builder().register(
                    "org.cascadebot.cascadebot.data.objects",
                    "org.cascadebot.cascadebot.permissions.objects",
                    "org.cascadebot.cascadebot.utils.buttons",
                    "org.cascadebot.cascadebot.utils.lists",
                    "org.cascadebot.cascadebot.scheduler",
                    "org.cascadebot.shared"
            ).register(LanguageEmbedField.class).register(WeightPair.class).build())
    );

    @Getter
    private String databaseName;

    public DatabaseManager(String username, char[] password, String databaseName, List<String> hosts, boolean ssl) {
        MongoClientSettings.Builder settingsBuilder = MongoClientSettings.builder();

        if (!StringUtils.isBlank(username) && password.length != 0) {
            settingsBuilder.credential(MongoCredential.createCredential(
                    username, databaseName, password.clone()
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

        setDatabaseName(connString.getDatabase());

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

    public MongoDatabase getDatabase() {
        return SYNC_CLIENT.getDatabase(databaseName);
    }

    public void setDatabaseName(String database) {
        this.databaseName = database;
    }

    public void runTask(IMongoTask task) {
        task.run(SYNC_CLIENT.getDatabase(databaseName));
    }

    public void runAsyncTask(IAsyncMongoTask task) {
        task.run(ASYNC_CLIENT.getDatabase(databaseName));
    }

    public void insertDocument(String collection, Document document) {
        runTask(db -> {
            db.getCollection(collection).insertOne(document);
        });
    }

    public CodecRegistry getCodecRegistry() {
        return CODEC_REGISTRY;
    }

}
