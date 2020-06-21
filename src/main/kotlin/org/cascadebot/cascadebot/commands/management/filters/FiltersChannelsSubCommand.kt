/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.filters

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.utils.DiscordUtils

class FiltersChannelsSubCommand : SubCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.isEmpty()) {
            context.uiMessaging.replyUsage()
            return
        }

        when {
            context.testForArg("add") -> {
                val args = context.args.copyOfRange(1, context.args.size)

                // <filter> <channel>
                if (args.size < 2) {
                    context.uiMessaging.replyUsage()
                    return
                }

                val filterName = args[0]
                val filter = context.data.management.filters.find { it.name == filterName }

                if (filter == null) {
                    context.typedMessaging.replyDanger(context.i18n("commands.filters.doesnt_exist", filterName))
                    return
                }

                val channel: TextChannel? = DiscordUtils.getTextChannel(context.guild, args[1])

                if (channel == null) {
                    context.typedMessaging.replyDanger(context.i18n("commands.filters.channels.invalid_channel", args[1]))
                    return
                }

                filter.channelIds.add(channel.idLong)
                context.typedMessaging.replySuccess(context.i18n("commands.filters.channels.add.success", channel.asMention))
            }
            context.testForArg("remove") -> {

            }
            context.testForArg("list") -> {

            }
            else -> {
                context.uiMessaging.replyUsage()
            }
        }
    }

    override fun command(): String = "channels"

    override fun parent(): String = "filters"

    override fun permission(): CascadePermission? = CascadePermission.of("filters.channels", false)

}