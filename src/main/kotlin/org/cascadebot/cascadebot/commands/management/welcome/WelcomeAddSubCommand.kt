/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.welcome

import net.dv8tion.jda.api.entities.Member
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.DeprecatedSubCommand
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.messaging.embed
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.utils.FormatUtils

class WelcomeAddSubCommand : DeprecatedSubCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.isEmpty()) {
            context.uiMessaging.replyUsage()
            return
        }

        val message = context.getMessage(0)

        val welcomeMessages = context.data.management.greetings.welcomeMessages
        welcomeMessages.add(message)

        val index = welcomeMessages.indexOf(message)
        val proportion = welcomeMessages.getItemProportion(index)

        context.typedMessaging.replySuccess(embed(MessageType.SUCCESS) {
            title {
                name = context.i18n("commands.welcome.add.success_title")
            }
            description = "${context.i18n("commands.welcome.add.success_text_1")}\n" +
                    "```\n$message\n```\n" +
                    context.i18n("commands.welcome.add.success_text_2", welcomeMessages.size, FormatUtils.round(proportion * 100, 0).toInt())
        })

    }

    override fun command(): String = "add"

    override fun parent(): String = "welcome"

    override fun permission(): CascadePermission? = CascadePermission.of("welcome.add", false)

}