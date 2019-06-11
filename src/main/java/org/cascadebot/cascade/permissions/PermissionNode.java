/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascade.permissions;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// All credit to the FlareBot project for this file
// https://github.com/FlareBot/FlareBot/blob/master/src/main/java/stream/flarebot/flarebot/permissions/PermissionNode.java
@AllArgsConstructor
@Getter
public class PermissionNode implements Predicate<String> {

    private final String node;

    @Override
    public boolean test(String permission) {
        if (getNode().equals("*"))
            return true;
        // It splits by a `*` that's on a start of a string or has . around them
        String textNode = Arrays.stream(getNode().split("(?:^\\*(\\.))|(?:(?<=\\.)\\*(?=\\.))|(?:(?<=\\.)\\*$)"))
                // Then it escapes all of that so its not regexps
                .map(Pattern::quote)
                // And then it joins them with a match all regexp
                .collect(Collectors.joining(".+")) + (getNode().endsWith("*") ? ".+" : "");
        // And then it lets Java REGEXP compare them. Ty @I-Al-Istannen for making me do this comment
        return permission.matches(textNode);
    }

}