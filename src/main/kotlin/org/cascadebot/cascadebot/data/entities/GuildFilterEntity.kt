package org.cascadebot.cascadebot.data.entities

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.TextChannel
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.data.Config
import org.cascadebot.cascadebot.data.language.Language
import org.cascadebot.cascadebot.data.language.Locale
import org.cascadebot.cascadebot.data.objects.CommandFilter
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.messaging.embed
import org.cascadebot.cascadebot.utils.language.LanguageUtils
import org.cascadebot.cascadebot.utils.toCapitalized
import org.hibernate.annotations.Type
import java.io.Serializable
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.IdClass
import javax.persistence.JoinColumn
import javax.persistence.JoinColumns
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "guild_filter")
@IdClass(GuildFilterId::class)
class GuildFilterEntity(
    name: String,
    guildId: Long,
    type: CommandFilter.FilterType,
    operator: CommandFilter.FilterOperator
) {

    @Id
    @Column(name = "name")
    val name: String = name

    @Id
    @Column(name = "guild_id")
    val guildId: Long = guildId

    @Column(name = "enabled", nullable = false)
    var enabled: Boolean = true

    @Column(name = "type", nullable = false)
    @Type(type = "psql-enum")
    @Enumerated(EnumType.STRING)
    var type: CommandFilter.FilterType = type

    @Column(name = "operator", nullable = false)
    @Type(type = "psql-enum")
    @Enumerated(EnumType.STRING)
    var operator: CommandFilter.FilterOperator = operator

    @Column(name = "commands", columnDefinition = "varchar(255)[]", nullable = false)
    @Type(type = "list-array")
    val commands: MutableList<String> = mutableListOf()

    @OneToMany
    @JoinColumns(
        value = [JoinColumn(name = "filter_name"), JoinColumn(name = "guild_id")]
    )
    val channels: MutableList<GuildFilterChannelEntity> = mutableListOf()

    @OneToMany
    @JoinColumns(
        value = [JoinColumn(name = "filter_name"), JoinColumn(name = "guild_id")]
    )
    val users: MutableList<GuildFilterUserEntity> = mutableListOf()

    @OneToMany
    @JoinColumns(
        value = [JoinColumn(name = "filter_name"), JoinColumn(name = "guild_id")]
    )
    val roles: MutableList<GuildFilterRoleEntity> = mutableListOf()

    val configured: Boolean
        get() = commands.isNotEmpty() && (channels.isNotEmpty() || users.isNotEmpty() || roles.isNotEmpty())

    val statusEmote: String
        get() = when {
            !configured -> Config.INS.globalEmotes["offline"]?.let { CascadeBot.INS.shardManager.getEmoteById(it)?.asMention }
            enabled -> Config.INS.globalEmotes["online"]?.let { CascadeBot.INS.shardManager.getEmoteById(it)?.asMention }
            else -> Config.INS.globalEmotes["dnd"]?.let { CascadeBot.INS.shardManager.getEmoteById(it)?.asMention }
        } ?: ""

    fun evaluateFilter(command: String, channel: TextChannel, member: Member): CommandFilter.FilterResult {
        if (!enabled) {
            return CommandFilter.FilterResult.NEUTRAL
        }
        if (!commands.contains(command)) {
            return CommandFilter.FilterResult.NEUTRAL
        }

        var channelMatch = CommandFilter.FilterMatch.NEUTRAL
        // The channel condition is only considered if one or more channels have been added to the filter
        if (channels.size != 0) {
            channelMatch = if (channels.contains(GuildFilterChannelEntity(name, guildId, channel.idLong))) CommandFilter.FilterMatch.MATCH else CommandFilter.FilterMatch.NOT_MATCH
        }

        var userMatch = CommandFilter.FilterMatch.NEUTRAL
        // The user condition is only considered if one or more users have been added to the filter
        if (users.size != 0) {
            userMatch = if (users.contains(GuildFilterUserEntity(name, guildId, member.idLong))) CommandFilter.FilterMatch.MATCH else CommandFilter.FilterMatch.NOT_MATCH
        }

        var roleMatch = CommandFilter.FilterMatch.NEUTRAL
        // The role condition is only considered if one or more roles have been added to the filter
        if (roles.size != 0) {
            roleMatch = if (member.roles.stream().map { obj: Role -> obj.idLong }.anyMatch { id: Long -> roles.contains(GuildFilterRoleEntity(name, guildId, id)) }) CommandFilter.FilterMatch.MATCH else CommandFilter.FilterMatch.NOT_MATCH
        }

        val combinedResult: Boolean = if (operator == CommandFilter.FilterOperator.AND) {
            // Check that all of the results are either MATCH or NEUTRAL
            channelMatch != CommandFilter.FilterMatch.NOT_MATCH && userMatch != CommandFilter.FilterMatch.NOT_MATCH && roleMatch != CommandFilter.FilterMatch.NOT_MATCH
        } else {
            // Check that any of the results are either MATCH or NEUTRAL
            channelMatch != CommandFilter.FilterMatch.NOT_MATCH || userMatch != CommandFilter.FilterMatch.NOT_MATCH || roleMatch != CommandFilter.FilterMatch.NOT_MATCH
        }
        return when (type) {
            CommandFilter.FilterType.WHITELIST -> if (combinedResult) CommandFilter.FilterResult.ALLOW else CommandFilter.FilterResult.DENY
            CommandFilter.FilterType.BLACKLIST -> if (combinedResult) CommandFilter.FilterResult.DENY else CommandFilter.FilterResult.ALLOW
        }
    }

    fun getFilterEmbed(locale: Locale): EmbedBuilder = embed(MessageType.NEUTRAL) {
        title {
            name = this@GuildFilterEntity.name
        }
        author {
            name = "Command Filter"
        }

        val commandText = if (commands.isEmpty()) {
            locale.i18n("commands.filters.no_commands")
        } else {
            locale.i18n("commands.filters.commands_list", commands.size, commands.joinToString(", ") { "`${Language.i18n(locale, "commands.$it.command")}`" })
        }

        color = if (!configured) null else {
            if (enabled) MessageType.SUCCESS.color else MessageType.DANGER.color
        }

        description = Language.i18n(
            locale,
            "commands.filters.embed_description",
            LanguageUtils.i18nEnum(operator, locale),
            locale.i18n("commands.filters.op_${operator.name.toLowerCase()}_description"),
            commandText,
            LanguageUtils.i18nEnum(type, locale),
            locale.i18n("commands.filters.type_${type.name.toLowerCase()}_description")
        )

        val conditions: String = if (channels.isEmpty() && roles.isEmpty() && users.isEmpty()) {
            locale.i18n("commands.filters.no_conditions")
        } else {
            val conditionsBuilder = StringBuilder()
            if (channels.isNotEmpty()) {
                conditionsBuilder
                    .append(locale.i18n("words.channels").toCapitalized())
                    .append(": ")
                    .append(channels.joinToString(", ") { "<#${it.channelId}>" })
                    .append("\n")
            }
            if (roles.isNotEmpty()) {
                conditionsBuilder
                    .append("*${LanguageUtils.i18nEnum(operator, locale)}*")
                    .append("\n")
                    .append(locale.i18n("words.roles").toCapitalized())
                    .append(": ")
                    .append(roles.joinToString(", ") { "<@&${it.roleId}>" })
                    .append("\n")
            }
            if (users.isNotEmpty()) {
                conditionsBuilder
                    .append("*${LanguageUtils.i18nEnum(operator, locale)}*")
                    .append("\n")
                    .append(locale.i18n("words.users").toCapitalized())
                    .append(": ")
                    .append(users.joinToString(", ") { "<@${it.userId}>" })
                    .append("\n")
            }
            conditionsBuilder.toString()
        }

        field {
            name = locale.i18n("words.conditions").toCapitalized()
            value = conditions
        }

        field {
            name = locale.i18n("words.status").toCapitalized()
            value = "$statusEmote " + if (!configured) {
                if (commands.isEmpty()) {
                    locale.i18n("words.not_configured").toCapitalized() + " - " + locale.i18n("commands.filters.not_configured_commands")
                } else {
                    locale.i18n("words.not_configured").toCapitalized() + " - " + locale.i18n("commands.filters.not_configured_conditions")
                }
            } else {
                locale.i18n("words.${if (enabled) "enabled" else "disabled"}").toCapitalized()
            }
        }
    }

}

data class GuildFilterId(val name: String, val guildId: Long) : Serializable {
    constructor() : this("", 0)
}