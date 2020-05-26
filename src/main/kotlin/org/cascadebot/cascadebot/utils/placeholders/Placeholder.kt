package org.cascadebot.cascadebot.utils.placeholders

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
                        it.value.getString("placeholders.$absoluteKey.description").orElse(null)
                )
            } else {
                error("Could not parse placeholder!")
            }
        }
    }

    data class PlaceholderInfo(val key: String, val description: String? = null)

}

class StaticPlaceholder<T>(key: String, absoluteKey: String, val mapping: StaticPlaceholder<T>.(T) -> String?) : Placeholder<T>(key, absoluteKey)

class ArgsPlaceholder<T>(key: String, absoluteKey: String, val noArg: Boolean, val validArgs: List<String>, val mapping: ArgsPlaceholder<T>.(T, List<String>) -> String?) : Placeholder<T>(key, absoluteKey) {

    val args: MutableMap<String, List<String>> = mutableMapOf()

    fun isArg(argKey: String, test: String): Boolean {
        if (argKey in args) return test.toLowerCase() in args[argKey]!!
        val argsList = mutableListOf<String>()
        for (value in Language.getLanguages().values) {
            argsList.add(value.getString("placeholders.$absoluteKey.$argKey").orElse(argKey))
        }
        args[argKey] = argsList.toList()
        return argsList.contains(test.toLowerCase())
    }

}