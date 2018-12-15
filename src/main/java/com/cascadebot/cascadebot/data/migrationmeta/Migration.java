/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.data.migrationmeta;

import com.cascadebot.shared.Version;
import org.bson.Document;

public abstract class Migration {

    private final Version newVersion;

    public Migration(Version oldVersion, Version newVersion) {
        this.newVersion = newVersion;
    }

    public abstract Document runMigration(Document document);

    Document runMigrationInternal(Document document) {
        Document newDocument = runMigration(document);
        if (Version.parseVer(newDocument.getString("config_version")).compareTo(newVersion) < 0) {
            throw new MigrationException(
                    "Intermediate config does not have the expected version! Config has a larger version than the migration!" +
                            "\nConfig version was expected to be less than {} but it wasn't ;(" +
                            "\nCan you ask the migration to not change the version plskthnx :)"
                    , newVersion
            );
        }
        newDocument.put("config_version", newVersion.toString());
        return newDocument;
    }

    public Version getNewVersion() {
        return newVersion;
    }

}


