/*
 * Copyright (c) 2021 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.entities

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.TextChannel
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.UnicodeConstants
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.messaging.Messaging
import org.cascadebot.cascadebot.messaging.MessagingObjects
import org.cascadebot.cascadebot.utils.interactions.CascadeActionRow
import org.cascadebot.cascadebot.utils.interactions.ComponentContainer
import org.cascadebot.cascadebot.utils.interactions.PersistentComponent
import org.hibernate.annotations.Cascade
import org.hibernate.annotations.CascadeType
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass
import javax.persistence.JoinColumn
import javax.persistence.JoinColumns
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "guild_todolist")
@IdClass(GuildTodolistId::class)
class GuildTodolistEntity(name: String, guildId: Long) {

    @Id
    @Column(name = "name")
    val name: String = name

    @Id
    @Column(name = "guild_id")
    val guildId: Long = guildId

    @Column(name = "message_id", nullable = true)
    var messageId: Long? = null

    @Column(name = "channel_id", nullable = true)
    var channelId: Long? = null

    @Column(name = "owner_id", nullable = true)
    var ownerId: Long? = null

    @Column(name = "current_item", nullable = false)
    var currentItem: Int = 0

    @OneToMany
    @Cascade(CascadeType.ALL)
    @JoinColumns(
        JoinColumn(name = "todolist_name", referencedColumnName = "name"),
        JoinColumn(name = "guild_id", referencedColumnName = "guild_id"),
    )
    val items: MutableList<GuildTodolistItemEntity> = mutableListOf()

    @OneToMany
    @Cascade(CascadeType.ALL)
    @JoinColumns(
        JoinColumn(name = "todolist_name", referencedColumnName = "name"),
        JoinColumn(name = "guild_id", referencedColumnName = "guild_id"),
    )
    val members: MutableList<GuildTodolistMemberEntity> = mutableListOf();

    fun addTodoItem(text: String): Int {
        val item = GuildTodolistItemEntity(name, guildId, text)
        items.add(item)
        return items.indexOf(item)
    }

    fun removeTodoItem(id: Int): GuildTodolistItemEntity {
        return items.removeAt(id)
    }

    fun addEditUser(member: Member) {
        members.add(GuildTodolistMemberEntity(name, guildId, member.idLong))
    }

    fun removeEditUser(member: Member) {
        members.removeIf { it.memberId == member.idLong }
    }

    fun canUserEdit(id: Long): Boolean {
        return ownerId == id || members.any { it.memberId == id }
    }
    fun edit(context: CommandContext) {
        val messageId = this.messageId
        val channelId = this.channelId

        if (messageId == null || channelId == null) return
        val originalChannel = context.guild.getTextChannelById(channelId)
        if (originalChannel != null && originalChannel.idLong == channelId) {
            val message = originalChannel.retrieveMessageById(messageId).complete()
            if (message != null) {
                message.editMessageEmbeds(todoListMessage).queue()
                doCheckToggle(message)
            }
        }
    }

    fun send(channel: TextChannel) {
        val buttonGroup = generateButtons(true)
        currentItem = 0
        Messaging.sendComponentMessage(channel, todoListMessage, buttonGroup).thenAccept {
            messageId = it.idLong
            channelId = it.channel.idLong
        }
    }

    private fun generateButtons(check: Boolean): ComponentContainer {
        val container = ComponentContainer()
        val row = CascadeActionRow()
        if (check) {
            row.addComponent(PersistentComponent.TODO_BUTTON_CHECK.component)
        } else {
            row.addComponent(PersistentComponent.TODO_BUTTON_UNCHECK.component)
        }
        row.addComponent(PersistentComponent.TODO_BUTTON_NAVIGATE_LEFT.component)
        row.addComponent(PersistentComponent.TODO_BUTTON_NAVIGATE_UP.component)
        row.addComponent(PersistentComponent.TODO_BUTTON_NAVIGATE_DOWN.component)
        row.addComponent(PersistentComponent.TODO_BUTTON_NAVIGATE_RIGHT.component)
        container.addRow(row)
        return container
    }

    fun setChecked(message: Message?, check: Boolean) {
        val channelId = this.channelId ?: error("Channel ID must not be null")
        val channel = CascadeBot.INS.client.getTextChannelById(channelId)
        if (channel != null) {
            val container = GuildDataManager.getGuildData(channel.guild.idLong).componentCache[channelId]!![messageId]
            container!!.getRow(0).setComponent(0, if (check) {
                PersistentComponent.TODO_BUTTON_CHECK.component
            } else {
                PersistentComponent.TODO_BUTTON_UNCHECK.component
            })
            val data = GuildDataManager.getGuildData(message?.guild!!.idLong)
            data.addComponents(channel, message, container)
        }
    }

    fun addUncheckButton(message: Message) {
        setChecked(message, true)
    }

    fun addCheckButton(message: Message) {
        setChecked(message, false)
    }

    fun doCheckToggle(message: Message) {
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
                val item: GuildTodolistItemEntity = items[i]
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

data class GuildTodolistId(val name: String, val guildId: Long) : Serializable {
    constructor() : this("", 0)
}