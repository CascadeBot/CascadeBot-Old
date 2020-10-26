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

class ModlogEventStore(val trigger: ModlogEvent, val responsible: User?, affected: Any, extraInfo: MutableList<ModlogEmbedPart>) {
    
    val affected: ModlogAffected = when (affected) {
        is User -> {
            ModlogAffected(AffectedType.USER, affected.asTag, affected.id)
        }
        is Role -> {
            ModlogAffected(AffectedType.ROLE, "${affected.name} (${affected.id})", affected.id)
        }
        is Emote -> {
            ModlogAffected(AffectedType.EMOTE, affected.name, affected.id)
        }
        is Guild -> {
            ModlogAffected(AffectedType.GUILD, affected.name)
        }
        is GuildChannel -> {
            ModlogAffected(AffectedType.CHANNEL, affected.name, affected.id)
        }
        is Group -> {
            ModlogAffected(AffectedType.GROUP, affected.name, affected.id)
        }
        is Field -> {
            ModlogAffected(AffectedType.SETTING, affected.name)
        }
        is Module -> {
            ModlogAffected(AffectedType.MODULE, affected.name)
        }
        is MainCommand -> {
            ModlogAffected(AffectedType.COMMAND, affected.command())
        }
        is Playlist -> {
            ModlogAffected(AffectedType.PLAYLIST, affected.name, affected.playlistId.toHexString())
        }
        is Tag -> {
            ModlogAffected(AffectedType.TAG, affected.name)
        }
        else -> {
            ModlogAffected()
        }
    }

    var extraInfo: MutableList<ModlogEmbedPart> = mutableListOf()
    var extraDescriptionInfo: MutableList<String> = mutableListOf()

    val responsibleId: Long = responsible?.idLong ?: 0

    constructor() : this(ModlogEvent.CASCADE_BLACKLIST, null, "", mutableListOf())

    init {
        if (!this.affected.affectedType.allowedDisplayTypes.contains(trigger.displayType)) {
            throw UnsupportedOperationException("This events display type does not support this affected")
        }
    }

}