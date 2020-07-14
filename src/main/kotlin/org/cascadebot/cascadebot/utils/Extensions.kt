/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Role
import org.cascadebot.cascadebot.data.managers.GuildDataManager
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt


fun String.truncate(length: Int, ellipses: Boolean = true): String =
        when {
            this.length < length -> this
            ellipses -> this.take(length - 3) + "..."
            else -> this.take(length)
        }

fun Double.toPercentage(dp: Int = 0): String {
    return (round((this * 100) * 10.0.pow(dp)) / 10.0.pow(dp)).roundToInt().toString() + "%"
}

fun Guild.getMutedRole(): Role {
    val muteRoleName = GuildDataManager.getGuildData(this.idLong).moderation.muteRoleName
    val roleByName = this.getRolesByName(muteRoleName, true)
    return if (roleByName.isEmpty()) {
        this.createRole().setName(muteRoleName).complete()
    } else {
        roleByName[0]
    }
}