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
        this.arg = arg.toLowerCase(); //This probably isn't needed but megh
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

    /**
     * Gets the usage string.
     *
     * @param base The base command/preifx to use. Example: ';help '
     * @return A string representing the usage
     */
    protected String getUsageString(String base) {
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
                usageBuilder.append(subArg.getUsageString(base + field + " "));
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

    /**
     * Checks for this argument at a giving position.
     *
     * @param args The args sent in from the command
     * @param pos  The position this argument should be in
     * @return If the argument exists at that position
     */
    public boolean argExists(String[] args, int pos) {
        if(args.length <= pos) {
            return false;
        }
        if(type.equals(ArgumentType.REQUIRED)) {
            return true;
        }
        if(!args[pos].equalsIgnoreCase(arg) && !this.type.equals(ArgumentType.OPTIONAL)) {
            for(String alias : aliases) {
                if(!args[pos].equalsIgnoreCase(alias)) {
                    return false;
                }
            }
        }
        if(this.type.equals(ArgumentType.COMMAND) && this.subArgs.size() > 0 && this.description.isEmpty()) {
            for(Argument sub : this.subArgs) {
                if(sub.type.equals(ArgumentType.REQUIRED) || sub.type.equals(ArgumentType.COMMAND)) {
                    return sub.argExists(args, pos + 1);
                }
            }
        }
        return true;
    }

    public Set<Argument> getSubArgs() {
        return subArgs;
    }

    public boolean argEquals(String arg) {
        return this.arg.equalsIgnoreCase(arg);
    }

    public boolean argStartsWith(String start) {
        return this.arg.startsWith(start.toLowerCase());
    }

    //TODO implement utils for checking arguments in the command. we have a class here why not use it.
}
