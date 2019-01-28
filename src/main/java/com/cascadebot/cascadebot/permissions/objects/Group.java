package com.cascadebot.cascadebot.permissions.objects;

import com.cascadebot.cascadebot.permissions.Permission;
import com.google.common.collect.Sets;

import java.util.Set;

public class Group {

    private String name;
    private Set<Permission> permissions = Sets.newConcurrentHashSet();

    public Group(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
