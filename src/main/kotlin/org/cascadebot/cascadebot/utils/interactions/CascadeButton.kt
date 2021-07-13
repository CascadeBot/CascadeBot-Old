package org.cascadebot.cascadebot.utils.interactions

import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.interactions.components.Button
import net.dv8tion.jda.api.interactions.components.ButtonStyle
import net.dv8tion.jda.api.interactions.components.Component
import org.cascadebot.cascadebot.utils.asString
import java.lang.IllegalStateException

open class CascadeButton private constructor (val label: String?, val emoji: Emoji?, val type: ButtonStyle, val consumer: IButtonRunnable) : CascadeComponent(generateId(label, emoji)) {

    var disabled: Boolean = false

    override val discordComponent: Component
        get() {
            var button: Button = Button.of(type, id, label, null)
            button = if (disabled) {
                button.asDisabled()
            } else {
                button.asEnabled()
            }
            return button
        }

    init {
        if (type == ButtonStyle.LINK) {
            throw UnsupportedOperationException("Please use CascadeLinkButton if trying to use a link button") // TODO implement link buttons
        }
    }

    constructor(type: ButtonStyle, label: String, consumer: IButtonRunnable) : this(label, null, type, consumer)
    constructor(type: ButtonStyle, emoji: Emoji, consumer: IButtonRunnable) : this(null, emoji, type, consumer)
    constructor(type: ButtonStyle, label: String, emoji: Emoji, consumer: IButtonRunnable) : this(label, emoji, type, consumer)

}

private fun generateId(label: String?, emoji: Emoji?) : String {
    require(!(label == null && emoji == null)) { "Both the label and emoji cannot be null!" }
    return if (label != null) {
        if (emoji != null) {
            "$label-${emoji.asString()}"
        } else {
            label
        }
    } else emoji?.toString() ?: throw IllegalStateException("Both label and emoji cannot be null!")
}