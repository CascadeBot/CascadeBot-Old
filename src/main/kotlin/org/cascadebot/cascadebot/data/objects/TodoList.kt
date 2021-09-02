package org.cascadebot.cascadebot.data.objects

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.TextChannel
import org.bson.BsonDocument
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.UnicodeConstants
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.data.database.BsonObject
import org.cascadebot.cascadebot.data.managers.GuildDataManager
import org.cascadebot.cascadebot.messaging.Messaging.sendButtonedMessage
import org.cascadebot.cascadebot.messaging.MessagingObjects
import org.cascadebot.cascadebot.utils.GuildDataUtils.assertWriteMode
import org.cascadebot.cascadebot.utils.buttons.PersistentButton
import org.cascadebot.cascadebot.utils.buttons.PersistentButtonGroup

class TodoList(val ownerId: Long) : BsonObject {

    val items: MutableList<TodoListItem> = ArrayList()
    var messageId: Long = -1
    var channelId: Long = -1
    var currentItem = 0

    //List of users id who are able to access this list
    private val users: MutableList<Long> = ArrayList()

    private constructor() : this(0) {
        //Constructor for mongodb
    }

    fun addTodoItem(text: String?): Int {
        assertWriteMode()
        val item = TodoListItem(text)
        items.add(item)
        return items.indexOf(item)
    }

    fun removeTodoItem(id: Int): TodoListItem {
        assertWriteMode()
        return items.removeAt(id)
    }

    fun addEditUser(member: Member) {
        assertWriteMode()
        users.add(member.idLong)
    }

    fun removeEditUser(member: Member) {
        assertWriteMode()
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
        assertWriteMode()
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
        assertWriteMode()
        val channel = CascadeBot.INS.client.getTextChannelById(channelId)
        if (channel != null) {
            val data = GuildDataManager.getGuildData(channel.guild.idLong)
            val buttonGroup = data.persistentButtons[channelId]!![messageId]
            buttonGroup!!.addPersistentButton(PersistentButton.TODO_BUTTON_UNCHECK)
            buttonGroup.removePersistentButton(PersistentButton.TODO_BUTTON_CHECK)
        }
    }

    fun addCheckButton(message: Message?) {
        assertWriteMode()
        val channel = CascadeBot.INS.client.getTextChannelById(channelId)
        if (channel != null) {
            val data = GuildDataManager.getGuildData(channel.guild.idLong)
            val buttonGroup = data.persistentButtons[channelId]!![messageId]
            buttonGroup!!.addPersistentButton(PersistentButton.TODO_BUTTON_CHECK)
            buttonGroup.removePersistentButton(PersistentButton.TODO_BUTTON_UNCHECK)
        }
    }

    fun doCheckToggle(message: Message?) {
        assertWriteMode()
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

    override fun fromBson(bsonDocument: BsonDocument) {
        if (bsonDocument.contains("channelId")) {
            channelId = bsonDocument["channelId"]!!.asNumber().longValue()
        }
        if (bsonDocument.contains("messageId")) {
            messageId = bsonDocument["messageId"]!!.asNumber().longValue()
        }
        if (bsonDocument.contains("currentItem")) {
            currentItem = bsonDocument["currentItem"]!!.asNumber().intValue()
        }
        if (bsonDocument.contains("items")) {
            items.clear();
            for (item in bsonDocument["items"]!!.asArray()) {
                val todoItem = TodoListItem(item.asDocument()["text"]!!.asString().value)
                todoItem.done = item.asDocument()["done"]!!.asBoolean().value
                items.add(todoItem)
            }
        }
        CascadeBot.INS.client.getTextChannelById(channelId)!!.editMessageById(messageId, todoListMessage).queue()
    }
}
