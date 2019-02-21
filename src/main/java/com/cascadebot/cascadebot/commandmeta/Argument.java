/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commandmeta;

import java.util.Collections;
import java.util.Set;

public class Argument {

    private final String arg;
    private final String description;
    private final Set<Argument> subArgs;
    private final ArgumentType type;
    private final Set<String> aliases;

    private Argument(String arg, String description, Set<Argument> subArgs, ArgumentType type, Set<String> aliases) {
        this.arg = arg;
        this.description = description;
        this.subArgs = Collections.unmodifiableSet(subArgs);
        if (subArgs.size() > 0) {
            this.type = ArgumentType.COMMAND;
        } else {
            this.type = type;
        }
        this.aliases = Collections.unmodifiableSet(aliases);
    }

    public static Argument of(String arg, String description) {
        return new Argument(arg, description, Set.of(), ArgumentType.COMMAND, Set.of());
    }

    public static Argument ofA(String arg, String description, Set<String> aliases) {
        return new Argument(arg, description, Set.of(), ArgumentType.COMMAND, aliases);
    }

    public static Argument of(String arg, String description, ArgumentType type) {
        return new Argument(arg, description, Set.of(), type, Set.of());
    }

    public static Argument ofA(String arg, String description, ArgumentType type, Set<String> aliases) {
        return new Argument(arg, description, Set.of(), type, aliases);
    }

    public static Argument of(String arg, String description, Set<Argument> subArgs) {
        return new Argument(arg, description, subArgs, ArgumentType.COMMAND, Set.of());
    }

    public static Argument ofA(String arg, String description, Set<Argument> subArgs, Set<String> aliases) {
        return new Argument(arg, description, subArgs, ArgumentType.COMMAND, aliases);
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
