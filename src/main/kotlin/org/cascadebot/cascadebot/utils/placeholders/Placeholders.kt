package org.cascadebot.cascadebot.utils.placeholders

class Placeholders<T> {

    companion object {
        // https://regex101.com/r/LxMI0R/3
        val placeholderRegex = Regex("\\{([\\p{Ll}_-]+)(?::((?:[\\p{Ll}0-9_-]+,?)+))?}")
    }

    private val placeholders: MutableList<Placeholder<T>> = mutableListOf()

    fun staticPlaceholder(key: String, mapping: StaticPlaceholder<T>.(T) -> String?) {
        placeholders.add(StaticPlaceholder(key, mapping))
    }

    fun argsPlaceholder(key: String, mapping: ArgsPlaceholder<T>.(T, List<String>) -> String?) {
        placeholders.add(ArgsPlaceholder(key, mapping))
    }

    fun formatMessage(message: String, input: T): String {
        val toReplace = mutableMapOf<String, String>()
        for (matchResult in placeholderRegex.findAll(message)) {
            placeholders.find { matchResult.groupValues[1] in it.localisedKeys.values }?.let { placeholder ->
                when (placeholder) {
                    is StaticPlaceholder<T> -> {
                        placeholder.mapping(placeholder, input)?.let {
                            toReplace[matchResult.groupValues[0]] = it
                        }
                    }
                    is ArgsPlaceholder<T> -> {
                        val args = if (matchResult.groupValues[2].isNotEmpty()) {
                            matchResult.groupValues[2].split(",")
                        } else {
                            listOf()
                        }

                        placeholder.mapping(placeholder, input, args)?.let {
                            toReplace[matchResult.groupValues[0]] = it
                        }
                    }
                    else -> error("Invalid Placeholder type: ${placeholder::class.simpleName}")
                }
            }
        }
        var newMessage = message
        toReplace.forEach { newMessage = newMessage.replace(it.key, it.value) }
        return newMessage
    }

}

fun <T> placeholders(init: Placeholders<T>.() -> Unit): Placeholders<T> = Placeholders<T>().apply(init)
