/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.TextChannel
import org.bson.BsonDocument
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.data.Config
import org.cascadebot.cascadebot.data.database.BsonObject
import org.cascadebot.cascadebot.data.language.Language
import org.cascadebot.cascadebot.data.language.Locale
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.messaging.embed
import org.cascadebot.cascadebot.utils.ifContainsArray
import org.cascadebot.cascadebot.utils.ifContainsBoolean
import org.cascadebot.cascadebot.utils.ifContainsString
import org.cascadebot.cascadebot.utils.language.LanguageUtils
import org.cascadebot.cascadebot.utils.toCapitalized
import java.util.Collections

class CommandFilter(val name: String) : BsonObject {

    // Constructor for MongoDB
    private constructor() : this("")

    var type = FilterType.BLACKLIST
    var operator = FilterOperator.AND

    var enabled = true

    val commands: MutableSet<String> = Collections.synchronizedSet(mutableSetOf())
    val channelIds: MutableSet<Long> = Collections.synchronizedSet(mutableSetOf())
    val userIds: MutableSet<Long> = Collections.synchronizedSet(mutableSetOf())
    val roleIds: MutableSet<Long> = Collections.synchronizedSet(mutableSetOf())

    val configured: Boolean
        get() = commands.isNotEmpty() && (channelIds.isNotEmpty() || userIds.isNotEmpty() || roleIds.isNotEmpty())

    val statusEmote: String
        get() = when {
            !configured -> Config.INS.globalEmotes["offline"]?.let { CascadeBot.INS.shardManager.getEmoteById(it)?.asMention }
            enabled -> Config.INS.globalEmotes["online"]?.let { CascadeBot.INS.shardManager.getEmoteById(it)?.asMention }
            else -> Config.INS.globalEmotes["dnd"]?.let { CascadeBot.INS.shardManager.getEmoteById(it)?.asMention }
        } ?: ""

    fun evaluateFilter(command: String, channel: TextChannel, member: Member): FilterResult {
        if (!enabled) {
            return FilterResult.NEUTRAL
        }
        if (!commands.contains(command)) {
            return FilterResult.NEUTRAL
        }

        var channelMatch = FilterMatch.NEUTRAL
        // The channel condition is only considered if one or more channels have been added to the filter
        if (channelIds.size != 0) {
            channelMatch = if (channelIds.contains(channel.idLong)) FilterMatch.MATCH else FilterMatch.NOT_MATCH
        }

        var userMatch = FilterMatch.NEUTRAL
        // The user condition is only considered if one or more users have been added to the filter
        if (userIds.size != 0) {
            userMatch = if (userIds.contains(member.idLong)) FilterMatch.MATCH else FilterMatch.NOT_MATCH
        }

        var roleMatch = FilterMatch.NEUTRAL
        // The role condition is only considered if one or more roles have been added to the filter
        if (roleIds.size != 0) {
            roleMatch = if (member.roles.stream().map { obj: Role -> obj.idLong }.anyMatch { id: Long -> roleIds.contains(id) }) FilterMatch.MATCH else FilterMatch.NOT_MATCH
        }

        val combinedResult: Boolean = if (operator == FilterOperator.AND) {
            // Check that all of the results are either MATCH or NEUTRAL
            channelMatch != FilterMatch.NOT_MATCH && userMatch != FilterMatch.NOT_MATCH && roleMatch != FilterMatch.NOT_MATCH
        } else {
            // Check that any of the results are either MATCH or NEUTRAL
            channelMatch != FilterMatch.NOT_MATCH || userMatch != FilterMatch.NOT_MATCH || roleMatch != FilterMatch.NOT_MATCH
        }
        return when (type) {
            FilterType.WHITELIST -> if (combinedResult) FilterResult.ALLOW else FilterResult.DENY
            FilterType.BLACKLIST -> if (combinedResult) FilterResult.DENY else FilterResult.ALLOW
        }
    }

