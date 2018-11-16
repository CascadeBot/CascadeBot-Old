/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.data.migrationmeta;

import com.cascadebot.cascadebot.utils.ReflectionUtils;
import com.cascadebot.shared.ExitCodes;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MigrationManager {

    private static MigrationManager instance = null;

    private final List<Migration> migrations = new CopyOnWriteArrayList<>();
    private final Logger logger = LoggerFactory.getLogger("Migration Manager");

    public MigrationManager() {
        instance = this;

        long start = System.currentTimeMillis();
        try {
            for (Class<?> c : ReflectionUtils.getClasses("com.cascadebot.cascadebot.data.migrations")) {
                if (Migration.class.isAssignableFrom(c))
                    migrations.add((Migration) ConstructorUtils.invokeConstructor(c));
            }
            logger.info("Loaded {} migrations in {}ms.", migrations.size(), (System.currentTimeMillis() - start));
        } catch (ClassNotFoundException | IOException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            logger.error("Could not load migrations!", e);
            System.exit(ExitCodes.ERROR_STOP_NO_RESTART);
        }
    }

    public void

    public static MigrationManager instance() {
        return instance;
    }

}
