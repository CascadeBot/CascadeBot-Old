/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.data.migrationmeta;

import com.cascadebot.cascadebot.ShutdownHandler;
import com.cascadebot.cascadebot.utils.ReflectionUtils;
import com.cascadebot.shared.Version;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.TreeSet;

public class MigrationManager {

    private static MigrationManager instance = null;

    private final TreeSet<Migration> migrations = new TreeSet<>(Comparator.comparing(Migration::getNewVersion));
    private final Logger logger = LoggerFactory.getLogger("Migration Manager");

    public MigrationManager() {
        instance = this;

        long start = System.currentTimeMillis();
        try {
            for (Class<?> c : ReflectionUtils.getClasses("com.cascadebot.cascadebot.data.migrations")) {
                if (Migration.class.isAssignableFrom(c)) {
                    Migration migration = (Migration) ConstructorUtils.invokeConstructor(c);
                    if (migrations.stream().allMatch(m -> migration.getNewVersion().equals(m.getNewVersion()))) {
                        migrations.add(migration);
                    } else {
                        logger.error("Two migrations have the same version! Exiting! Version: {}", migration.getNewVersion());
                        ShutdownHandler.exitWithError();
                    }
                }
            }
            logger.info("Loaded {} migrations in {}ms.", migrations.size(), (System.currentTimeMillis() - start));
        } catch (ClassNotFoundException | IOException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            logger.error("Could not load migrations!", e);
            ShutdownHandler.exitWithError();
        }
    }

    public LinkedList<Migration> getMigrationsToRun(Version currentVersion) {
        LinkedList<Migration> migrationsToRun = new LinkedList<>(); // Preserve order of migrations
        Version versionToCheck = currentVersion; // Start at oldest config version
        for (Migration migration : migrations){
            if (migration.getNewVersion().compareTo(versionToCheck) > 0) {
                migrationsToRun.add(migration);
            }
            versionToCheck = migration.getNewVersion(); // Set to new version to check for next migration in chain
            // In theory we shouldn't skip any migrations since they are ordered but ya' know, it could happen :eyes:
        }
        return migrationsToRun;
    }

    public static MigrationManager instance() {
        return instance;
    }

}
