/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.utils;

import com.cascadebot.cascadebot.tasks.Task;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ConfirmUtils {

    // Holds the users that have confirmed an action
    //                       User ID, Key
    private static ListMultimap<Long, String> confirmedMap = ArrayListMultimap.create();

    public static boolean confirmAction(long userId, String actionKey) {
        return confirmAction(userId, actionKey, TimeUnit.MINUTES.toMillis(1));
    }

    public static boolean confirmAction(long userId, String actionKey, long millis) {
        // TODO: Expire after millis, need to modify tasks and scheduler
        return confirmedMap.put(userId, actionKey);
    }

    public static boolean unconfirmAction(long userId, String actionKey) {
        return confirmedMap.remove(userId, actionKey);
    }

    public static List<String> unconfirmActions(long userId) {
        return confirmedMap.removeAll(userId);
    }

    public static boolean hasConfirmedAction(long userId, String actionKey) {
        return confirmedMap.containsEntry(userId, actionKey);
    }

    public static List<String> getConfirmedActions(long userId) {
        return confirmedMap.get(userId);
    }


}
