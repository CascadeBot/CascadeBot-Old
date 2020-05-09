package org.cascadebot.cascadebot.utils.placeholders

import org.cascadebot.cascadebot.data.language.Language
import org.cascadebot.cascadebot.data.language.Locale

abstract class Placeholder<T>(val key: String) {

    val localisedKeys: Map<Locale, String> by lazy {
        Language.getLanguages().mapValues {
            val element = it.value.getElement("placeholders.$key")
            if (element.isEmpty) return@mapValues key
            if (element.get().isJsonObject) {
                return@mapValues element.get().asJsonObject["key"]?.asString ?: key
            } else {
                return@mapValues if (element.get().asString.isBlank()) key else element.get().asString
            }
        }
    }

}

class StaticPlaceholder<T>(key: String, val mapping: StaticPlaceholder<T>.(T) -> String?) : Placeholder<T>(key)

class ArgsPlaceholder<T>(key: String, val mapping: ArgsPlaceholder<T>.(T, List<String>) -> String?) : Placeholder<T>(key) {

    val args: MutableMap<String, List<String>> = mutableMapOf()

    fun isArg(argKey: String, test: String): Boolean {
        if (argKey in args) return test.toLowerCase() in args[argKey]!!
        val argsList = mutableListOf<String>()
        for (value in Language.getLanguages().values) {
            argsList.add(value.getString("placeholders.$key.$argKey").orElse(argKey))
        }
        args[argKey] = argsList.toList()
        return argsList.contains(test.toLowerCase())
    }

}