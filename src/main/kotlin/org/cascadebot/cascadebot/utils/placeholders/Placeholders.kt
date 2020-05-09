package org.cascadebot.cascadebot.utils.placeholders

class Placeholders<T> {

    companion object {
        // https://regex101.com/r/LxMI0R/3
        val placeholderRegex = Regex("\\{([\\p{Ll}_-]+)(?::((?:[\\p{Ll}0-9_-]+,?)+))?}")
    }

    private val placeholders: MutableList<Placeholder<T>> = mutableListOf()

    fun addStaticPlaceholder(key: String, mapping: (T) -> String?) {
        placeholders.add(StaticPlaceholder(key, mapping))
    }

    fun addArgsPlaceholder(key: String, mapping: (T, List<String>) -> String?) {
        placeholders.add(ArgsPlaceholder(key, mapping))
    }

    fun formatMessage(message: String, input: T): String {
        val toReplace = mutableMapOf<String, String>()
        for (matchResult in placeholderRegex.findAll(message)) {
            if (matchResult.groupValues[2].isNotEmpty()) {
                placeholders.find { matchResult.groupValues[1] in it.localisedKeys.values }?.let { placeholder ->
                    if (placeholder is ArgsPlaceholder<T>) {
                        placeholder.mapping(input, matchResult.groupValues[2].split(","))?.let {
                            toReplace[matchResult.groupValues[0]] = it
                        }
                    }
                }
            } else {
                placeholders.find { matchResult.groupValues[1] in it.localisedKeys.values }?.let { placeholder ->
                    if (placeholder is StaticPlaceholder<T>) {
                        placeholder.mapping(input)?.let {
                            toReplace[matchResult.groupValues[0]] = it
                        }
                    }
                }
            }
        }
        var newMessage = message
        toReplace.forEach { newMessage = message.replace(it.key, it.value) }
        return newMessage
    }

}

fun <T> placeholders(init: Placeholders<T>.() -> Unit): Placeholders<T> = Placeholders<T>().apply(init)
