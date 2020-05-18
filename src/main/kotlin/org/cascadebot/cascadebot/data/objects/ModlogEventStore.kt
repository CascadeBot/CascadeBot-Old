package org.cascadebot.cascadebot.data.objects

import club.minnced.discord.webhook.send.WebhookEmbed
import net.dv8tion.jda.api.entities.ISnowflake
import net.dv8tion.jda.api.entities.User
import org.cascadebot.cascadebot.moderation.ModlogEvent

data class ModlogEventStore(
        val trigger: ModlogEvent,
        val responsible: User?,
        val affected: ISnowflake,

        val extraInfo: List<WebhookEmbed.EmbedField>
)