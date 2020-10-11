package org.cascadebot.cascadebot.commandmeta

import org.apache.commons.lang3.StringUtils
import org.cascadebot.cascadebot.data.language.Language
import org.cascadebot.cascadebot.data.language.Locale
import org.cascadebot.cascadebot.data.objects.ArgumentType
import kotlin.math.max

data class Argument(val id: String,
                    val type: ArgumentType,
                    val displayAlone: Boolean,
                    val varargs: Boolean,
                    val subArgs: List<Argument>,
                    val aliases: Set<String>
) {

    fun name(locale: Locale): String {
        if (type != ArgumentType.COMMAND) {
            return Language.i18n(locale, "arguments." + id.substring(id.lastIndexOf('.') + 1))
        }
        return if (Language.getLanguage(locale)?.getElement("commands.$id")?.isPresent!!) {
            Language.i18n(locale, "commands.$id.command")
        } else Language.i18n(locale, "arguments." + id.replace(".", "#") + ".name")
    }

    fun description(locale: Locale): String {
        var identifier = id;
        val language = Language.getLanguage(locale)
        language?.let {
            val specificDescription = language.getElement("arguments." + id.replace(".", "#"))
            if (specificDescription.isPresent) {
                return Language.i18n(locale, "arguments." + id.replace(".", "#") + ".description")
            }

            while (identifier.isNotEmpty()) {
                val element = language.getElement("commands.$identifier")
                if (element.isPresent) {
                    return Language.i18n(locale, "commands.$identifier.description")
                }
                identifier = identifier.substring(0, max(identifier.lastIndexOf("."), 1))
            }
        }
        return "No description found for $id"
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
        val varArgs = if (varargs) "..." else ""
        return when (type) {
            ArgumentType.OPTIONAL -> "[$argument$varArgs]"
            ArgumentType.REQUIRED -> "<$argument$varArgs>"
            else -> argument
        }
    } //TODO implement utils for checking arguments in the command. we have a class here why not use it.

}
