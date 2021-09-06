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

class GoodbyeRemoveSubCommand : DeprecatedSubCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.size != 1) {
            context.uiMessaging.replyUsage()
            return
        }

        if (!context.isArgInteger(0)) {
            context.typedMessaging.replyDanger(context.i18n("commands.goodbye.remove.message_index_number"))
            return
        }

        val index = context.getArgAsInteger(0)!! - 1
        val goodbyeMessages = context.data.management.greetings.goodbyeMessages
        if (index < 0 || index >= goodbyeMessages.size) {
            context.typedMessaging.replyDanger(context.i18n("commands.goodbye.invalid_message_index", goodbyeMessages.size))
            return
        }

        val message = goodbyeMessages.remove(index)

        context.typedMessaging.replySuccess(embed(MessageType.INFO, context.user) {
            title {
                name = context.i18n("commands.goodbye.remove.remove_success_title")
            }
            description = context.i18n("commands.goodbye.remove.remove_success_text", message ?: "Message unavailable!")
        })
    }

    override fun command(): String = "remove"

    override fun parent(): String = "goodbye"

    override fun permission(): CascadePermission? = CascadePermission.of("goodbye.remove", false)

}