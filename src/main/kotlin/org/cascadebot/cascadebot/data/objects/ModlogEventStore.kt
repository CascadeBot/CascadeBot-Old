package org.cascadebot.cascadebot.data.objects

import net.dv8tion.jda.api.entities.Emote
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.GuildChannel
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import org.cascadebot.cascadebot.commandmeta.MainCommand
import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.moderation.ModlogEmbedPart
import org.cascadebot.cascadebot.moderation.ModlogEvent
import org.cascadebot.cascadebot.permissions.objects.Group
import java.lang.UnsupportedOperationException
import java.lang.reflect.Field

class ModlogEventStore {
    
    var trigger: ModlogEvent = ModlogEvent.CASCADE_BLACKLIST

    @Transient
    var responsible: User? = null

    var affected: ModlogAffected = ModlogAffected()

    var extraInfo: MutableList<ModlogEmbedPart> = mutableListOf()
    var extraDescriptionInfo: MutableList<String> = mutableListOf()

    var responsibleId: Long = 0

    constructor(trigger: ModlogEvent, responsible: User?, affected: Any, extraInfo: MutableList<ModlogEmbedPart>) {
        this.trigger = trigger
        this.responsible = responsible
        this.extraInfo = extraInfo

        var affectedType: AffectedType = AffectedType.UNKNOWN
        this.affected = when (affected) {
            is User -> {
                affectedType = AffectedType.USER
                ModlogAffected(AffectedType.USER, affected.asTag, affected.id)
            }
            is Role -> {
                affectedType = AffectedType.ROLE
                ModlogAffected(AffectedType.ROLE, affected.name, affected.id)
            }
            is Emote -> {
                affectedType = AffectedType.EMOTE
                ModlogAffected(AffectedType.EMOTE, affected.name, affected.id)
            }
            is Guild -> {
                affectedType = AffectedType.GUILD
                ModlogAffected(AffectedType.GUILD, affected.name)
            }
            is GuildChannel -> {
                affectedType = AffectedType.CHANNEL
                ModlogAffected(AffectedType.CHANNEL, affected.name, affected.id)
            }
            is Group -> {
                affectedType = AffectedType.GROUP
                ModlogAffected(AffectedType.GROUP, affected.name, affected.id)
            }
            is Field -> {
                affectedType = AffectedType.SETTING
                ModlogAffected(AffectedType.SETTING, affected.name)
            }
            is Module -> {
                affectedType = AffectedType.MODULE
                ModlogAffected(AffectedType.MODULE, affected.name)
            }
            is MainCommand -> {
                affectedType = AffectedType.COMMAND
                ModlogAffected(AffectedType.COMMAND, affected.command())
            }
            is Playlist -> {
                affectedType = AffectedType.PLAYLIST
                ModlogAffected(AffectedType.PLAYLIST, affected.name, affected.playlistId.toHexString())
            }
            is Tag -> {
                affectedType = AffectedType.TAG
                ModlogAffected(AffectedType.TAG, affected.name)
            }
            else -> {
                ModlogAffected()
            }
        }
        if (!affectedType.allowedDisplayTypes.contains(trigger.displayType)) {
            throw UnsupportedOperationException("This events display type does not support this affected")
        }

        if (responsible != null) {
            responsibleId = responsible.idLong
        }
    }

    constructor()

}