package org.cascadebot.cascadebot.data.objects

import net.dv8tion.jda.api.entities.Emote
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.GuildChannel
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import org.cascadebot.cascadebot.commandmeta.MainCommand
import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.moderation.ModlogEmbedPart
import org.cascadebot.cascadebot.moderation.ModlogEvent
import org.cascadebot.cascadebot.permissions.objects.Group
import java.lang.UnsupportedOperationException
import java.lang.reflect.Field

class ModlogEventStore(val trigger: ModlogEvent, @Transient val responsible: User?, affected: Any, var extraInfo: MutableList<ModlogEmbedPart>) {
    
    val affected: ModlogAffected = when (affected) {
        is User -> {
            ModlogAffected(AffectedType.USER, affected.asTag, affected.asMention, affected.id, affected.effectiveAvatarUrl)
        }
        is Role -> {
            ModlogAffected(AffectedType.ROLE, "${affected.name} (${affected.id})", affected.asMention, affected.id)
        }
        is Emote -> {
            ModlogAffected(AffectedType.EMOTE, affected.name, affected.asMention, affected.id, affected.imageUrl)
        }
        is Guild -> {
            ModlogAffected(AffectedType.GUILD, affected.name, null, affected.id, imageUrl = affected.iconUrl)
        }
        is TextChannel -> {
            ModlogAffected(AffectedType.GUILD, affected.name, affected.asMention, affected.id)
        }
        is GuildChannel -> {
            ModlogAffected(AffectedType.CHANNEL, affected.name, null, affected.id)
        }
        is Group -> {
            ModlogAffected(AffectedType.GROUP, affected.name, null, affected.id)
        }
        is Field -> {
            ModlogAffected(AffectedType.SETTING, affected.name, null, null)
        }
        is Module -> {
            ModlogAffected(AffectedType.MODULE, affected.name, null, null)
        }
        is MainCommand -> {
            ModlogAffected(AffectedType.COMMAND, affected.command(), null, null)
        }
        is Playlist -> {
            ModlogAffected(AffectedType.PLAYLIST, affected.name, affected.playlistId.toHexString(), null, null)
        }
        is Tag -> {
            ModlogAffected(AffectedType.TAG, affected.name, null, null)
        }
        else -> {
            ModlogAffected()
        }
    }

    var extraDescriptionInfo: MutableList<String> = mutableListOf()

    val responsibleId: Long = responsible?.idLong ?: 0

    constructor() : this(ModlogEvent.CASCADE_BLACKLIST, null, "", mutableListOf())

    init {
        if (!this.affected.affectedType.allowedDisplayTypes.contains(trigger.displayType)) {
            throw UnsupportedOperationException("This events display type does not support this affected")
        }
    }

}