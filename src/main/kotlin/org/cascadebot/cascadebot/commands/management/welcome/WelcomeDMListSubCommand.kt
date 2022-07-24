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
import org.cascadebot.cascadebot.utils.listOf
import org.cascadebot.cascadebot.utils.pagination.Page
import org.cascadebot.cascadebot.utils.pagination.PageObjects
import org.cascadebot.cascadebot.utils.placeholders.PlaceholderObjects
import org.cascadebot.cascadebot.utils.toPercentage
import org.cascadebot.cascadebot.utils.truncate

class WelcomeDMListSubCommand : SubCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.isNotEmpty()) {
            context.uiMessaging.replyUsage()
            return
        }
        val welcomedmMessages = context.transaction {
            return@transaction listOf(
                GuildGreetingEntity::class.java,
                mapOf(Pair("guild_id", context.getGuildId()), Pair("type", GreetingType.WELCOME_DM))
            )
        } ?: throw UnsupportedOperationException("This shouldn't happen")

        var totalWeight = 0;
        for (greeting in welcomedmMessages) {
            totalWeight += greeting.weight;
        }
        val items = welcomedmMessages;

        val overviewPage = PageObjects.EmbedPage(embed(MessageType.INFO) {
            title { name = context.i18n("commands.welcomedm.messages_title") }
            field {
                name = "Message count"
                value = welcomedmMessages.size.toString()
                inline = true
            }
            field {
                name = context.i18n("commands.welcomedm.embed_total_weight")
                value = totalWeight.toString()
                inline = true
            }
            field {
                name = context.i18n("commands.welcomedm.embed_quick_overview")
                value = run {
                    var result = ""
                    for (item in items.take(10)) {
                        if (item == null) continue
                        result += item.content.truncate(25)
                            .padEnd(25) + " - " + (item.weight.toDouble() / totalWeight.toDouble()).toPercentage() + "\n"
                    }
                    if (items.size > 10) result += context.i18n("commands.welcomedm.quick_overview_more", items.size - 10)
                    result
                }
            }
        })

        val pages: MutableList<PageObjects.EmbedPage> = mutableListOf(overviewPage)

        for (item in items) {
            pages.add(PageObjects.EmbedPage(embed(MessageType.INFO) {
                title { name = context.i18n("commands.welcomedm.messages_title") }
                field {
                    name = context.i18n("commands.welcomedm.embed_message")
                    value = PlaceholderObjects.welcomes.highlightMessage(item.content)
                }
                field {
                    name = context.i18n("commands.welcomedm.proportion_title")
                    value = (item.weight.toDouble() / totalWeight.toDouble()).toPercentage()
                    inline = true
                }
                field {
                    name = context.i18n("commands.welcomedm.embed_weight")
                    value = item.weight.toString()
                    inline = true
                }
            }))
        }


        context.uiMessaging.sendPagedMessage(pages as List<Page>)
    }

    override fun command(): String = "list"

    override fun parent(): String = "welcomedm"

}