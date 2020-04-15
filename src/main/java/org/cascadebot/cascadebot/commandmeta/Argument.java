/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commandmeta;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.data.language.Locale;

import java.util.List;
import java.util.Set;

@Getter
public class Argument {

    private final String id;
    private final List<Argument> subArgs;
    private final ArgumentType type;
    private final boolean displayAlone;
    private final Set<String> aliases;

    Argument(String id, ArgumentType type, boolean displayAlone, List<Argument> subArgs, Set<String> aliases) {
        this.id = id;
        this.subArgs = List.copyOf(subArgs);
        this.type = type;
        this.displayAlone = displayAlone;
        this.aliases = Set.copyOf(aliases);
    }

    public String name(Locale locale) {
        if (type != ArgumentType.COMMAND) {
            return Language.i18n(locale, "arguments." + id.substring(id.lastIndexOf('.') + 1));
        }
        int sepCount = StringUtils.countMatches(id, '.');
        if (sepCount == 1) {
            ICommandMain command = CascadeBot.INS.getCommandManager().getCommand(id.substring(0, id.lastIndexOf('.')));
            if (command != null) {
                var subCommand = command.getSubCommands().stream().filter(sub -> sub.command().equals(id.substring(id.lastIndexOf('.') + 1))).findFirst().orElse(null);
                if (subCommand != null) {
                    return subCommand.command(locale);
                }
            }
        } else if (sepCount == 0) {
            ICommandMain command = CascadeBot.INS.getCommandManager().getCommand(id);
            if (command != null) {
                return command.command(locale);
            }
        }

        if (Language.getLanguage(locale).getElement("commands." + id).isPresent()) {
            return Language.i18n(locale, "commands." + id + ".command");
        }
        return Language.i18n(locale, "arguments." + id.replace(".", "#") + ".name");
    }

    public String description(Locale locale) {
        if (type != ArgumentType.COMMAND) {
            Argument parent = CascadeBot.INS.getArgumentManager().getParent(id);
            return parent != null ? parent.description(locale) : "";
        }
        int sepCount = StringUtils.countMatches(id, '.');
        if (sepCount == 1) {
            ICommandMain command = CascadeBot.INS.getCommandManager().getCommand(id.substring(0, id.lastIndexOf('.')));
            if (command != null) {
                var subCommand = command.getSubCommands().stream().filter(sub -> sub.command().equals(id.substring(id.lastIndexOf('.') + 1))).findFirst().orElse(null);
                if (subCommand != null) {
                    return subCommand.description(locale);
                }
            }
        } else if (sepCount == 0) {
            ICommandMain command = CascadeBot.INS.getCommandManager().getCommand(id);
            if (command != null) {
                return command.description(locale);
            }
        }

        if (Language.getLanguage(locale).getElement("commands." + id).isPresent()) {
            return Language.i18n(locale, "commands." + id + ".description");
        }
        return Language.i18n(locale, "arguments." + id.replace(".", "#") + ".description");

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
    public String getUsageString(Locale locale, String base) {
        StringBuilder usageBuilder = new StringBuilder();
        String field = this.getArgument(locale);

        if (isDisplayAlone() || subArgs.size() == 0) {
            usageBuilder.append("`").append(base).append(field).append("`");
            if (!StringUtils.isBlank(description(locale))) {
                usageBuilder.append(" - ").append(description(locale));
            }
            usageBuilder.append('\n');
        }
        for (Argument subArg : subArgs) {
            usageBuilder.append(subArg.getUsageString(locale, base + field + " "));
        }

        return usageBuilder.toString();
    }

    public String getArgument(Locale locale) {
        String argument = name(locale).isBlank() ? id.substring(id.lastIndexOf('.') + 1) : name(locale);
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

    //TODO implement utils for checking arguments in the command. we have a class here why not use it.
}
