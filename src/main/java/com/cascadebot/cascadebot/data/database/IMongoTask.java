/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.data.database;

import com.mongodb.client.MongoDatabase;

@FunctionalInterface
public interface IMongoTask {

    public void run(MongoDatabase database);

}
