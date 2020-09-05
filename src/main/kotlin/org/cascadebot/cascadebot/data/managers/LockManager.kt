package org.cascadebot.cascadebot.data.managers


import javassist.NotFoundException
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.IPermissionHolder
import net.dv8tion.jda.api.entities.ISnowflake
import net.dv8tion.jda.api.entities.TextChannel
import java.util.*

object LockManager {

    private fun getPerm(channel: TextChannel, target: ISnowflake): Int {
        var perm = 0
        try {
            if (channel.getPermissionOverride(target as IPermissionHolder)?.denied?.contains(Permission.MESSAGE_WRITE)!!) perm = 1
            if (channel.getPermissionOverride(target)?.allowed?.contains(Permission.MESSAGE_WRITE)!!) perm = 2
        } catch (e: NullPointerException) {
            null
        }
        return perm
    }

    // perm values: 0 = null/not specified, 1 = false, 2 = true
    fun add(channel: TextChannel, target: ISnowflake) {
        GuildDataManager.getGuildData(channel.guild.idLong).lockedChannels[channel.id] = mutableMapOf(Pair(target.id, getPerm(channel, target)))
    }

    fun add(channel: TextChannel) {
        GuildDataManager.getGuildData(channel.guild.idLong).lockedChannels[channel.id] = mutableMapOf(Pair(channel.guild.publicRole.id, getPerm(channel, channel.guild.publicRole)))
    }
    
    fun unlock(guild: Guild, channel: TextChannel, target: ISnowflake) {
        val state = GuildDataManager.getGuildData(channel.guild.idLong).lockedChannels[channel.id]?.get(target.id)
                ?: throw NotFoundException("")
        val empty = EnumSet.noneOf(net.dv8tion.jda.api.Permission::class.java)
        val perm = EnumSet.of(Permission.MESSAGE_WRITE)

        channel.getPermissionOverride(target as IPermissionHolder)?.manager
                ?.setAllow(if (state == 2) perm else empty)
                ?.setDeny(if (state == 1) perm else empty)
                ?.clear(if (state == 0) perm else empty)
                ?.queue { GuildDataManager.getGuildData(guild.idLong).lockedChannels[channel.idLong.toString()]?.remove(target.idLong.toString()) }
    }

}
