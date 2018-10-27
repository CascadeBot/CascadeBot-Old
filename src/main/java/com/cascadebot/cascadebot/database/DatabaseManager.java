/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.database;

import com.mongodb.client.MongoClient;
import net.dv8tion.jda.core.utils.Checks;
import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class DatabaseManager {

    private final MongoClient SYNC_CLIENT;
    private final com.mongodb.async.client.MongoClient ASYNC_CLIENT;


    public DatabaseManager(String database, String url) {
        SYNC_CLIENT = null;
        ASYNC_CLIENT = null;
    }


    /**
     * Builds a standard mongodb connection string
     *
     * @param username The optional username to use for this connection string.
     * @param password The optional password to use for this connection. For this to be included in the connection string, a username must be provided.
     * @param hosts    A non-empty list of hosts with ports appended in the format {@code host:port}. The default is {@code 27017}.
     * @param database The optional database to connect to.
     * @param options  The optional string of options to append to this connection string.
     * @return The built connection string.
     * @throws IllegalArgumentException If hosts is empty.
     */
    public static String buildStandardConnectionString(String username, String password, List<String> hosts, String database, String options) {
        Checks.notEmpty(hosts, "hosts");
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

}
