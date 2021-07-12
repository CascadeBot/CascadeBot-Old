package org.cascadebot.cascadebot.utils.interactions

import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.interactions.components.Button
import net.dv8tion.jda.api.interactions.components.ButtonStyle
import net.dv8tion.jda.api.interactions.components.Component
import org.cascadebot.cascadebot.utils.buttons.IButtonRunnable

class CascadeButton : CascadeComponent {
    private val type: ButtonStyle;
    private val consumer: IButtonRunnable;
    private var label: String? = null
    private var emoji: Emoji? = null
    private lateinit var id: String;

    private constructor(type: ButtonStyle, consumer: IButtonRunnable) {
        if (type == ButtonStyle.LINK) {
            throw UnsupportedOperationException("Please use CascadeLinkButton if trying to use a link button")
        }
        this.type = type
        this.consumer = consumer
    }

    constructor(type: ButtonStyle, label: String, consumer: IButtonRunnable) : this(type, consumer) {
        this.label = label
        id = label;
    }

    constructor(type: ButtonStyle, emoji: Emoji, consumer: IButtonRunnable) : this(type, consumer) {
        this.emoji = emoji
        id = if (emoji.isUnicode) {
            emoji.name
        } else {
            emoji.id
        }
    }

    constructor(type: ButtonStyle, label: String, emoji: Emoji, consumer: IButtonRunnable) : this(type, consumer) {
        this.label = label
        this.emoji = emoji
        id = "$label-" + if (emoji.isUnicode) {
            emoji.name
        } else {
            emoji.id
        }
    }

    override fun getDiscordComponent(): Component {
        return Button.of(type, id, label, emoji)
    }

    override fun getId(): String {
        return id
    }

}