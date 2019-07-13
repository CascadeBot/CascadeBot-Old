/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.database;

import com.mongodb.async.client.MongoDatabase;

@FunctionalInterface
public interface IAsyncMongoTask {

    public void run(MongoDatabase database);

}
