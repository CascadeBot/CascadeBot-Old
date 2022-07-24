/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.welcome

import net.dv8tion.jda.api.entities.Member
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.data.entities.GuildGreetingEntity
import org.cascadebot.cascadebot.data.objects.GreetingType
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.messaging.embed
import org.cascadebot.cascadebot.utils.FormatUtils
import org.cascadebot.cascadebot.utils.listOf

class WelcomeAddSubCommand : SubCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.isEmpty()) {
            context.uiMessaging.replyUsage()
            return
        }

        val message = context.getMessage(0)

        val greeting = GuildGreetingEntity(context.getGuildId(), GreetingType.WELCOME, message);
        context.saveDataObject(greeting);

        val greetings = context.transaction {
            return@transaction listOf(GuildGreetingEntity::class.java, mapOf(Pair("guild_id", context.getGuildId()), Pair("type", GreetingType.WELCOME)))
        }

        val proportion = greeting.weight;

        context.typedMessaging.replySuccess(embed(MessageType.SUCCESS) {
            title {
                name = context.i18n("commands.welcome.add.success_title")
            }
            description = "${context.i18n("commands.welcome.add.success_text_1")}\n" +
                    "```\n$message\n```\n" +
                    context.i18n("commands.welcome.add.success_text_2", greetings!!.size, FormatUtils.round((proportion * 100).toDouble(), 0).toInt())
        })

    }

    override fun command(): String = "add"

    override fun parent(): String = "welcome"

}