package org.cascadebot.cascadebot.utils.interactions

import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.interactions.components.Button
import net.dv8tion.jda.api.interactions.components.ButtonStyle
import net.dv8tion.jda.api.interactions.components.Component
import org.cascadebot.cascadebot.utils.asString
import java.lang.IllegalStateException

class CascadeButton private constructor (val type: ButtonStyle, val label: String?, val emoji: Emoji?, val consumer: IButtonRunnable) : CascadeComponent(generateId(label, emoji)) {

    var disabled: Boolean = false

    override val discordComponent: Component
        get() = Button.of(type, id, label, emoji).withDisabled(disabled)

    init {
        require(label != null || emoji != null) { "Label and emoji cannot both be null" }
        if (type == ButtonStyle.LINK) {
            throw UnsupportedOperationException("Please use CascadeLinkButton if trying to use a link button") // TODO implement link buttons
        }
    }

    companion object {
        @JvmStatic
        fun primary(label: String, consumer: IButtonRunnable) = CascadeButton(ButtonStyle.PRIMARY, label, null, consumer)
        @JvmStatic
        fun primary(emoji: Emoji, consumer: IButtonRunnable) = CascadeButton(ButtonStyle.PRIMARY, null, emoji, consumer)
        @JvmStatic
        fun primary(label: String, emoji: Emoji, consumer: IButtonRunnable) = CascadeButton(ButtonStyle.PRIMARY, label, emoji, consumer)

        @JvmStatic
        fun secondary(label: String, consumer: IButtonRunnable) = CascadeButton(ButtonStyle.SECONDARY, label, null, consumer)
        @JvmStatic
        fun secondary(emoji: Emoji, consumer: IButtonRunnable) = CascadeButton(ButtonStyle.SECONDARY, null, emoji, consumer)
        @JvmStatic
        fun secondary(label: String, emoji: Emoji, consumer: IButtonRunnable) = CascadeButton(ButtonStyle.SECONDARY, label, emoji, consumer)

        @JvmStatic
        fun success(label: String, consumer: IButtonRunnable) = CascadeButton(ButtonStyle.SUCCESS, label, null, consumer)
        @JvmStatic
        fun success(emoji: Emoji, consumer: IButtonRunnable) = CascadeButton(ButtonStyle.SUCCESS, null, emoji, consumer)
        @JvmStatic
        fun success(label: String, emoji: Emoji, consumer: IButtonRunnable) = CascadeButton(ButtonStyle.SUCCESS, label, emoji, consumer)

        @JvmStatic
        fun danger(label: String, consumer: IButtonRunnable) = CascadeButton(ButtonStyle.DANGER, label, null, consumer)
        @JvmStatic
        fun danger(emoji: Emoji, consumer: IButtonRunnable) = CascadeButton(ButtonStyle.DANGER, null, emoji, consumer)
        @JvmStatic
        fun danger(label: String, emoji: Emoji, consumer: IButtonRunnable) = CascadeButton(ButtonStyle.DANGER, label, emoji, consumer)
    }

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