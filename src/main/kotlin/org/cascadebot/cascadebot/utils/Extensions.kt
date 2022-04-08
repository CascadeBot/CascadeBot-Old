/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.data.entities.GuildSettingsModerationEntity
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt

/**
 * Truncates the string to the specified length. If ellipses is enabled,
 * then the string is truncated to `length - 3` and `...` added to the end.
 *
 * @param length The maximum length of the output string.
 * @param ellipses Whether to add ellipses `...` to the end of the string (Not exceeding the maximum length).
 * @return The truncated string.
 */
fun String.truncate(length: Int, ellipses: Boolean = true): String =
        when {
            this.length < length -> this
            ellipses -> this.take(length - 3) + "..."
            else -> this.take(length)
        }

/**
 * Converts a string to lowercase and then capitalizes the first character.
 *
 * @return The string with only the first character capitalized.
 */
fun String.toCapitalized(): String = this.toLowerCase().capitalize()

/**
 * Converts each word to capitalized using the [toCapitalized] function.
 * Each word is converted to lowercase and then the first letter is capitalized.
 *
 * @return The string with each word having its first letter capitalized.
 * @see toCapitalized
 */
fun String.toTitleCase(): String = this.split(" ").joinToString(" ") { it.toCapitalized() }

/**
 * Converts the first letter of a sentence to capitalized using [toCapitalized].
 * Each sentence is converted to lowercase and then the first letter is capitalized.
 *
 * @return The string with each sentence having its first letter capitalized.
 * @see toCapitalized
 */
fun String.toSentenceCase() : String = this.split(".").joinToString(".") { it.toCapitalized() }

/**
 * Converts the double to a percentage to a certain number of decimal places (By default 0) and adds a
 * percent sign at the end.
 *
 * e.g. 0.56789 (1 decimal place) -> "56.8%"
 *
 * @return The double multiplied by 100, rounded to `dp` decimal places and a "%" appended.
 * @see round
 */
fun Double.toPercentage(dp: Int = 0): String {
    return (round((this * 100) * 10.0.pow(dp)) / 10.0.pow(dp)).roundToInt().toString() + "%"
}

/**
 * Gets the muted role for this guild. If the muted role ID is not cached in the guild-data
 * or the role ID that was cached was invalid, a new role is automatically created.
 *
 * @return The Muted role for this guild.
 * @throws InsufficientPermissionException If the logged in account does not have the [Permission.MANAGE_ROLES] Permission.
 * @see getOrCreateMutedRole
 */
fun Guild.getMutedRole(): Role {
    val moderationSettings = CascadeBot.INS.postgresManager.transaction {
        get(GuildSettingsModerationEntity::class.java, idLong)
    } ?: throw UnsupportedOperationException("This shouldn't happen");
    return moderationSettings.muteRoleId?.let { this.getRoleById(it) } ?: getOrCreateMutedRole(this, moderationSettings)
}

/**
 * Attempts to get the muted role that already exists. Using the `muteRoleName` property from the
 * moderation settings, it first attempts to search the role by name. If multiple roles exist with
 * the same name, the first role in the list is used.
 *
 * If no role is found by name, this method creates a role at the highest level the bot is able to.
 *
 * Once an appropriate role is found, the id of the role is put into the `muteRoleId` field in the guild data.
 *
 * @param guild The guild to get the Muted role for.
 * @param guildData The data for the guild in question.
 * @return The Muted role for the specified guild.
 * @throws InsufficientPermissionException If the logged in account does not have the [Permission.MANAGE_ROLES] Permission.
 * @throws IllegalArgumentException If the guildData does not match the guild.
 */
private fun getOrCreateMutedRole(guild: Guild, moderationSettings: GuildSettingsModerationEntity): Role {
    val muteRoleName = moderationSettings.muteRoleName
    val roleByName = guild.getRolesByName(muteRoleName, true)
    return if (roleByName.isEmpty()) {
        guild.createRole().setName(muteRoleName).complete().also {
            guild.modifyRolePositions()
                    .selectPosition(it)
                    .moveTo((guild.selfMember.roles.first()?.position?.minus(1)) ?: 0)
                    .complete()
        }
    } else {
        roleByName[0]
    }.also {
        moderationSettings.muteRoleId = it.idLong
        CascadeBot.INS.postgresManager.transaction {
            save(moderationSettings)
        }
    }
}

/**
 * Converts a Java [Function] to a Kotlin Higher order function `(A)->B`.
 *
 * @return The Kotlin Higher order function that represents the Java function.
 */
fun <A, B> Function<A, B>.toKotlin(): (A) -> B = { this.apply(it) }

/**
 * Converts a Java [Consumer] to a Kotlin Higher order function `(T)->Unit`.
 *
 * @return The Kotlin Higher order function that represents the Java consumer.
 */
fun <T> Consumer<T>.toKotlin(): (T) -> Unit = { this.accept(it) }

/**
 * Converts a Java [Supplier] to a Kotlin Higher order function `()->T`.
 *
 * @return The Kotlin Higher order function that represents the Java supplier.
 */
fun <T> Supplier<T>.toKotlin(): () -> T = { this.get() }

fun Emoji.asString() {
    if (this.isUnicode) {
        this.name
    } else {
        this.id
    }
}