/*
 * Copyright (c) 2022 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.utils.interactions

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.data.entities.GuildTodolistEntity

fun todoButtonCheck(runner: Member, owner: Member, channel: TextChannel, message: InteractionMessage) {
    val todoListOption = CascadeBot.INS.postgresManager.transaction {
        val query = this.createQuery("FROM GuildTodolistEntity T where T.message_id = :message_id", GuildTodolistEntity::class.java)
        query.setParameter("message_id", message.idLong)
        query.uniqueResultOptional()
    }!!
    if (todoListOption.isEmpty) {
        // The list doesn't exist, so no action to do
        return
    }

    val todoList = todoListOption.get()

    if (!todoList.canUserEdit(runner.idLong)) {
        return
    }

    val item = todoList.items[todoList.currentItem]
    item.done = true
    todoList.addUncheckButton(message.message)
    message.editMessage(todoList.todoListMessage).queue()

    message.editMessage(todoList.todoListMessage)
}