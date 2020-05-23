package org.cascadebot.cascadebot.data.objects

import net.dv8tion.jda.api.entities.ISnowflake
import net.dv8tion.jda.api.entities.User
import org.cascadebot.cascadebot.moderation.ModlogEvent
import org.cascadebot.cascadebot.utils.LanguageEmbedField

data class ModlogEventStore(
        val trigger: ModlogEvent,
        val responsible: User?,
        val affected: ISnowflake,

        val extraInfo: List<LanguageEmbedField>
)