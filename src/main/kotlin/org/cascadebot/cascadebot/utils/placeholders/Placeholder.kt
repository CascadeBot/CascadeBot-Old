package org.cascadebot.cascadebot.utils.placeholders

import com.ibm.icu.text.MessageFormat
import org.cascadebot.cascadebot.data.language.Language
import org.cascadebot.cascadebot.data.language.Locale

abstract class Placeholder<T>(val key: String, val absoluteKey: String) {

    val localisedInfo: Map<Locale, PlaceholderInfo> by lazy {
        Language.getLanguages().mapValues {
            val element = it.value.getElement("placeholders.$absoluteKey")
            if (element.isEmpty) return@mapValues PlaceholderInfo(key)
            if (element.get().isJsonObject) {
                return@mapValues PlaceholderInfo(
                        it.value.getString("placeholders.$absoluteKey.key").orElse(key),
                        it.value.getString("placeholders.$absoluteKey.description").orElse(null),
                        it.value.getString("placeholders.$absoluteKey.example_usage").orElse(null)
                )
            } else {
                error("Could not parse placeholder!")
            }
        }
    }

    data class PlaceholderInfo(
            val key: String,
            val description: String? = null,
            val exampleUsage: String? = null
    )

    fun getUsageInfo(locale: Locale): String {
        val info = localisedInfo[locale] ?: error("Could not get localised information from locale $locale")
        val usage = if (true) "" else if (info.exampleUsage != null) "Example usage: ${MessageFormat.format(info.exampleUsage, info.key)}" else ""
        return """
            `{${info.key}}` - ${info.description}
            $usage
        """.trimIndent().trim()
    }

}

class PlaceholderContext<T>(val item: T, val locale: Locale)

class StaticPlaceholder<T>(key: String, absoluteKey: String, val mapping: StaticPlaceholder<T>.(PlaceholderContext<T>) -> String?) : Placeholder<T>(key, absoluteKey)

class ArgsPlaceholder<T>(key: String, absoluteKey: String, val mapping: ArgsPlaceholder<T>.(PlaceholderContext<T>, List<String>) -> String?) : Placeholder<T>(key, absoluteKey) {

    val args: MutableMap<String, Map<Locale, String>> = mutableMapOf()

    fun isArg(locale: Locale, argKey: String, test: String): Boolean {
        if (argKey in args && locale in args[argKey]!!) return args[argKey]!!.getOrElse(locale) { argKey }.equals(test, ignoreCase = true)
        val argsList = mutableMapOf<Locale, String>()
        for ((langLocale, config) in Language.getLanguages()) {
            argsList[langLocale] = config.getString("placeholders.$absoluteKey.$argKey").orElse(argKey)
        }
        args[argKey] = argsList.toMap()
        return argsList[locale].equals(test, ignoreCase = true) || isArg(Locale.getDefaultLocale(), argKey, test)
    }

}