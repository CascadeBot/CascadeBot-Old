/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.welcome

import net.dv8tion.jda.api.entities.Member
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.data.entities.GuildGreetingChannelEntity
import org.cascadebot.cascadebot.data.entities.GuildGreetingEntity
import org.cascadebot.cascadebot.data.objects.GreetingType
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.utils.DiscordUtils
import org.cascadebot.cascadebot.utils.listOf

class WelcomeChannelSubCommand : SubCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.size > 1) {
            context.uiMessaging.replyUsage()
            return
        }

        if (context.args.isEmpty()) {
            context.typedMessaging.replyInfo(run {
                val greetings = context.getDataObject(GuildGreetingChannelEntity::class.java) ?: throw UnsupportedOperationException("This shouldn't happen")
                if (greetings.channelId != null) {
                    val channel = CascadeBot.INS.client.getGuildChannelById(greetings.channelId)
                    context.i18n("commands.welcome.current_channel", channel?.asMention!!)
                } else {
                    context.i18n("commands.welcome.no_channel_set")
                }
            })
        } else {
            if (context.testForArg("clear")) {
                context.saveDataObject(GuildGreetingChannelEntity(context.getGuildId(), null))
                context.typedMessaging.replySuccess(context.i18n("commands.welcome.channel.clear_success"))
                return
            }

            val channel = DiscordUtils.getTextChannel(context.guild, context.getArg(0))
            if (channel == null) {
                context.typedMessaging.replyDanger(context.i18n("commands.welcome.channel.invalid_channel"))
                return
            }

            context.saveDataObject(GuildGreetingChannelEntity(context.getGuildId(), channel.idLong))

            context.typedMessaging.replySuccess(context.i18n("commands.welcome.channel.set_success", channel.asMention))
        }

    }

    override fun command(): String = "channel"

    override fun parent(): String = "welcome"

    override fun permission(): CascadePermission? = CascadePermission.of("welcome.channel", false)

}