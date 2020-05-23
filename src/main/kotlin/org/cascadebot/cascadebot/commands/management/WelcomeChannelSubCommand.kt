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
import org.cascadebot.cascadebot.utils.DiscordUtils

class WelcomeChannelSubCommand : SubCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.size > 1) {
            context.uiMessaging.replyUsage()
            return
        }

        if (context.args.isEmpty()) {
            context.typedMessaging.replyInfo(embed(MessageType.INFO, context.user) {
                description = run {
                    val greetings = context.data.management.greetings
                    if (greetings.welcomeChannel != null) {
                        "The current welcome channel is: ${greetings.welcomeChannel?.asMention}"
                    } else {
                        "There is no welcome channel set!"
                    }
                }
            })
        } else {
            if (context.getArg(0).equals("clear", true)) {
                context.data.management.greetings.welcomeChannel = null
                context.typedMessaging.replySuccess("Successfully cleared the welcome channel!")
                return
            }

            val channel = DiscordUtils.getTextChannel(context.guild, context.getArg(0))
            if (channel == null) {
                context.typedMessaging.replyDanger("Please specify a valid channel!")
                return
            }

            context.data.management.greetings.welcomeChannel = channel

            context.typedMessaging.replySuccess("Successfully set the welcome channel to ${channel.asMention}")
        }

    }

    override fun command(): String = "channel"

    override fun parent(): String = "welcome"

    override fun permission(): CascadePermission? = CascadePermission.of("welcome.channel", false)

}