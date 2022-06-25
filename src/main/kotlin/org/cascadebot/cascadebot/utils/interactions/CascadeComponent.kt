package org.cascadebot.cascadebot.utils.interactions

import net.dv8tion.jda.api.interactions.components.Button
import net.dv8tion.jda.api.interactions.components.ButtonStyle
import net.dv8tion.jda.api.interactions.components.Component
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.utils.ChannelId
import java.util.UUID

abstract class CascadeComponent(val uniqueId: String, persistent: Boolean) {

    val id = "${if (persistent) "persistent" else "cached"}-${UUID.randomUUID()}"
    var persistent: Boolean = persistent
        protected set

    abstract val discordComponent : Component
    abstract val componentType: Component.Type

    companion object {
        fun fromDiscordComponent(channelId: ChannelId, component: Component) : CascadeComponent? {
            if (component.type == Component.Type.BUTTON && (component as Button).style == ButtonStyle.LINK) {
                return CascadeLinkButton.of(component.url!!, component.url, component.emoji)
            }

            val id = component.id ?: return null

            val prefix = id.substringBefore("-")
            val uuid = UUID.fromString(id.substringAfter("-"))

            if (prefix == "persistent") {
                return PersistentComponent.values().find { it.component.discordComponent == component }?.component
            } else if (prefix == "cached") {
                return CascadeBot.INS.componentCache.cache[channelId]?.get(component.id)
            } else {
                error("Invalid prefix ($prefix) on custom ID detected")
            }
        }
    }

}