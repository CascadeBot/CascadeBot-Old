/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.filters

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.DeprecatedSubCommand
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.messaging.embed
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.utils.DiscordUtils

class FiltersChannelsSubCommand : DeprecatedSubCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.isEmpty()) {
            context.uiMessaging.replyUsage()
            return
        }

        val args = context.args.copyOfRange(1, context.args.size)

        if (args.isEmpty()) {
            context.uiMessaging.replyUsage()
            return
        }

        val filterName = args[0]
        val filter = context.data.management.filters.find { it.name == filterName }

        if (filter == null) {
            context.typedMessaging.replyDanger(context.i18n("commands.filters.doesnt_exist", filterName))
            return
        }

        when {
            context.testForArg("link") -> {
                if (args.size < 2) {
                    context.uiMessaging.replyUsage()
                    return
                }

                val channel: TextChannel? = DiscordUtils.getTextChannel(context.guild, args[1])

                if (channel == null) {
                    context.typedMessaging.replyDanger(context.i18n("responses.cannot_find_channel_matching", args[1]))
                    return
                }

                if (filter.channelIds.add(channel.idLong)) {
                    context.typedMessaging.replySuccess(context.i18n("commands.filters.channels.link.success", channel.asMention, filterName))
                } else {
                    context.typedMessaging.replyInfo(context.i18n("commands.filters.channels.link.already_exists", channel.asMention, filterName))
                }
            }
            context.testForArg("unlink") -> {
                if (args.size < 2) {
                    context.uiMessaging.replyUsage()
                    return
                }

                val channel: TextChannel? = DiscordUtils.getTextChannel(context.guild, args[1])

                if (channel == null) {
                    context.typedMessaging.replyDanger(context.i18n("commands.filters.channels.invalid_channel", args[1]))
                    return
                }

                if (filter.channelIds.remove(channel.idLong)) {
                    context.typedMessaging.replySuccess(context.i18n("commands.filters.channels.unlink.success", channel.asMention, filterName))
                } else {
                    context.typedMessaging.replyDanger(context.i18n("commands.filters.channels.unlink.didnt_exist", channel.asMention, filterName))
                }
            }

            context.testForArg("list") -> {
                context.typedMessaging.replyInfo(embed(MessageType.INFO) {
                    title {
                        name = context.i18n("commands.filters.channels.list.embed_title", filterName)
                    }
                    description = filter.channelIds
                            .mapNotNull { context.guild.getTextChannelById(it)?.asMention }
                            .joinToString("\n")
                            .ifBlank { context.i18n("commands.filters.channels.list.no_filters") }
                })
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