/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commandmeta;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

@Getter
public class Argument {

    private final String id;
    private final Set<Argument> subArgs;
    private final ArgumentType type;
    private final boolean displayAlone;
    private final Set<String> aliases;

    Argument(String id, ArgumentType type, boolean displayAlone, Set<Argument> subArgs, Set<String> aliases) {
        this.id = id;
        this.subArgs = Set.copyOf(subArgs);
        this.type = type;
        this.displayAlone = displayAlone;
        this.aliases = Set.copyOf(aliases);
    }

    public String getName() {
        // TODO: Implement this into locale
        return "";
    }

    public String getDescription() {
        // TODO: Implement this into locale
        return "";
    }

    /**
     * Gets the usage string.
     * <p>
     * Formatting:
     * - Aliased arguments are shown as {@code <alias1|alias2>} for as many aliases as the argument has.
     * - A required parameter is show as {@code <argument>}
     * - An optional parameter is show as {@code [argument]}
     *
     * @param base The base command/prefix to use. Example: ';help '.
     * @return A string representing the usage.
     */
    public String getUsageString(String base) {
        StringBuilder usageBuilder = new StringBuilder();
        String field = this.toString();

        if (isDisplayAlone() || subArgs.size() == 0) {
            usageBuilder.append("`").append(base).append(field).append("`");
            if (!StringUtils.isBlank(getDescription())) {
                usageBuilder.append(" - ").append(getDescription());
            }
            usageBuilder.append('\n');
        }
        for (Argument subArg : subArgs) {
            usageBuilder.append(subArg.getUsageString(base + field + " "));
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
        if (!args[pos].equalsIgnoreCase(getName()) && !this.type.equals(ArgumentType.OPTIONAL)) {
            for (String alias : aliases) {
                if (!args[pos].equalsIgnoreCase(alias)) {
                    return false;
                }
            }
        }
        if (this.type.equals(ArgumentType.COMMAND) && this.subArgs.size() > 0 && this.getDescription().isEmpty()) {
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
        String argument = getName().isBlank() ? id.substring(id.lastIndexOf('.') + 1) : getName();
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

    public boolean argEquals(String id) {
        return this.id.equalsIgnoreCase(id);
    }

    public boolean argStartsWith(String start) {
        return this.id.startsWith(start.toLowerCase());
    }

    //TODO implement utils for checking arguments in the command. we have a class here why not use it.
}