    fun getFilterEmbed(locale: Locale): EmbedBuilder = embed(MessageType.NEUTRAL) {
        title {
            name = this@CommandFilter.name
        }
        author {
            name = "Command Filter"
        }

        val commandText = if (commands.isEmpty()) {
            locale.i18n("commands.filters.no_commands")
        } else {
            locale.i18n("commands.filters.commands_list", commands.size, commands.joinToString(", ") { "`${Language.i18n(locale, "commands.$it.command")}`" })
        }

        color = if (!configured) null else {
            if (enabled) MessageType.SUCCESS.color else MessageType.DANGER.color
        }

        description = Language.i18n(
                locale,
                "commands.filters.embed_description",
                LanguageUtils.i18nEnum(operator, locale),
                locale.i18n("commands.filters.op_${operator.name.toLowerCase()}_description"),
                commandText,
                LanguageUtils.i18nEnum(type, locale),
                locale.i18n("commands.filters.type_${type.name.toLowerCase()}_description")
        )

        val conditions: String = if (channelIds.isEmpty() && roleIds.isEmpty() && userIds.isEmpty()) {
            locale.i18n("commands.filters.no_conditions")
        } else {
            val conditionsBuilder = StringBuilder()
            if (channelIds.isNotEmpty()) {
                conditionsBuilder
                        .append(locale.i18n("words.channels").toCapitalized())
                        .append(": ")
                        .append(channelIds.joinToString(", ") { "<#$it>" })
                        .append("\n")
            }
            if (roleIds.isNotEmpty()) {
                conditionsBuilder
                        .append("*${LanguageUtils.i18nEnum(operator, locale)}*")
                        .append("\n")
                        .append(locale.i18n("words.roles").toCapitalized())
                        .append(": ")
                        .append(roleIds.joinToString(", ") { "<@&$it>" })
                        .append("\n")
            }
            if (userIds.isNotEmpty()) {
                conditionsBuilder
                        .append("*${LanguageUtils.i18nEnum(operator, locale)}*")
                        .append("\n")
                        .append(locale.i18n("words.users").toCapitalized())
                        .append(": ")
                        .append(userIds.joinToString(", ") { "<@$it>" })
                        .append("\n")
            }
            conditionsBuilder.toString()
        }

        field {
            name = locale.i18n("words.conditions").toCapitalized()
            value = conditions
        }

        field {
            name = locale.i18n("words.status").toCapitalized()
            value = "$statusEmote " + if (!configured) {
                if (commands.isEmpty()) {
                    locale.i18n("words.not_configured").toCapitalized() + " - " + locale.i18n("commands.filters.not_configured_commands")
                } else {
                    locale.i18n("words.not_configured").toCapitalized() + " - " + locale.i18n("commands.filters.not_configured_conditions")
                }
            } else {
                locale.i18n("words.${if (enabled) "enabled" else "disabled"}").toCapitalized()
            }
        }
    }


    /**
     * Determines whether users who match this filter will be blocked or whitelisted
     */
    enum class FilterType {

        WHITELIST, BLACKLIST
    }

    /**
     * Determines whether the channel, roles and user properties all have to be matched or just one of them.
     */
    enum class FilterOperator {

        AND, OR
    }

    /**
     * Whether this filter explicitly allows or denies the user. Also has a neutral option which has no effect on the outcome.
     */
    enum class FilterResult {

        ALLOW, DENY, NEUTRAL
    }

    /**
     * Whether the condition matches or not. Also has a neutral option which has no effect on the outcome.
     */
    enum class FilterMatch {

        MATCH, NOT_MATCH, NEUTRAL
    }

    override fun fromBson(bsonDocument: BsonDocument) {
        bsonDocument.ifContainsString("type") {
            type = FilterType.valueOf(it)
        }
        bsonDocument.ifContainsString("operator") {
            operator = FilterOperator.valueOf(it)
        }
        bsonDocument.ifContainsBoolean("enabled") {
            enabled = it
        }
        bsonDocument.ifContainsArray("commands") {
            commands.clear()
            for (commandBson in it) {
                commands.add(commandBson.asString().value)
            }
        }
        bsonDocument.ifContainsArray("channelIds") {
            channelIds.clear()
            for (idBson in it) {
                channelIds.add(idBson.asNumber().longValue())
            }
        }
        bsonDocument.ifContainsArray("userIds") {
            userIds.clear()
            for (idBson in it) {
                userIds.add(idBson.asNumber().longValue())
            }
        }
        bsonDocument.ifContainsArray("roleIds") {
            roleIds.clear()
            for (idBson in it) {
                roleIds.add(idBson.asNumber().longValue())
            }
        }
    }

}
