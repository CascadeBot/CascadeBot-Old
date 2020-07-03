/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.TextChannel
import org.cascadebot.cascadebot.data.language.Language
import org.cascadebot.cascadebot.data.language.Locale
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.messaging.embed
import org.cascadebot.cascadebot.utils.language.LanguageUtils
import org.cascadebot.cascadebot.utils.toCapitalized
import org.jsoup.select.NodeFilter
import java.lang.StringBuilder
import java.util.Collections

class CommandFilter(val name: String) {

    // Constructor for MongoDB
    private constructor() : this("")

    var type = FilterType.BLACKLIST
    var operator = FilterOperator.AND

    var enabled = true

    val commands: MutableSet<String> = Collections.synchronizedSet(mutableSetOf())
    val channelIds: MutableSet<Long> = Collections.synchronizedSet(mutableSetOf())
    val userIds: MutableSet<Long> = Collections.synchronizedSet(mutableSetOf())
    val roleIds: MutableSet<Long> = Collections.synchronizedSet(mutableSetOf())

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

        val commandList = if (commands.isEmpty()) {
            locale.i18n("commands.filters.no_commands")
        } else {
            commands.joinToString(", ") { "`${Language.i18n(locale, "commands.$it.name")}`" }
        }

        description = Language.i18n(
                locale,
            "commands.filters.embed_description",
                LanguageUtils.i18nEnum(operator, locale),
                commands.size,
                commandList,
                LanguageUtils.i18nEnum(type, locale),
                locale.i18n("commands.filters.${type.name.toLowerCase()}_description")
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
                        .append("*${LanguageUtils.i18nEnum(operator, locale)}*")
                        .append("\n")
            }
            if (roleIds.isNotEmpty()) {
                conditionsBuilder
                        .append(locale.i18n("words.roles").toCapitalized())
                        .append(": ")
                        .append(roleIds.joinToString(", ") { "<@&$it>" })
                        .append("\n")
                        .append("*${LanguageUtils.i18nEnum(operator, locale)}*")
                        .append("\n")
            }
            if (userIds.isNotEmpty()) {
                conditionsBuilder
                        .append(locale.i18n("words.users").toCapitalized())
                        .append(": ")
                        .append(userIds.joinToString(", ") { "<@$it>" })
                        .append("\n")
                        .append("*${LanguageUtils.i18nEnum(operator, locale)}*")
                        .append("\n")
            }
            conditionsBuilder.toString()
        }

        field {
            name = locale.i18n("words.conditions").toCapitalized()
            value = conditions
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

}
