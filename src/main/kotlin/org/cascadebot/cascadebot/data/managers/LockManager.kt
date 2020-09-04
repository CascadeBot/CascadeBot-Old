package org.cascadebot.cascadebot.data.managers


import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.*
import java.util.*

object LockManager {

    // perm values: 0 = null/not specified, 1 = false, 2 = true
    fun add(channel: TextChannel, target: Role) {
        var perm = 0
        if (channel.getPermissionOverride(target)?.denied?.contains(Permission.MESSAGE_WRITE)!!) perm = 1
        if (channel.getPermissionOverride(target)?.allowed?.contains(Permission.MESSAGE_WRITE)!!) perm = 2
        GuildDataManager.getGuildData(channel.guild.idLong).lockedChannels[channel.id]?.put(target.id, perm)
    }

    fun add(channel: TextChannel) {
        var perm = 0
        if (channel.getPermissionOverride(channel.guild.publicRole)?.denied?.contains(Permission.MESSAGE_WRITE)!!) perm = 1
        if (channel.getPermissionOverride(channel.guild.publicRole)?.allowed?.contains(Permission.MESSAGE_WRITE)!!) perm = 2
        GuildDataManager.getGuildData(channel.guild.idLong).lockedChannels[channel.id]?.put(channel.guild.publicRole.id, perm)
    }

    fun add(channel: TextChannel, target: Member) {
        var perm = 0
        if (channel.getPermissionOverride(target)?.denied?.contains(Permission.MESSAGE_WRITE)!!) perm = 1
        if (channel.getPermissionOverride(target)?.allowed?.contains(Permission.MESSAGE_WRITE)!!) perm = 2
        GuildDataManager.getGuildData(channel.guild.idLong).lockedChannels[channel.id]?.put(target.id, perm)

    }

    fun unlock(guild: Guild, channel: TextChannel, target: ISnowflake) {
        val state = GuildDataManager.getGuildData(guild.idLong).lockedChannels[channel.id]?.get(target.id)
        val empty = EnumSet.noneOf(net.dv8tion.jda.api.Permission::class.java)
        val perm = EnumSet.of(Permission.MESSAGE_WRITE)

        channel.getPermissionOverride(target as IPermissionHolder)?.manager
                ?.setAllow(if (state == 2) perm else empty)
                ?.setDeny(if (state == 1) perm else empty)
                ?.clear(if (state == 0) perm else empty)
                ?.queue { GuildDataManager.getGuildData(guild.idLong).lockedChannels[channel.idLong.toString()]?.remove(target.idLong.toString()) }
    }

}
