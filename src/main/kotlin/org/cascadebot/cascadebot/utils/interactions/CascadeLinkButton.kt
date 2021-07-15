package org.cascadebot.cascadebot.utils.interactions

import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.interactions.components.Button
import net.dv8tion.jda.api.interactions.components.ButtonStyle
import net.dv8tion.jda.api.interactions.components.Component

class CascadeLinkButton private constructor (val link: String, val label: String?, val emoji: Emoji?) : CascadeComponent(link) {
    override val discordComponent: Component = Button.of(ButtonStyle.LINK, link, label, emoji)

    init {
        require(label != null || emoji != null) { "Label and emoji cannot both be null" }
    }

    companion object {
        @JvmStatic
        fun of(link: String, label: String) = CascadeLinkButton(link, label, null)
        @JvmStatic
        fun of(link: String, emoji: Emoji) = CascadeLinkButton(link, null, emoji)
        @JvmStatic
        fun of(link: String, label: String, emoji: Emoji) = CascadeLinkButton(link, label, emoji)
    }

}