package org.cascadebot.cascadebot.commandmeta

import org.apache.commons.lang3.StringUtils
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.data.language.Language
import org.cascadebot.cascadebot.data.language.Locale

data class Argument internal constructor(val id: String,
                                    val type: ArgumentType,
                                    val displayAlone: Boolean,
                                    val subArgs: Set<Argument>,
                                    val aliases: Set<String>
) {

    fun name(locale: Locale): String {
        if (type != ArgumentType.COMMAND) {
            return Language.i18n(locale, "arguments." + id.substring(id.lastIndexOf('.') + 1))
        }
        val sepCount = StringUtils.countMatches(id, '.')
        if (sepCount == 1) {
            val command = CascadeBot.INS.commandManager.getCommand(id.substring(0, id.lastIndexOf('.')))
            if (command != null) {
                val subCommand = command.subCommands.stream().filter { sub: ISubCommand -> sub.command() == id.substring(id.lastIndexOf('.') + 1) }.findFirst().orElse(null)
                if (subCommand != null) {
                    return subCommand.command(locale)
                }
            }
        } else if (sepCount == 0) {
            val command = CascadeBot.INS.commandManager.getCommand(id)
            if (command != null) {
                return command.command(locale)
            }
        }
        return if (Language.getLanguage(locale).getElement("commands.$id").isPresent) {
            Language.i18n(locale, "commands.$id.command")
        } else Language.i18n(locale, "arguments." + id.replace(".", "#") + ".name")
    }

    fun description(locale: Locale): String {
        if (type != ArgumentType.COMMAND) {
            val parent = CascadeBot.INS.argumentManager.getParent(id)
            return if (parent != null) parent.description(locale) else ""
        }
        val sepCount = StringUtils.countMatches(id, '.')
        if (sepCount == 1) {
            val command = CascadeBot.INS.commandManager.getCommand(id.substring(0, id.lastIndexOf('.')))
            if (command != null) {
                val subCommand = command.subCommands.stream().filter { sub: ISubCommand -> sub.command() == id.substring(id.lastIndexOf('.') + 1) }.findFirst().orElse(null)
                if (subCommand != null) {
                    return subCommand.description(locale)
                }
            }
        } else if (sepCount == 0) {
            val command = CascadeBot.INS.commandManager.getCommand(id)
            if (command != null) {
                return command.description(locale)
            }
        }
        return if (Language.getLanguage(locale).getElement("commands.$id").isPresent) {
            Language.i18n(locale, "commands.$id.description")
        } else Language.i18n(locale, "arguments." + id.replace(".", "#") + ".description")
    }

    /**
     * Gets the usage string.
     *
     *
     * Formatting:
     * - Aliased arguments are shown as `<alias1|alias2>` for as many aliases as the argument has.
     * - A required parameter is show as `<argument>`
     * - An optional parameter is show as `[argument]`
     *
     * @param base The base command/prefix to use. Example: ';help '.
     * @return A string representing the usage.
     */
    fun getUsageString(locale: Locale, base: String): String {
        val usageBuilder = StringBuilder()
        val field = getArgument(locale)
        if (displayAlone || subArgs.isEmpty()) {
            usageBuilder.append("`").append(base).append(field).append("`")
            if (!StringUtils.isBlank(description(locale))) {
                usageBuilder.append(" - ").append(description(locale))
            }
            usageBuilder.append('\n')
        }
        for (subArg in subArgs) {
            usageBuilder.append(subArg.getUsageString(locale, "$base$field "))
        }
        return usageBuilder.toString()
    }

    fun getArgument(locale: Locale): String {
        var argument = if (name(locale).isBlank()) id.substring(id.lastIndexOf('.') + 1) else name(locale)
        if (aliases.isNotEmpty()) {
            val paramBuilder = StringBuilder()
            paramBuilder.append(argument)
            for (alias in aliases) {
                paramBuilder.append("|").append(alias)
            }
            argument = paramBuilder.toString()
        }
        return when (type) {
            ArgumentType.OPTIONAL -> "[$argument]"
            ArgumentType.REQUIRED -> "<$argument>"
            else -> argument
        }
    } //TODO implement utils for checking arguments in the command. we have a class here why not use it.

}
