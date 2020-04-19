package org.cascadebot.cascadebot.utils.buttons

import de.bild.codec.annotations.Transient
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote
import net.dv8tion.jda.api.entities.TextChannel
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.utils.buttons.Button.EmoteButton
import org.cascadebot.cascadebot.utils.buttons.Button.UnicodeButton
import java.util.*

open class ButtonGroup(
        val ownerId: Long,
        val channelId: Long,
        val guildId: Long
) {

    // Default constructor for MongoDB
    private constructor() : this(-1, -1, -1)

    @Transient
    private val buttons: MutableList<Button> = ArrayList()

    var messageId: Long = 0

    @Transient
    private var messageSentAction: Runnable? = null
    open fun addButton(button: Button) {
        buttons.add(button)
        val guild = CascadeBot.INS.shardManager.getGuildById(guildId) ?: return
        val channel = guild.getTextChannelById(channelId)
        if (messageId != 0L && channel != null) {
            channel.retrieveMessageById(messageId).queue { message: Message? -> message?.let{ button.addReaction(message)} }
        }
    }

    open fun removeButton(button: Button) {
        buttons.remove(button)
        val guild = CascadeBot.INS.shardManager.getGuildById(guildId) ?: return
        val channel = guild.getTextChannelById(channelId)
        if (messageId != 0L && channel != null) {
            channel.retrieveMessageById(messageId).queue { message: Message ->
                for (reaction in message.reactions) {
                    val reactionEmote = reaction.reactionEmote
                    if (button is UnicodeButton && !reactionEmote.isEmote) {
                        if (reactionEmote.name == button.unicode) {
                            reaction.removeReaction(CascadeBot.INS.selfUser).queue()
                        }
                    } else if (button is EmoteButton && reactionEmote.isEmote) {
                        if (reactionEmote.emote.idLong == button.emoteId) {
                            reaction.removeReaction(CascadeBot.INS.selfUser).queue()
                        }
                    }
                }
            }
        }
    }

    val owner: Member?
        get() = CascadeBot.INS.shardManager.getGuildById(guildId)!!.getMemberById(ownerId)

    fun setMessageSentAction(messageSentAction: Runnable?) {
        this.messageSentAction = messageSentAction
        if (messageId != 0L) {
            this.messageSentAction!!.run()
        }
    }

    fun setMessage(id: Long) {
        messageId = id
        if (messageSentAction != null) {
            messageSentAction!!.run()
        }
    }

    fun handleButton(clicker: Member?, channel: TextChannel?, buttonMessage: Message?, emote: ReactionEmote) {
        for (button in buttons) {
            if (button is EmoteButton && emote.isEmote) {
                if (button.emoteId == emote.emote.idLong) {
                    button.runnable.run(clicker, channel, buttonMessage)
                    return
                }
            } else if (button is UnicodeButton && !emote.isEmote) {
                if (button.unicode == emote.name) {
                    button.runnable.run(clicker, channel, buttonMessage)
                    return
                }
            }
        }
    }

    fun addButtonsToMessage(message: Message) {
        for (button in buttons) {
            button.addReaction(message)
        }
        setMessage(message.idLong)
    }

}
