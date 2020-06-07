package org.cascadebot.cascadebot.data.objects

import net.dv8tion.jda.api.entities.Emote
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.GuildChannel
import net.dv8tion.jda.api.entities.ISnowflake
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.commandmeta.MainCommand
import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.moderation.ModlogEvent
import org.cascadebot.cascadebot.permissions.objects.Group
import org.cascadebot.cascadebot.utils.LanguageEmbedField
import java.lang.reflect.Field

class ModlogEventStore {
    
    var trigger: ModlogEvent = ModlogEvent.CASCADE_BLACKLIST

    @Transient
    var responsible: User? = null

    @Transient
    var affected: Any = CascadeBot.INS.selfUser

    var extraInfo: List<LanguageEmbedField> = ArrayList()

    var affectedId: String = ""
    var affectedType: String = ""

    var responsibleId: Long = 0;

    constructor(trigger: ModlogEvent, responsible: User?, affected: Any, extraInfo: List<LanguageEmbedField>) {
        this.trigger = trigger
        this.responsible = responsible
        this.affected = affected
        this.extraInfo = extraInfo

        affectedType = when (affected) {
            is User -> {
                affectedId = affected.id
                "User"
            }
            is Role -> {
                affectedId = affected.id
                "Role"
            }
            is Emote -> {
                affectedId = affected.id
                "Emote"
            }
            is Guild -> {
                affectedId = affected.id
                "Guild"
            }
            is GuildChannel -> {
                affectedId = affected.id
                "Channel"
            }
            is Group -> {
                affectedId = affected.id
                "Group"
            }
            is Field -> {
                affectedId = affected.name
                "Setting"
            }
            is Module -> {
                affectedId = affected.name.toLowerCase()
                "Module"
            }
            is MainCommand -> {
                affectedId = affected.command()
                "Command"
            }
            is Playlist -> {
                affectedId = affected.name
                "Playlist"
            }
            is Tag -> {
                affectedId = affected.name
                "Tag"
            }
            else -> {
                affectedId = "unknown"
                "unknown"
            }
        }

        if (responsible != null) {
            responsibleId = responsible.idLong
        }
    }

    constructor()

}