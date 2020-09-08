package org.cascadebot.cascadebot.data.managers


import javassist.NotFoundException
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.IPermissionHolder
import net.dv8tion.jda.api.entities.ISnowflake
import net.dv8tion.jda.api.entities.TextChannel
import java.util.*

object LockManager {
    // perm values: 0 = null/not specified, 1 = false, 2 = true

    fun getPerm(channel: TextChannel, target: ISnowflake): List<Int> {
        // [target, selfMember]
        val perm = mutableListOf(0, 0)
        try {
            if (channel.getPermissionOverride(target as IPermissionHolder)?.denied?.contains(Permission.MESSAGE_WRITE)!!) perm[0] = 1
            if (channel.getPermissionOverride(target)?.allowed?.contains(Permission.MESSAGE_WRITE)!!) perm[0] = 2
            if (channel.getPermissionOverride(channel.guild.selfMember)?.allowed?.contains(Permission.MESSAGE_WRITE)!!) perm[1] = 1
            if (channel.getPermissionOverride(channel.guild.selfMember)?.allowed?.contains(Permission.MESSAGE_WRITE)!!) perm[0] = 2
        } catch (e: NullPointerException) {
            null
        }
        return perm
    }

    private fun add(channel: TextChannel, target: ISnowflake) {
        if (GuildDataManager.getGuildData(channel.guild.idLong).lockedChannels[channel.id] == null) {
            (GuildDataManager.getGuildData(channel.guild.idLong).lockedChannels.put(channel.id, mutableMapOf(Pair(target.id, getPerm(channel, target)))))
        } else {
            GuildDataManager.getGuildData(channel.guild.idLong).lockedChannels[channel.id]!![target.id] = getPerm(channel, target)
        }
    }


    fun lock(channel: TextChannel, target: IPermissionHolder) {
        add(channel, target)
        channel.upsertPermissionOverride(channel.guild.selfMember).grant(Permission.MESSAGE_WRITE).queue()
        channel.upsertPermissionOverride(target).deny(Permission.MESSAGE_WRITE).queue()
    }

    fun unlock(guild: Guild, channel: TextChannel, target: IPermissionHolder) {
        val state = GuildDataManager.getGuildData(channel.guild.idLong).lockedChannels[channel.id]?.get(target.id)
                ?: throw NotFoundException("")
        val empty = EnumSet.noneOf(Permission::class.java)
        val perm = EnumSet.of(Permission.MESSAGE_WRITE)

        val selfPermission = channel.getPermissionOverride(guild.selfMember)?.manager
                ?.grant(if (state[1] == 2) perm else empty)
                ?.deny(if (state[1] == 1) perm else empty)
        if (state[1] == 0) selfPermission?.clear(perm)
        selfPermission?.queue { if (it.allowedRaw == 0L && it.deniedRaw == 0L) it.delete().queue() }

        val targetPermission = channel.getPermissionOverride(target)?.manager
        if (state[0] == 2) targetPermission?.grant(perm)
        if (state[0] == 1) targetPermission?.deny(perm)
        if (state[0] == 0) targetPermission?.clear(perm)
        targetPermission?.queue {
            GuildDataManager.getGuildData(guild.idLong).lockedChannels[channel.idLong.toString()]?.remove(target.idLong.toString())
            if (it.allowedRaw == 0L && it.deniedRaw == 0L) it.delete().queue()
        }
    }

}
