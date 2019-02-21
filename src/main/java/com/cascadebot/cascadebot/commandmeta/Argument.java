/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commandmeta;

import java.util.HashSet;
import java.util.Set;

public class Argument {

    private String arg;
    private String description;
    private Set<Argument> subArgs;
    private ArgumentType type;
    private Set<String> aliases;

    private Argument(String arg, String description, Set<Argument> subArgs, ArgumentType type, Set<String> aliases) {
        this.arg = arg;
        this.description = description;
        this.subArgs = subArgs;
        if (subArgs.size() > 0) {
            this.type = ArgumentType.COMMAND;
        } else {
            this.type = type;
        }
        this.aliases = aliases;
    }

    public static Argument of(String arg) {
        return new Argument(arg, "", new HashSet<>(), ArgumentType.COMMAND, new HashSet<>());
    }

    public static Argument of(String arg, ArgumentType type) {
        return new Argument(arg, "", new HashSet<>(), type, new HashSet<>());
    }

    public static Argument of(String arg, String description) {
        return new Argument(arg, description, new HashSet<>(), ArgumentType.COMMAND, new HashSet<>());
    }

    public static Argument of(String arg, String description, ArgumentType type) {
        return new Argument(arg, description, new HashSet<>(), type, new HashSet<>());
    }

    public static Argument of(String arg, String description, Set<Argument> subArgs) {
        return new Argument(arg, description, subArgs, ArgumentType.COMMAND, new HashSet<>());
    }

    protected String getUnformattedUsageString(String base) {
        StringBuilder usageBuilder = new StringBuilder();
        if (subArgs.size() > 0) {
            String field = arg;
            if(aliases.size() > 0) {
                StringBuilder fieldBuilder = new StringBuilder();
                fieldBuilder.append("<").append(field);
                for(String alias : aliases) {
                    fieldBuilder.append("/").append(alias);
                }
                fieldBuilder.append(">");
                field = fieldBuilder.toString();
            } else {
                if (!description.isBlank()) {
                    usageBuilder.append("`").append(base).append(arg).append("` - ").append(description).append('\n');
                }
            }
            for (Argument subArg : subArgs) {
                usageBuilder.append(subArg.getUnformattedUsageString(base + field + " "));
            }
        } else {
            String param = arg;
            if(aliases.size() > 0) {
                StringBuilder paramBuilder = new StringBuilder();
                paramBuilder.append(param);
                for(String alias : aliases) {
                    paramBuilder.append("/").append(alias);
                }
                param = paramBuilder.toString();
            }
            if (type.equals(ArgumentType.OPTIONAL)) {
                param = "[" + param + "]";
            } else if (type.equals(ArgumentType.REQUIRED)) {
                param = "<" + param + ">";
            }
            usageBuilder.append("`").append(base).append(param).append("`");
            if (!description.isBlank()) {
                usageBuilder.append(" - ").append(description);
            }
            usageBuilder.append('\n');
        }

        return usageBuilder.toString();
    }

    public Set<Argument> getSubArgs() {
        return subArgs;
    }

    public boolean argEquals(String arg) {
        return this.arg.equalsIgnoreCase(arg);
    }

    //TODO implement utils for checking arguments in the command. we have a class here why not use it.
}
