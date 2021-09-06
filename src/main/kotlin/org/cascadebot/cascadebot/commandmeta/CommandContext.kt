package org.cascadebot.cascadebot.commandmeta

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Emote
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.GuildChannel
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.SelfUser
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import org.apache.commons.lang3.ArrayUtils
import org.apache.commons.lang3.StringUtils
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.data.Config
import org.cascadebot.cascadebot.data.language.Language
import org.cascadebot.cascadebot.data.language.Locale
import org.cascadebot.cascadebot.data.objects.ArgumentType
import org.cascadebot.cascadebot.data.objects.GuildData
import org.cascadebot.cascadebot.data.objects.GuildSettingsCore
import org.cascadebot.cascadebot.messaging.MessagingDirectMessage
import org.cascadebot.cascadebot.messaging.MessagingTimed
import org.cascadebot.cascadebot.messaging.MessagingTyped
import org.cascadebot.cascadebot.messaging.MessagingUI
import org.cascadebot.cascadebot.music.CascadePlayer
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.utils.interactions.InteractionMessage

data class CommandContext(
    val jda: JDA,
    val channel: TextChannel,
    val message: InteractionMessage,
    val guild: Guild,
    val data: GuildData,
    val member: Member,
    var trigger: String,
    val mention: Boolean
) {
    val typedMessaging = MessagingTyped(this)
    val timedMessaging = MessagingTimed(this)
    val uiMessaging = MessagingUI(this)
    val directMessaging = MessagingDirectMessage(this)

    val musicPlayer: CascadePlayer
        get() = CascadeBot.INS.musicHandler.getPlayer(guild.idLong)!!

    val user: User
        get() = member.user

    val selfUser: SelfUser
        get() = jda.selfUser

    val selfMember: Member
        get() = guild.getMember(selfUser)!!

    fun reply(message: String) {
        require(!message.isBlank()) { "The message cannot be blank!" }
        channel.sendMessage(message).queue()
    }

    fun reply(embed: MessageEmbed) {
        channel.sendMessage(embed).queue()
    }

    fun i18n(path: String, vararg args: Any): String {
        return Language.i18n(guild.idLong, path, *args)
    }

    /**
     * Checks the permission for the member and channel provided for the context.
     * Usually this is the channel a command was sent in and the member who send the command.
     *
     * @param permissions Non-null and non empty permissions to check.
     * @return true if the member has all of the specified permissions in the channel.
     * @throws IllegalArgumentException if permissions are empty or null.
     */
    fun hasPermission(vararg permissions: Permission?): Boolean {
        check(permissions.isNotEmpty()) { "Permissions cannot be empty!" }
        return member.hasPermission(channel, *permissions)
    }

    /**
     * Checks the permissions for the specified member in the channel provided for this context.
     *
     * @param member      the non-null member to check permissions for. The member needs to be in the same guild as the guild in the context.
     * @param permissions permissions Non-null and non empty permissions to check.
     * @return true if the member has all of the specified permissions in the channel.
     * @throws IllegalArgumentException if member is null or not in the same guild.
     * @throws IllegalArgumentException if permissions are empty or null.
     */
    fun hasPermission(member: Member, vararg permissions: Permission): Boolean {
        check(member.guild.idLong == guild.idLong) { "Member needs to be in the same guild as this context! Guild ID: ${guild.id}" }
        check(permissions.isNotEmpty()) { "Permissions cannot be empty!" }
        return this.member.hasPermission(channel, *permissions)
    }

    fun hasPermission(permission: String?): Boolean {
        return hasPermission(member, permission)
    }

    fun hasPermission(member: Member?, permission: String?): Boolean {
        val cascadePermission = CascadeBot.INS.permissionsManager.getPermission(permission)
        if (cascadePermission == null) {
            CascadeBot.LOGGER.warn("Could not check permission {} as it does not exist!!", permission)
            return false
        }
        return data.management.permissions.hasPermission(member, channel, cascadePermission, data.core)
    }

    fun hasPermission(permission: CascadePermission?): Boolean {
        return permission != null && data.management.permissions.hasPermission(member, channel, permission, data.core)
    }

    fun hasPermission(member: Member?, channel: GuildChannel?, permission: CascadePermission?): Boolean {
        return permission != null && data.management.permissions.hasPermission(member, channel, permission, data.core)
    }

    //endregion
    //region Helper Methods
    private fun getGlobalEmote(key: String): Emote? {
        val emoteId = Config.INS.globalEmotes[key]
        if (emoteId != null) {
            return CascadeBot.INS.shardManager.getEmoteById(emoteId)
        }
        CascadeBot.LOGGER.warn("Tried to get global emote that doesn't exist! Key: {}", key)
        return null
    }

    fun globalEmote(key: String): String? {
        return getGlobalEmote(key)?.asMention ?: ""
    }

}
