package org.cascadebot.cascadebot.utils.buttons

import net.dv8tion.jda.api.entities.Message
import org.cascadebot.cascadebot.CascadeBot
import java.util.function.Consumer

abstract class Button private constructor(var runnable: IButtonRunnable) {
    abstract fun addReaction(message: Message)

    @Deprecated("Use CascadeButton instead", ReplaceWith("CascadeButton", "org.cascadebot.cascadebot.utils.interactions.CascadeButton"))
    class EmoteButton(val emoteId: Long, runnable: IButtonRunnable) : Button(runnable) {
        override fun addReaction(message: Message) {
            if (CascadeBot.INS.shardManager.getEmoteById(emoteId) != null) {
                message.addReaction(CascadeBot.INS.shardManager.getEmoteById(emoteId)!!).queue()
            } else {
                CascadeBot.LOGGER.warn("An emote button has an invalid emote ID, please update it! ID: {}", emoteId)
            }
        }

        init {
            if (emoteId <= 0) CascadeBot.LOGGER.warn("An emote button has been registered with an invalid ID!")
        }
    }

    @Deprecated("Use CascadeButton instead", ReplaceWith("CascadeButton", "org.cascadebot.cascadebot.utils.interactions.CascadeButton"))
    class UnicodeButton(val unicode: String, runnable: IButtonRunnable) : Button(runnable) {
        override fun addReaction(message: Message) {
            message.addReaction(unicode).queue(null) { error -> CascadeBot.LOGGER.debug("Failed to add reaction!", error) }
        }

    }

}
