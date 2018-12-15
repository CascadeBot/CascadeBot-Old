/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.data.migrationmeta;

public class MigrationException extends RuntimeException {

    public MigrationException() {
    }

    public MigrationException(String message) {
        super(message);
    }

    public MigrationException(String message, Object... objects) {
        super(String.format(message, objects));
    }

    public MigrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public MigrationException(Throwable cause) {
        super(cause);
    }

}
