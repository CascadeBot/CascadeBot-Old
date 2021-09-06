/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.welcome

import net.dv8tion.jda.api.entities.Member
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.DeprecatedSubCommand
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.utils.DiscordUtils

class WelcomeChannelSubCommand : DeprecatedSubCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.size > 1) {
            context.uiMessaging.replyUsage()
            return
        }

        if (context.args.isEmpty()) {
            context.typedMessaging.replyInfo(run {
                val greetings = context.data.management.greetings
                if (greetings.welcomeChannel != null) {
                    context.i18n("commands.welcome.current_channel", greetings.welcomeChannel?.asMention!!)
                } else {
                    context.i18n("commands.welcome.no_channel_set")
                }
            })
        } else {
            if (context.testForArg("clear")) {
                context.data.management.greetings.welcomeChannel = null
                context.typedMessaging.replySuccess(context.i18n("commands.welcome.channel.clear_success"))
                return
            }

            val channel = DiscordUtils.getTextChannel(context.guild, context.getArg(0))
            if (channel == null) {
                context.typedMessaging.replyDanger(context.i18n("commands.welcome.channel.invalid_channel"))
                return
            }

            context.data.management.greetings.welcomeChannel = channel

            context.typedMessaging.replySuccess(context.i18n("commands.welcome.channel.set_success", channel.asMention))
        }

    }

    override fun command(): String = "channel"

    override fun parent(): String = "welcome"

    override fun permission(): CascadePermission? = CascadePermission.of("welcome.channel", false)

}