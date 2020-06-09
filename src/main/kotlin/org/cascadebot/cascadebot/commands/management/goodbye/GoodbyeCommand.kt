/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.goodbye

import net.dv8tion.jda.api.entities.Member
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.MainCommand
import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.data.language.Language.i18n
import org.cascadebot.cascadebot.data.language.Locale
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.messaging.embed
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.utils.pagination.Page
import org.cascadebot.cascadebot.utils.pagination.PageObjects
import org.cascadebot.cascadebot.utils.placeholders.PlaceholderObjects
import org.cascadebot.cascadebot.utils.placeholders.PlaceholderObjects.goodbyes
import org.cascadebot.cascadebot.utils.placeholders.PlaceholderObjects.tags
import org.cascadebot.cascadebot.utils.placeholders.getPlaceholderUsagePage
import org.cascadebot.cascadebot.utils.toPercentage
import org.cascadebot.cascadebot.utils.truncate

class GoodbyeCommand : MainCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.isNotEmpty()) {
            context.uiMessaging.replyUsage()
            return
        }
        val goodbyeMessages = context.data.management.greetings.goodbyeMessages

        val totalWeight = goodbyeMessages.totalWeight
        val items = goodbyeMessages.itemsAndWeighting

        val overviewPage = PageObjects.EmbedPage(embed(MessageType.INFO) {
            title { name = context.i18n("commands.goodbye.messages_title") }
            field {
                name = "Message count"
                value = goodbyeMessages.size.toString()
                inline = true
            }
            field {
                name = context.i18n("commands.goodbye.embed_total_weight")
                value = goodbyeMessages.totalWeight.toString()
                inline = true
            }
            field {
                name = context.i18n("commands.goodbye.embed_quick_overview")
                value = run {
                    var result = ""
                    for ((item, weight) in items.take(10)) {
                        if (item == null) continue
                        result += item.truncate(25).padEnd(25) + " - " + (weight.toDouble() / totalWeight.toDouble()).toPercentage() + "\n"
                    }
                    if (items.size > 10) result += context.i18n("commands.goodbye.quick_overview_more", items.size - 10)
                    result
                }
            }
        })

        val pages: MutableList<PageObjects.EmbedPage> = mutableListOf(overviewPage)

        for ((message, weight) in items) {
            check(message != null) { "The message should never be null!" }
            pages.add(PageObjects.EmbedPage(embed(MessageType.INFO) {
                title { name = context.i18n("commands.goodbye.messages_title") }
                field {
                    name = context.i18n("commands.goodbye.embed_message")
                    value = PlaceholderObjects.goodbyes.highlightMessage(message)
                }
                field {
                    name = context.i18n("commands.goodbye.proportion_title")
                    value = (weight.toDouble() / totalWeight.toDouble()).toPercentage()
                    inline = true
                }
                field {
                    name = context.i18n("commands.goodbye.embed_weight")
                    value = weight.toString()
                    inline = true
                }
            }))
        }


        context.uiMessaging.sendPagedMessage(pages as List<Page>)
    }

    override fun command(): String = "goodbye"

    override fun module(): Module = Module.MANAGEMENT

    override fun permission(): CascadePermission = CascadePermission.of("goodbye", false)

    override fun additionalUsagePages(locale: Locale): List<Page> {
        return listOf(
                getPlaceholderUsagePage(
                        goodbyes.placeholders,
                        i18n(locale, "placeholders.goodbyes.title"),
                        locale
                )
        )
    }

    override fun subCommands(): Set<SubCommand> = setOf(GoodbyeAddSubCommand(), GoodbyeClearSubCommand(), GoodbyeRemoveSubCommand(), GoodbyeWeightSubCommand())

}