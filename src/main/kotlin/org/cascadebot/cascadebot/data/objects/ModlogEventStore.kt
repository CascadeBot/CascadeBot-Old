package org.cascadebot.cascadebot.data.objects

import net.dv8tion.jda.api.entities.Emote
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.GuildChannel
import net.dv8tion.jda.api.entities.ISnowflake
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.moderation.ModlogEvent
import org.cascadebot.cascadebot.utils.LanguageEmbedField

class ModlogEventStore {
    var trigger: ModlogEvent = ModlogEvent.CASCADE_BLACKLIST

    @Transient
    var responsible: User? = null

    @Transient
    var affected: ISnowflake = CascadeBot.INS.selfUser

    var extraInfo: List<LanguageEmbedField> = ArrayList()

    var affectedId: Long = 0
    var affectedType: String = ""

    var responsibleId: Long = 0;

    constructor(trigger: ModlogEvent, responsible: User?, affected: ISnowflake, extraInfo: List<LanguageEmbedField>) {
        this.trigger = trigger
        this.responsible = responsible
        this.affected = affected
        this.extraInfo = extraInfo

        affectedId = affected.idLong
        affectedType = when (affected) {
            is User -> "User"
            is Role -> "Role"
            is Emote -> "Emote"
            is Guild -> "Guild"
            is GuildChannel -> "Channel"
            else -> "unknown"
        }

        if (responsible != null) {
            responsibleId = responsible.idLong
        }
    }

    constructor()

}