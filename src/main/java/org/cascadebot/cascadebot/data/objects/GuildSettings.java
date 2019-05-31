/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects;

import lombok.Getter;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Getter
public class GuildSettings {

    public static Map<String, Field> VALUES = new HashMap<>();

    static {
        for (Field field : GuildSettings.class.getDeclaredFields()) {
            if (field.getName().equals("VALUES")) continue;
            field.setAccessible(true);
            VALUES.put(field.getName().toLowerCase(), field);
        }
    }

    //region Boolean flags
    private boolean mentionPrefix = false; // Whether the bot will respond to a mention as a prefix
    private boolean deleteCommand = true;
    private boolean useEmbedForMessages = true;
    private boolean showPermErrors = true; // Whether commands will silently fail on no permissions
    private boolean showModuleErrors = false;
    private boolean adminsHaveAllPerms = true;
    private boolean allowTagCommands = true; // Whether tag commands will be executed by ;<tagname>
    //endregion

}
