/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.TextChannel
import java.util.Collections

class CommandFilter(val name: String) {

    // Constructor for MongoDB
    private constructor() : this("")

    var type = FilterType.BLACKLIST
    var operator = FilterOperator.AND

    var enabled = false

    val commands: MutableList<String> = Collections.synchronizedList(listOf())
    val channelIds: MutableList<Long> = Collections.synchronizedList(listOf())
    val userIds: MutableList<Long> = Collections.synchronizedList(listOf())
    val roleIds: MutableList<Long> = Collections.synchronizedList(listOf())

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

    val filterEmbed: EmbedBuilder
        get() = EmbedBuilder()

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
