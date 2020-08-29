/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Role
import org.cascadebot.cascadebot.data.managers.GuildDataManager
import org.cascadebot.cascadebot.data.objects.GuildData
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt


fun String.truncate(length: Int, ellipses: Boolean = true): String =
        when {
            this.length < length -> this
            ellipses -> this.take(length - 3) + "..."
            else -> this.take(length)
        }

fun String.toCapitalized(): String = this.toLowerCase().capitalize()

fun String.toTitleCase(): String = this.split(" ").joinToString(" ") { it.toCapitalized() }

fun String.toSentenceCase() : String = this.split(".").joinToString(".") { it.toCapitalized() }

fun Double.toPercentage(dp: Int = 0): String {
    return (round((this * 100) * 10.0.pow(dp)) / 10.0.pow(dp)).roundToInt().toString() + "%"
}

fun Guild.getMutedRole(): Role {
    val guildData = GuildDataManager.getGuildData(this.idLong)
    return this.getRoleById(guildData.mutedRoleId) ?: getOrCreateMutedRole(this, guildData)
}

private fun getOrCreateMutedRole(guild: Guild, guildData: GuildData): Role {
    val muteRoleName = guildData.moderation.muteRoleName
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
    }.also { guildData.mutedRoleId = it.idLong }
}

fun <A, B> Function<A, B>.toKotlin(): (A) -> B = { this.apply(it) }

fun <A> Consumer<A>.toKotlin(): (A) -> Unit = { this.accept(it) }

fun <A> Supplier<A>.toKotlin(): () -> A = { this.get() }