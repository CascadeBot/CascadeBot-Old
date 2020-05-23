/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management

import net.dv8tion.jda.api.entities.Member
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.messaging.embed
import org.cascadebot.cascadebot.permissions.CascadePermission

class WelcomeRemoveSubCommand : SubCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.size != 1) {
            context.uiMessaging.replyUsage()
            return
        }

        if (!context.isArgInteger(0)) {
            context.typedMessaging.replyDanger("The index needs to be a number!")
            return
        }

        val index = context.getArgAsInteger(0)!! - 1
        val welcomeMessages = context.data.management.greetings.welcomeMessages
        if (index < 0 || index >= welcomeMessages.size) {
            context.typedMessaging.replyDanger("The message index must be between `1` and `${welcomeMessages.size}`")
            return
        }

        val message = welcomeMessages.remove(index)

        context.typedMessaging.replySuccess(embed(MessageType.INFO, context.user) {
            title {
                name = "Deleted welcome message!"
            }
            description = "This message has been deleted!\n```\n$message\n```"
        })
    }

    override fun command(): String = "remove"

    override fun parent(): String = "welcome"

    override fun permission(): CascadePermission? = CascadePermission.of("welcome.remove", false)

}