package org.cascadebot.cascadebot.utils.interactions

import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.interactions.components.Button
import net.dv8tion.jda.api.interactions.components.ButtonStyle
import net.dv8tion.jda.api.interactions.components.Component
import org.cascadebot.cascadebot.utils.asString

typealias ButtonRunnable = (runner: Member, owner: Member, channel: TextChannel, message: InteractionMessage) -> Unit

class CascadeButton private constructor (val type: ButtonStyle, val label: String?, val emoji: Emoji?, persistent: Boolean = false, val consumer: ButtonRunnable) : CascadeComponent(generateId(label, emoji), persistent) {

    var disabled: Boolean = false

    override val discordComponent: Component = Button.of(type, id, label, emoji).withDisabled(disabled)
    override val componentType: Component.Type = Component.Type.BUTTON

    init {
        require(label != null && emoji != null) { "Label and emoji cannot both be null" }
        if (type == ButtonStyle.LINK) {
            throw UnsupportedOperationException("Please use CascadeLinkButton if trying to use a link button") // TODO implement link buttons
        }
    }

    companion object {
        @JvmStatic
        fun primary(label: String, consumer: ButtonRunnable) = CascadeButton(ButtonStyle.PRIMARY, label, null, false, consumer)

        @JvmStatic
        fun primary(emoji: Emoji, consumer: ButtonRunnable) = CascadeButton(ButtonStyle.PRIMARY, null, emoji, false, consumer)

        @JvmStatic
        fun primary(label: String, emoji: Emoji, consumer: ButtonRunnable) = CascadeButton(ButtonStyle.PRIMARY, label, emoji, false, consumer)


        @JvmStatic
        fun secondary(label: String, consumer: ButtonRunnable) = CascadeButton(ButtonStyle.SECONDARY, label, null, false, consumer)

        @JvmStatic
        fun secondary(emoji: Emoji, consumer: ButtonRunnable) = CascadeButton(ButtonStyle.SECONDARY, null, emoji, false, consumer)

        @JvmStatic
        fun secondary(label: String, emoji: Emoji, consumer: ButtonRunnable) = CascadeButton(ButtonStyle.SECONDARY, label, emoji, false, consumer)


        @JvmStatic
        fun success(label: String, consumer: ButtonRunnable) = CascadeButton(ButtonStyle.SUCCESS, label, null, false, consumer)

        @JvmStatic
        fun success(emoji: Emoji, consumer: ButtonRunnable) = CascadeButton(ButtonStyle.SUCCESS, null, emoji, false, consumer)

        @JvmStatic
        fun success(label: String, emoji: Emoji, consumer: ButtonRunnable) = CascadeButton(ButtonStyle.SUCCESS, label, emoji, false, consumer)


        @JvmStatic
        fun danger(label: String, consumer: ButtonRunnable) = CascadeButton(ButtonStyle.DANGER, label, null, false, consumer)

        @JvmStatic
        fun danger(emoji: Emoji, consumer: ButtonRunnable) = CascadeButton(ButtonStyle.DANGER, null, emoji, false, consumer)

        @JvmStatic
        fun danger(label: String, emoji: Emoji, consumer: ButtonRunnable) = CascadeButton(ButtonStyle.DANGER, label, emoji, false, consumer)


        @JvmStatic
        fun persistent(type: ButtonStyle, label: String, consumer: ButtonRunnable) = CascadeButton(type, label, null, true, consumer)

        @JvmStatic
        fun persistent(type: ButtonStyle, emoji: Emoji, consumer: ButtonRunnable) = CascadeButton(type, null, emoji, true, consumer)

        @JvmStatic
        fun persistent(type: ButtonStyle, label: String, emoji: Emoji, consumer: ButtonRunnable) = CascadeButton(type, label, emoji, true, consumer)

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