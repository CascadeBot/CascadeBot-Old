package org.cascadebot.cascadebot.utils.interactions

import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.interactions.components.Button
import net.dv8tion.jda.api.interactions.components.ButtonStyle
import net.dv8tion.jda.api.interactions.components.Component

class CascadeLinkButton private constructor (val link: String, val label: String?, val emoji: Emoji?, persistent: Boolean) : CascadeComponent(link, persistent) {
    override val discordComponent: Component = Button.of(ButtonStyle.LINK, link, label, emoji)
    override val componentType: Component.Type = Component.Type.BUTTON

    init {
        require(label != null && emoji != null) { "Label and emoji cannot both be null" }
    }

    companion object {
        @JvmStatic
        @JvmOverloads
        fun of(link: String, label: String? = null, emoji: Emoji? = null) = CascadeLinkButton(link, label, emoji, false)
    }

}