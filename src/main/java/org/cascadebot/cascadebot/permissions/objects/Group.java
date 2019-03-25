/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.permissions.objects;

import com.google.common.collect.Sets;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import java.util.Set;

public class Group {

    private String name;
    private Set<CascadePermission> permissions = Sets.newConcurrentHashSet();

    public Group(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
