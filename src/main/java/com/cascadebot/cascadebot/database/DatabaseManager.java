/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import net.dv8tion.jda.core.utils.Checks;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class DatabaseManager {

    private final MongoClient SYNC_CLIENT;
    private final com.mongodb.async.client.MongoClient ASYNC_CLIENT;

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseManager.class);
    private String database;

    public DatabaseManager(String username, String password, String database, String options, String[] hosts) {
        this(ConnectionStringType.STANDARD, username, password, database, options, hosts);
    }

    public DatabaseManager(ConnectionStringType connectionStringType, String username, String password, String database, String options, String[] hosts) {
        this.database = database;

        String connectionString = "";
        if (StringUtils.isAnyBlank(hosts)) hosts = new String[]{"localhost"};
        switch (connectionStringType) {
            case SRV:
                connectionString = buildSRVConnectionString(username, password, hosts[0], database, options);
                break;
            case STANDARD:
                connectionString = buildStandardConnectionString(username, password, hosts, database, options);
                break;
        }

        MongoClientSettings.Builder builder = MongoClientSettings.builder();
        builder.credential(MongoCredential.createCredential())

        SYNC_CLIENT = MongoClients.create();
        ASYNC_CLIENT = com.mongodb.async.client.MongoClients.create(connectionString);
    }

    public DatabaseManager(String connectionString) {
        SYNC_CLIENT =
                MongoClients.create(MongoClientSettings.builder().applyToClusterSettings(builder -> builder.))..build());
        ASYNC_CLIENT = com.mongodb.async.client.MongoClients.create(connectionString);
    }

    /**
     * Builds a standard mongodb connection string
     *
     * @param username The optional username to use for this connection string.
     * @param password The optional password to use for this connection. For this to be included in the connection string, a username must be provided.
     * @param hosts    A non-empty list of hosts with ports appended in the format {@code host:port}. None of the provided strings can be blank. The default is {@code 27017}.
     * @param database The optional database to connect to.
     * @param options  The optional string of options to append to this connection string.
     * @return The built connection string.
     * @throws IllegalArgumentException If hosts is empty.
     */
    public static String buildStandardConnectionString(String username, String password, String[] hosts, String database, String options) {
        Checks.notEmpty(hosts, "hosts");
        Checks.check(!StringUtils.isAnyBlank(hosts), "No hosts can be blank!");
        StringBuilder builder = new StringBuilder()
                .append("mongodb://");
        if (!StringUtils.isBlank(username)) { // If username is blank, we just move onto the hosts
            if (StringUtils.isBlank(password)) {
                builder.append(username).append("@");
            } else {
                builder.append(username).append(":").append(URLEncoder.encode(password, StandardCharsets.UTF_8)).append("@"); // Make sure the password is encoded to not use : @ %
            }
        }
        for (String host : hosts) {
            builder.append(host).append(",");
        }
        builder.deleteCharAt(builder.length() - 1); // Remove last comma from hosts list
        builder.append("/").append(StringUtils.isBlank(database) ? "" : database);
        builder.append(StringUtils.isBlank(options) ? "" : "?" + options);
        return builder.toString();
    }

    /**
     * Builds a SRV mongodb connection string.
     *
     * @param username The optional username to use for this connection string.
     * @param password The optional password to use for this connection. For this to be included in the connection string, a username must be provided.
     * @param host     A non-blank host with a port appended in the format {@code host:port}. The default is {@code 27017}.
     * @param database The optional database to connect to.
     * @param options  The optional string of options to append to this connection string.
     * @return The built connection string.
     * @throws IllegalArgumentException If host is blank.
     */
    public static String buildSRVConnectionString(String username, String password, String host, String database, String options) {
        Checks.notBlank(host, "host");
        StringBuilder builder = new StringBuilder()
                .append("mongodb+srv://");
        if (!StringUtils.isBlank(username)) { // If username is blank, we just move onto the hosts
            if (StringUtils.isBlank(password)) {
                builder.append(username).append("@");
            } else {
                builder.append(username).append(":").append(URLEncoder.encode(password, StandardCharsets.UTF_8)).append("@"); // Make sure the password is encoded to not use : @ %
            }
        }
        builder.append(host);
        builder.append("/").append(StringUtils.isBlank(database) ? "" : database);
        builder.append(StringUtils.isBlank(options) ? "" : "?" + options);
        return builder.toString();
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

    public void runTask(MongoTask task) {
        task.run(SYNC_CLIENT.getDatabase(database));
    }

    public void runAsyncTask(AsyncMongoTask task) {
        task.run(ASYNC_CLIENT.getDatabase(database));
    }

    public void insertDocument(String collection, Document document) {
        runAsyncTask(db -> {
            db.getCollection(collection).insertOne(document, new DebugLogCallback<>("Inserted document", document));
        });
    }


    public enum ConnectionStringType {SRV, STANDARD}

}
