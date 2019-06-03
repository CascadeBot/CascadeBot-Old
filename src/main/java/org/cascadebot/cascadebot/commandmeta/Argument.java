/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commandmeta;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Set;

@Getter
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
        this.type = type;
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

    public static Argument of(String arg, String description, ArgumentType type, Set<Argument> subArgs) {
        return new Argument(arg, description, subArgs, type, Set.of());
    }

    /**
     * Gets the usage string.
     * <p>
     * Formatting:
     * - Aliased arguments are shown as {@code <alias1/alias2>} for as many aliases as the argument has.
     * - A required parameter is show as {@code <argument>}
     * - An optional parameter is show as {@code [argument]}
     *
     * @param base The base command/prefix to use. Example: ';help '.
     * @return A string representing the usage.
     */
    protected String getUsageString(String base) {
        StringBuilder usageBuilder = new StringBuilder();
        if (subArgs.size() > 0) {
            String field = this.toString();
            if (!StringUtils.isBlank(description) && (subArgs.isEmpty() || subArgs.stream().allMatch(argument -> argument.getType() == ArgumentType.OPTIONAL))) {
                usageBuilder.append("`").append(base).append(arg).append("` - ").append(description).append('\n');
            }
            for (Argument subArg : subArgs) {
                usageBuilder.append(subArg.getUsageString(base + field + " "));
            }
        } else {
            usageBuilder.append("`").append(base).append(this.toString()).append("`");
            if (!StringUtils.isBlank(description)) {
                usageBuilder.append(" - ").append(description);
            }
            usageBuilder.append('\n');
        }

        return usageBuilder.toString();
    }

    /**
     * Checks for this argument at a given position.
     *
     * @param args The arguments sent in from the command.
     * @param pos  The position this argument should be in.
     * @return If the argument exists at that position.
     */
    public boolean argExists(String[] args, int pos) {
        if (args.length <= pos) {
            return false;
        }
        if (type.equals(ArgumentType.REQUIRED)) {
            return true;
        }
        if (!args[pos].equalsIgnoreCase(arg) && !this.type.equals(ArgumentType.OPTIONAL)) {
            for (String alias : aliases) {
                if (!args[pos].equalsIgnoreCase(alias)) {
                    return false;
                }
            }
        }
        if (this.type.equals(ArgumentType.COMMAND) && this.subArgs.size() > 0 && this.description.isEmpty()) {
            for (Argument sub : this.subArgs) {
                if (sub.type.equals(ArgumentType.REQUIRED) || sub.type.equals(ArgumentType.COMMAND)) {
                    return sub.argExists(args, pos + 1);
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        String argument = arg;
        if (aliases.size() > 0) {
            StringBuilder paramBuilder = new StringBuilder();
            paramBuilder.append(argument);
            for (String alias : aliases) {
                paramBuilder.append("|").append(alias);
            }
            argument = paramBuilder.toString();
        }
        switch (type) {
            case OPTIONAL:
                argument = "[" + argument + "]";
                break;
            case REQUIRED:
                argument = "<" + argument + ">";
                break;
        }
        return argument;
    }

    public boolean argEquals(String arg) {
        return this.arg.equalsIgnoreCase(arg);
    }

    public boolean argStartsWith(String start) {
        return this.arg.startsWith(start.toLowerCase());
    }

    //TODO implement utils for checking arguments in the command. we have a class here why not use it.
}
