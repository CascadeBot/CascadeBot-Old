/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.goodbye

import net.dv8tion.jda.api.entities.Member
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.DeprecatedSubCommand
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.messaging.embed
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.utils.FormatUtils

class GoodbyeAddSubCommand : DeprecatedSubCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.isEmpty()) {
            context.uiMessaging.replyUsage()
            return
        }

        val message = context.getMessage(0)

        val goodbyeMessages = context.data.management.greetings.goodbyeMessages
        goodbyeMessages.add(message)

        val index = goodbyeMessages.indexOf(message)
        val proportion = goodbyeMessages.getItemProportion(index)

        context.typedMessaging.replySuccess(embed(MessageType.SUCCESS) {
            title {
                name = context.i18n("commands.goodbye.add.success_title")
            }
            description = "${context.i18n("commands.goodbye.add.success_text_1")}\n" +
                    "```\n$message\n```\n" +
                    context.i18n("commands.goodbye.add.success_text_2", goodbyeMessages.size, FormatUtils.round(proportion * 100, 0).toInt())
        })

    }

    override fun command(): String = "add"

    override fun parent(): String = "goodbye"

    override fun permission(): CascadePermission? = CascadePermission.of("goodbye.add", false)

}