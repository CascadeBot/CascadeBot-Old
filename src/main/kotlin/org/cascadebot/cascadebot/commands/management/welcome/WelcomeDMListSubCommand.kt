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
import org.cascadebot.cascadebot.utils.pagination.Page
import org.cascadebot.cascadebot.utils.pagination.PageObjects
import org.cascadebot.cascadebot.utils.placeholders.PlaceholderObjects
import org.cascadebot.cascadebot.utils.toPercentage
import org.cascadebot.cascadebot.utils.truncate

class WelcomeDMListSubCommand : DeprecatedSubCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.isNotEmpty()) {
            context.uiMessaging.replyUsage()
            return
        }
        val welcomeMessages = context.data.management.greetings.welcomeDMMessages

        val totalWeight = welcomeMessages.totalWeight
        val items = welcomeMessages.itemsAndWeighting

        val overviewPage = PageObjects.EmbedPage(embed(MessageType.INFO) {
            title { name = context.i18n("commands.welcomedm.overview_messages_title") }
            if (items.isEmpty()) {
                description = context.i18n("commands.welcomedm.no_messages")
            } else {
                field {
                    name = context.i18n("commands.welcome.embed_message_count")
                    value = welcomeMessages.size.toString()
                    inline = true
                }
                field {
                    name = context.i18n("commands.welcome.embed_total_weight")
                    value = welcomeMessages.totalWeight.toString()
                    inline = true
                }
                field {
                    name = context.i18n("commands.welcome.embed_quick_overview")
                    value = run {
                        var result = ""
                        for ((item, weight) in items.take(10)) {
                            if (item == null) continue
                            result += item.truncate(25).padEnd(25) + " - " + (weight.toDouble() / totalWeight.toDouble()).toPercentage() + "\n"
                        }
                        if (items.size > 10) result += context.i18n("responses.more_in_list", items.size - 10)
                        result
                    }
                }
            }
        })

        val pages: MutableList<PageObjects.EmbedPage> = mutableListOf(overviewPage)

        for ((index, weightPair) in items.withIndex()) {
            check(weightPair.item != null) { "The message should never be null!" }
            pages.add(PageObjects.EmbedPage(embed(MessageType.INFO) {
                title { name = context.i18n("commands.welcomedm.index_message_title", index + 1) }
                field {
                    name = context.i18n("commands.welcome.embed_message")
                    value = PlaceholderObjects.welcomes.highlightMessage(weightPair.item)
                }
                field {
                    name = context.i18n("commands.welcome.proportion_title")
                    value = (weightPair.weight.toDouble() / totalWeight.toDouble()).toPercentage()
                    inline = true
                }
                field {
                    name = context.i18n("commands.welcome.embed_weight")
                    value = weightPair.weight.toString()
                    inline = true
                }
            }))
        }


        context.uiMessaging.sendPagedMessage(pages as List<Page>)
    }

    override fun command(): String = "list"

    override fun parent(): String = "welcomedm"

    override fun permission(): CascadePermission? = CascadePermission.of("welcomedm.list", false)

}