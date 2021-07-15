package org.cascadebot.cascadebot.utils.interactions

import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.interactions.components.Button
import net.dv8tion.jda.api.interactions.components.ButtonStyle
import net.dv8tion.jda.api.interactions.components.Component

class CascadeLinkButton private constructor (val link: String, val emoji: Emoji?, val label: String?) : CascadeComponent(link) {
    override val discordComponent: Component
        get() {
            return Button.of(ButtonStyle.LINK, link, label, emoji)
        }

    init {
        require(label != null || emoji != null) { "Label and emoji cannot both be null" }
    }

    constructor(link: String, label: String): this(link, null, label)
    constructor(link: String, emoji: Emoji): this(link, emoji, null)
    constructor(link: String, label: String, emoji: Emoji): this(link, emoji, label)

}