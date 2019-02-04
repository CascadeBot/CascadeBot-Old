/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.data.database;

import com.mongodb.async.SingleResultCallback;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

public class DebugLogCallback<T> implements SingleResultCallback<T> {

    private final Level LEVEL = Level.DEBUG;
    private final Object OBJECT_TO_LOG;
    private final String MESSAGE;

    public DebugLogCallback(Object toLog) {
        this.OBJECT_TO_LOG = toLog;
        this.MESSAGE = "";
    }

    public DebugLogCallback(String message, Object toLog) {
        this.OBJECT_TO_LOG = toLog;
        this.MESSAGE = message;
    }

    @Override
    public void onResult(T result, Throwable t) {
        if (t != null) {
            LoggerFactory.getLogger(DebugLogCallback.class).error("Error in a Mongo callback", t);
        } else if (OBJECT_TO_LOG != null) {
            LoggerFactory.getLogger(DebugLogCallback.class).debug(MESSAGE + ": " + OBJECT_TO_LOG.toString().replace("\n", ""));
        }
    }

}
