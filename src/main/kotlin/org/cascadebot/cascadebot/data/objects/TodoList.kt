package org.cascadebot.cascadebot.data.objects

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.TextChannel
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.UnicodeConstants
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.data.managers.GuildDataManager
import org.cascadebot.cascadebot.messaging.Messaging.sendButtonedMessage
import org.cascadebot.cascadebot.messaging.MessagingObjects
import org.cascadebot.cascadebot.utils.buttons.PersistentButton
import org.cascadebot.cascadebot.utils.buttons.PersistentButtonGroup
import java.util.ArrayList

class TodoList(ownerId: Long) {

    val items: MutableList<TodoListItem> = ArrayList()
    var messageId: Long = -1
    var channelId: Long = -1
    var currentItem = 0
    var ownerId: Long = 0

    //List of users id who are able to access this list
    private val users: MutableList<Long> = ArrayList()

    private constructor() : this(0) {
        //Constructor for mongodb
    }

    fun addTodoItem(text: String?): Int {
        val item = TodoListItem(text)
        items.add(item)
        return items.indexOf(item)
    }

    fun removeTodoItem(id: Int): TodoListItem {
        return items.removeAt(id)
    }

    fun addEditUser(member: Member) {
        users.add(member.idLong)
    }

    fun removeEditUser(member: Member) {
        users.remove(member.idLong)
    }

    fun canUserEdit(id: Long): Boolean {
        return ownerId == id || users.contains(id)
    }

    class TodoListItem {

        var done = false
        var text: String? = null

        private constructor() {
            //Constructor for mongodb
        }

        internal constructor(text: String?) {
            this.text = text
            done = false
        }
    }

    fun edit(context: CommandContext) {
        if (messageId == -1L || channelId == -1L) return
        val originalChannel = context.guild.getTextChannelById(channelId)
        if (originalChannel != null && originalChannel.idLong == channelId) {
            val message = originalChannel.retrieveMessageById(messageId).complete()
            if (message != null) {
                message.editMessage(todoListMessage).queue()
                doCheckToggle(message)
            }
        }
    }

    fun send(context: CommandContext, channel: TextChannel?) {
        requireNotNull(channel) { "The channel should exist :(" }
        val buttonGroup = generateButtons(context.member.idLong, channel.idLong, context.guild.idLong)
        currentItem = 0
        sendButtonedMessage(channel, todoListMessage, buttonGroup).thenAccept {
            messageId = it.idLong
            channelId = it.channel.idLong
        }
    }

    private fun generateButtons(memberId: Long, channelId: Long, guildId: Long): PersistentButtonGroup {
        val buttonGroup = PersistentButtonGroup(memberId, channelId, guildId)
        buttonGroup.addPersistentButton(PersistentButton.TODO_BUTTON_NAVIGATE_LEFT)
        buttonGroup.addPersistentButton(PersistentButton.TODO_BUTTON_NAVIGATE_UP)
        buttonGroup.addPersistentButton(PersistentButton.TODO_BUTTON_NAVIGATE_DOWN)
        buttonGroup.addPersistentButton(PersistentButton.TODO_BUTTON_NAVIGATE_RIGHT)
        buttonGroup.addPersistentButton(PersistentButton.TODO_BUTTON_CHECK)
        return buttonGroup
    }

    fun addUncheckButton(message: Message?) {
        val channel = CascadeBot.INS.client.getTextChannelById(channelId)
        if (channel != null) {
            val data = GuildDataManager.getGuildData(channel.guild.idLong)
            val buttonGroup = data.persistentButtons[channelId]!![messageId]
            buttonGroup!!.addPersistentButton(PersistentButton.TODO_BUTTON_UNCHECK)
            buttonGroup.removePersistentButton(PersistentButton.TODO_BUTTON_CHECK)
        }
    }

    fun addCheckButton(message: Message?) {
        val channel = CascadeBot.INS.client.getTextChannelById(channelId)
        if (channel != null) {
            val data = GuildDataManager.getGuildData(channel.guild.idLong)
            val buttonGroup = data.persistentButtons[channelId]!![messageId]
            buttonGroup!!.addPersistentButton(PersistentButton.TODO_BUTTON_CHECK)
            buttonGroup.removePersistentButton(PersistentButton.TODO_BUTTON_UNCHECK)
        }
    }

    fun doCheckToggle(message: Message?) {
        val item = items[this.currentItem]
        if (item.done) {
            addUncheckButton(message)
        } else {
            addCheckButton(message)
        }
        //TODO check all items check. I'm going to wait for persistent buttons to do this
    }

    val todoListMessage: MessageEmbed
        get() {
            val pos: Int = this.currentItem
            val currentPage = pos / 10 + 1
            val start = currentPage * 10 - 10
            val end = start + 9
            val pageBuilder = StringBuilder()
            for (i in start..end) {
                if (i >= this.items.size) {
                    break
                }
                val item: TodoListItem = this.items.get(i)
                if (i == pos) {
                    pageBuilder.append(UnicodeConstants.SMALL_ORANGE_DIAMOND).append(" ")
                } else {
                    pageBuilder.append(UnicodeConstants.WHITE_SMALL_SQUARE).append(" ")
                }
                pageBuilder.append(i + 1).append(": ")
                if (item.done) {
                    pageBuilder.append("~~")
                }
                pageBuilder.append(item.text).append('\n')
                if (item.done) {
                    pageBuilder.append("~~")
                }
            }
            val embedBuilder = MessagingObjects.getClearThreadLocalEmbedBuilder()
            embedBuilder.setTitle("Todo List")
            embedBuilder.appendDescription(pageBuilder.toString())
            return embedBuilder.build()
        }
}
