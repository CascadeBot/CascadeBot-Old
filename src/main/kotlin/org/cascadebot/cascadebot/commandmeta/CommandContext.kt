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
import org.cascadebot.cascadebot.data.objects.guild.GuildData
import org.cascadebot.cascadebot.data.objects.guild.GuildSettingsCore
import org.cascadebot.cascadebot.data.objects.ArgumentType
import org.cascadebot.cascadebot.messaging.MessagingDirectMessage
import org.cascadebot.cascadebot.messaging.MessagingTimed
import org.cascadebot.cascadebot.messaging.MessagingTyped
import org.cascadebot.cascadebot.messaging.MessagingUI
import org.cascadebot.cascadebot.music.CascadePlayer
import org.cascadebot.cascadebot.permissions.CascadePermission

class CommandContext(
        val command: ICommandExecutable,
        val jda: JDA,
        val channel: TextChannel,
        val message: Message,
        val guild: Guild,
        val data: GuildData,
        val args: Array<String>,
        val member: Member,
        var trigger: String,
        val mention: Boolean
) {
    val typedMessaging = MessagingTyped(this)
    val timedMessaging = MessagingTimed(this)
    val uiMessaging = MessagingUI(this)
    val directMessaging = MessagingDirectMessage(this)

    val locale: Locale
        get() = data.locale

    val musicPlayer: CascadePlayer
        get() = CascadeBot.INS.musicHandler.getPlayer(guild.idLong)!!

    val user: User
        get() = member.user

    val selfUser: SelfUser
        get() = jda.selfUser

    val selfMember: Member
        get() = guild.getMember(selfUser)!!

    @JvmOverloads
    fun getMessage(start: Int, end: Int = args.size): String {
        return ArrayUtils.subarray(args, start, end).joinToString(" ")
    }

    fun getArg(index: Int): String = args[index]

    fun isArgInteger(index: Int): Boolean = args[index].toIntOrNull() == null

    fun getArgAsInteger(index: Int): Int? = args[index].toIntOrNull()

    fun isArgLong(index: Int): Boolean = args[index].toLongOrNull() == null

    fun getArgAsLong(index: Int): Long? = args[index].toLongOrNull()

    @Deprecated("This is only here for Java interop. Should not be used in Kotlin!", ReplaceWith("data.coreSettings"))
    fun getCoreSettings() : GuildSettingsCore = data.coreSettings

    /**
     * Tests for an argument of a particular id. This check it exists at the position and,
     * if the argument is a command arg, whether the localised command matches the input.
     *
     * @param id The argument id relative to the command
     * @return Whether the argument is present and correct in the arguments
     */
    fun testForArg(id: String): Boolean {
        val requiredArgsCount = StringUtils.countMatches(id, '.') + 1
        /*
            Tests to make sure that we're not trying to get an argument out of range

            For command of ;test <user> command
            If id given is user.command and args.length is 1 or 0, then the number of separators + 1
            (1 in this case) will be greater than to the number of args so we return false since
            there could not physically be an arg at that position.

            This guarantees there will always be an arg to check at the position.
            It's a lazy check because it doesn't check arguments after, that is the role of the command
            to check the arg length explicitly.
         */
        if (args.size < requiredArgsCount) return false
        val argId = command.absoluteCommand + "." + id
        val argument = CascadeBot.INS.argumentManager.getArgument(argId) ?: return false
        // If the argument doesn't exist, it can't be valid!
        if (argument.type != ArgumentType.COMMAND) {
            // If it's not a command, we know that the arg exists so return true.
            return true
        }

        return args[requiredArgsCount - 1].equals(argument.name(locale), ignoreCase = true)
    }

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

    fun getUsage(): String? {
        return getUsage(command)
    }

    fun getUsage(command: ICommandExecutable): String? {
        val parentArg = CascadeBot.INS.argumentManager.getArgument(command.absoluteCommand)
        return if (parentArg != null) {
            var parent: String? = null
            if (command is ISubCommand) {
                parent = command.parent()
            }
            val commandString: String = data.coreSettings.prefix + if (parent == null) "" else "$parent "
            parentArg.getUsageString(locale, commandString)
        } else {
            "`" + data.coreSettings.prefix + command.command(locale) + "` - " + command.description(locale)
        }
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
        return data.permissionSettings.hasPermission(member, channel, cascadePermission, data.coreSettings)
    }

    fun hasPermission(permission: CascadePermission?): Boolean {
        return permission != null && data.permissionSettings.hasPermission(member, channel, permission, data.coreSettings)
    }

    fun hasPermission(member: Member?, channel: GuildChannel?, permission: CascadePermission?): Boolean {
        return permission != null && data.permissionSettings.hasPermission(member, channel, permission, data.coreSettings)
    }

    fun runOtherCommand(command: String?, sender: Member?, context: CommandContext) {
        val commandMain = CascadeBot.INS.commandManager.getCommandByDefault(command)
                ?: throw IllegalArgumentException("Cannot find that command!")
        if (hasPermission(commandMain.permission)) {
            commandMain.onCommand(member, context)
        } else {
            context.uiMessaging.sendPermissionError(commandMain.permission)
        }
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
