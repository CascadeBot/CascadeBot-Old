package org.cascadebot.cascadebot.utils.placeholders

import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.utils.FormatUtils
import java.time.OffsetDateTime

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

val tags = placeholders<CommandContext> {
    argsPlaceholder("server") { context, args ->
        if (args.size != 1) return@argsPlaceholder context.guild.name
        when (true) {
            isArg("id", args[0]) -> context.guild.id
            isArg("region", args[0]) -> context.guild.region.name
            isArg("owner", args[0]) -> context.guild.owner!!.user.asTag
            isArg("member_count", args[0]) -> context.guild.memberCache.size().toString()
            else -> null
        }
    }
    argsPlaceholder("sender") { context, args ->
        if (args.size != 1) return@argsPlaceholder context.user.asTag
        when (true) {
            isArg("id", args[0]) -> context.member.id
            isArg("nickname", args[0]) -> context.member.nickname ?: "No nickname!"
            isArg("name", args[0]) -> context.user.name
            isArg("mention", args[0]) -> context.user.asMention
            else -> null
        }
    }
    argsPlaceholder("channel") { context, args ->
        if (args.size != 1) return@argsPlaceholder context.channel.name
        when (true) {
            isArg("id", args[0]) -> context.channel.id
            isArg("mention", args[0]) -> context.channel.asMention
            isArg("topic", args[0]) -> context.channel.topic
            isArg("creation", args[0]) -> FormatUtils.formatDateTime(context.channel.timeCreated, context.locale)
            isArg("parent", args[0]) -> if (context.channel.parent == null) "No channel parent" else context.channel.parent!!.name
            else -> null
        }
    }
    staticPlaceholder("time") { context -> FormatUtils.formatDateTime(OffsetDateTime.now(), context.locale) }
    argsPlaceholder("args") { context, args ->
        if (args.isEmpty()) return@argsPlaceholder null
        val argNum = args[0].toIntOrNull() ?: -1
        if (argNum in 0..context.args.size) return@argsPlaceholder null

        context.getArg(argNum)
    }
}
