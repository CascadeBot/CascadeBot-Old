package org.cascadebot.cascadebot.utils.placeholders

import net.dv8tion.jda.api.events.guild.member.GenericGuildMemberEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.data.language.Language
import org.cascadebot.cascadebot.data.language.Locale
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.messaging.MessagingObjects
import org.cascadebot.cascadebot.utils.FormatUtils
import org.cascadebot.cascadebot.utils.pagination.PageObjects
import java.time.Duration
import java.time.OffsetDateTime

class Placeholders<T>(var key: String) {

    companion object {
        // https://regex101.com/r/LxMI0R/3
        val placeholderRegex = Regex("\\{([\\p{Ll}_-]+)(?::((?:[\\p{Ll}0-9_-]+,?)+))?}")
    }

    private val _placeholders: MutableList<Placeholder<T>> = mutableListOf()
    val placeholders: List<Placeholder<T>>
        get() = _placeholders.toList()

    fun staticPlaceholder(key: String, mapping: StaticPlaceholder<T>.(PlaceholderContext<T>) -> String?) {
        _placeholders.add(StaticPlaceholder(key, "${this.key}.$key", mapping))
    }

    fun argsPlaceholder(key: String, mapping: ArgsPlaceholder<T>.(PlaceholderContext<T>, List<String>) -> String?) {
        _placeholders.add(ArgsPlaceholder(key, "${this.key}.$key", mapping))
    }

    // TODO: Use locale!
    fun formatMessage(locale: Locale, message: String, input: T): String {
        val toReplace = mutableMapOf<String, String>()
        for (matchResult in placeholderRegex.findAll(message)) {
            _placeholders.find { it.localisedInfo.values.any { info -> info.key == matchResult.groupValues[1] } }?.let { placeholder ->
                when (placeholder) {
                    is StaticPlaceholder<T> -> {
                        placeholder.mapping(placeholder, PlaceholderContext(input, locale))?.let {
                            toReplace[matchResult.groupValues[0]] = it
                        }
                    }

                    is ArgsPlaceholder<T> -> {
                        val args = if (matchResult.groupValues[2].isNotEmpty()) {
                            matchResult.groupValues[2].split(",")
                        } else {
                            listOf()
                        }

                        placeholder.mapping(placeholder, PlaceholderContext(input, locale), args)?.let {
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

    fun highlightMessage(message: String): String {
        val toReplace = mutableMapOf<String, String>()
        for (matchResult in placeholderRegex.findAll(message)) {
            val placeholder = _placeholders.find { it.localisedInfo.values.any { info -> info.key == matchResult.groupValues[1] } }
            if (placeholder != null) {
                matchResult.groupValues[0].also { toReplace[it] = "`$it`" }
            } else {
                matchResult.groupValues[0].also { toReplace[it] = "~~$it~~" }
            }
        }
        var newMessage = message
        toReplace.forEach { newMessage = newMessage.replace(it.key, it.value) }
        return newMessage
    }

}

fun <T> placeholders(parentKey: String, init: Placeholders<T>.() -> Unit): Placeholders<T> = Placeholders<T>(parentKey).apply(init)

object PlaceholderObjects {

    @JvmStatic
    val tags = placeholders<CommandContext>("tags") {
        argsPlaceholder("server") { context, args ->
            if (args.size != 1) return@argsPlaceholder context.item.guild.name
            when {
                isArg(context.locale, "id", args[0]) -> context.item.guild.id
                isArg(context.locale, "region", args[0]) -> context.item.guild.region.name
                isArg(context.locale, "owner", args[0]) -> context.item.guild.owner!!.user.asTag
                isArg(context.locale, "member_count", args[0]) -> context.item.guild.memberCache.size().toString()
                else -> null
            }
        }
        argsPlaceholder("sender") { context, args ->
            if (args.size != 1) return@argsPlaceholder context.item.user.asTag
            when {
                isArg(context.locale, "id", args[0]) -> context.item.member.id
                isArg(context.locale, "nickname", args[0]) -> context.item.member.nickname ?: "No nickname!"
                isArg(context.locale, "name", args[0]) -> context.item.user.name
                isArg(context.locale, "full_name", args[0]) -> context.item.user.asTag
                isArg(context.locale, "mention", args[0]) -> context.item.user.asMention
                else -> null
            }
        }
        argsPlaceholder("channel") { context, args ->
            if (args.size != 1) return@argsPlaceholder context.item.channel.name
            when {
                isArg(context.locale, "id", args[0]) -> context.item.channel.id
                isArg(context.locale, "mention", args[0]) -> context.item.channel.asMention
                isArg(context.locale, "topic", args[0]) -> context.item.channel.topic
                isArg(context.locale, "creation", args[0]) -> FormatUtils.formatDateTime(context.item.channel.timeCreated, context.locale)
                isArg(context.locale, "parent", args[0]) -> if (context.item.channel.parent == null) "No channel parent" else context.item.channel.parent!!.name
                else -> null
            }
        }
        staticPlaceholder("time") { context -> FormatUtils.formatDateTime(OffsetDateTime.now(), context.locale) }
        argsPlaceholder("args") { context, args ->
            if (args.isEmpty()) return@argsPlaceholder null
            val argNum = args[0].toIntOrNull() ?: -1
            if (argNum in 0..context.item.args.size) return@argsPlaceholder null

            context.item.getArg(argNum)
        }
    }

    private val greetingsCommon: Placeholders<GenericGuildMemberEvent>.() -> Unit = {
        argsPlaceholder("server") { context, args ->
            if (args.size != 1) return@argsPlaceholder context.item.guild.name
            when {
                isArg(context.locale, "id", args[0]) -> context.item.guild.id
                isArg(context.locale, "owner", args[0]) -> context.item.guild.owner!!.user.asTag
                isArg(context.locale, "member_count", args[0]) -> context.item.guild.memberCache.size().toString()
                else -> null
            }
        }
        argsPlaceholder("user") { context, args ->
            if (args.size != 1) return@argsPlaceholder context.item.user.asTag
            when {
                isArg(context.locale, "id", args[0]) -> context.item.user.id
                isArg(context.locale, "name", args[0]) -> context.item.user.name
                isArg(context.locale, "full_name", args[0]) -> context.item.user.asTag
                isArg(context.locale, "mention", args[0]) -> context.item.user.asMention
                else -> null
            }
        }
        staticPlaceholder("time") { context -> FormatUtils.formatDateTime(OffsetDateTime.now(), Language.getGuildLocale(context.item.guild.idLong)) }
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    val welcomes = placeholders<GuildMemberJoinEvent>("welcomes") {
        greetingsCommon(this as Placeholders<GenericGuildMemberEvent>)
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    val goodbyes = placeholders<GuildMemberRemoveEvent>("goodbyes") {
        greetingsCommon(this as Placeholders<GenericGuildMemberEvent>)
        staticPlaceholder("time_in_guild") { context -> Duration.between(context.item.member.timeJoined, OffsetDateTime.now()).toString() }
    }

}

fun <T> getPlaceholderUsagePage(placeholders: List<Placeholder<T>>, title: String, locale: Locale): PageObjects.EmbedPage {
    val builder = MessagingObjects.getMessageTypeEmbedBuilder(MessageType.INFO)
    val placeholderUsages = placeholders.joinToString("\n") { it.getUsageInfo(locale) }
    builder.setTitle(title)
    builder.setDescription(
            placeholderUsages
                    + "\n\n"
                    + Language.i18n(locale, "placeholders.see_the_docs")
    )
    return PageObjects.EmbedPage(builder)
}