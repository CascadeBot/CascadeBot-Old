package org.cascadebot.cascadebot.data.managers


import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.IPermissionHolder
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction
import java.time.OffsetDateTime
import java.util.Date
import java.util.EnumSet
import java.util.concurrent.CompletableFuture

enum class Status {
    ALLOW,
    DENY,
    NEUTRAL;

    fun apply(action: PermissionOverrideAction, perm: EnumSet<Permission>) {
        when (this) {
            ALLOW -> action.grant(perm)
            DENY -> action.deny(perm)
            NEUTRAL -> action.clear(perm)
        }
    }

}

data class LockPermissionState(
    val target: Status,
    val selfMember: Status,
    val createdAt: Date = Date()
) {

    private constructor() : this(Status.NEUTRAL, Status.NEUTRAL)

}

object LockManager {

    fun getPerm(channel: TextChannel, target: IPermissionHolder): LockPermissionState {
        var perm = LockPermissionState(Status.NEUTRAL, Status.NEUTRAL)

        val targetOverride = channel.getPermissionOverride(target)
        if (targetOverride != null) {
            if (targetOverride.allowed.contains(Permission.MESSAGE_WRITE)) perm = perm.copy(target = Status.ALLOW)
            if (targetOverride.denied.contains(Permission.MESSAGE_WRITE)) perm = perm.copy(target = Status.DENY)
        }

        val selfMemberOverride = channel.getPermissionOverride(channel.guild.selfMember)
        if (selfMemberOverride != null) {
            if (selfMemberOverride.allowed.contains(Permission.MESSAGE_WRITE)) perm = perm.copy(selfMember = Status.ALLOW)
            if (selfMemberOverride.denied.contains(Permission.MESSAGE_WRITE)) perm = perm.copy(selfMember = Status.DENY)
        }
        return perm
    }

    private fun storePermissions(channel: TextChannel, target: IPermissionHolder) {
        val lockedChannels = GuildDataManager.getGuildData(channel.guild.idLong).lockedChannels
        val mutableMap = lockedChannels[channel.id]
        if (mutableMap == null) {
            lockedChannels[channel.id] = mutableMapOf(Pair(target.id, getPerm(channel, target)))
        } else {
            mutableMap[target.id] = getPerm(channel, target)
        }
    }

    fun lock(channel: TextChannel, target: IPermissionHolder, success: () -> Unit, failure: (Throwable) -> Unit) {
        storePermissions(channel, target)

        CompletableFuture.allOf(
            channel.upsertPermissionOverride(channel.guild.selfMember).grant(Permission.MESSAGE_WRITE).submit(),
            channel.upsertPermissionOverride(target).deny(Permission.MESSAGE_WRITE).submit()
        ).handle { _, throwable -> if (throwable == null) success() else failure(throwable) }
    }

    fun isLocked(channel: TextChannel, target: IPermissionHolder): Boolean {
        val lockedChannels = GuildDataManager.getGuildData(channel.guild.idLong).lockedChannels
        val mutableMap = lockedChannels[channel.id] ?: return false
        return mutableMap.containsKey(target.id)
    }

    fun unlock(guild: Guild, channel: TextChannel, target: IPermissionHolder, success: (Boolean) -> Unit, failure: (Throwable) -> Unit) {
        val previousState = GuildDataManager.getGuildData(channel.guild.idLong).lockedChannels[channel.id]?.get(target.id)
        // If there is no state to restore, we can't do anything!
            ?: return success(false)
        val perm = EnumSet.of(Permission.MESSAGE_WRITE)

        val futures: MutableList<CompletableFuture<*>> = mutableListOf()

        val selfPermissionAction = channel.getPermissionOverride(guild.selfMember)?.manager
        if (selfPermissionAction != null) {
            // Apply the self member's previous state
            previousState.selfMember.apply(selfPermissionAction, perm)
            // Apply the changes to the permission override
            val submit = selfPermissionAction.submit()
            // If the permission override has no permissions set after we have reset the perms, delete it to tidy.
            submit.thenAccept { if (it.allowedRaw == 0L && it.deniedRaw == 0L) it.delete().queue() }
            futures.add(submit)
        }

        val targetPermissionAction = channel.getPermissionOverride(target)?.manager
        if (targetPermissionAction != null) {
            // Apply the target's previous state
            previousState.target.apply(targetPermissionAction, perm)
            // Apply thr changes to the permission override
            val submit = targetPermissionAction.submit()
            // Remove the locked channels entry for this permission override and delete the
            // override if there are no permissions left on it to tidy.
            submit.thenAccept {
                GuildDataManager.getGuildData(guild.idLong).lockedChannels[channel.idLong.toString()]?.remove(target.idLong.toString())
                if (it.allowedRaw == 0L && it.deniedRaw == 0L) it.delete().queue()
            }
            futures.add(submit)
        }

        val bothFutures = CompletableFuture.allOf(*futures.toTypedArray())

        bothFutures.handle { _, throwable ->
            if (throwable == null) {
                success(true)
            } else {
                failure(throwable)
            }
        }

        if (futures.isEmpty()) {
            success(false)
        }
    }

}
