/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.data.migrationmeta;

import com.cascadebot.shared.Version;
import org.bson.Document;

public abstract class Migration {

    private final Version oldVersion;
    private final Version newVersion;

    public Migration(Version oldVersion, Version newVersion) {
        this.oldVersion = oldVersion;
        this.newVersion = newVersion;
    }

    public abstract Document runMigration(Document document);

    Document runMigrationInternal(Document document) {
        Document newDocument = runMigration(document);
        newDocument.put("config_version", newVersion.toString());
        return newDocument;
    }

}


