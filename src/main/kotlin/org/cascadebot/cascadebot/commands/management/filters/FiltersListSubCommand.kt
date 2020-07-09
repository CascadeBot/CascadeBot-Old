/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.filters

import net.dv8tion.jda.api.entities.Member
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.messaging.embed
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.cascadebot.utils.pagination.Page
import org.cascadebot.cascadebot.utils.pagination.PageObjects
import org.cascadebot.cascadebot.utils.toCapitalized

class FiltersListSubCommand : SubCommand() {

    private val FILTERS_LISTED: Int = 5

    override fun onCommand(sender: Member, context: CommandContext) {

        val pages: MutableList<Page> = mutableListOf()

        val filters = context.data.management.filters
        pages.add(PageObjects.EmbedPage(embed(MessageType.INFO) {
            title {
                name = "Command Filters"
            }
            if (filters.isEmpty()) {
                description = "There are no command filters for this server!"
            } else {
                val builder = StringBuilder("Here's a quick overview of your command filters:\n\n")
                for (filter in filters.take(FILTERS_LISTED)) {
                    val icon = when {
                        !filter.configured -> context.globalEmote("offline")
                        filter.enabled -> context.globalEmote("online")
                        else -> context.globalEmote("dnd")
                    }
                    builder.append("$icon ${filter.name}\n")
                }
                if (filters.size > FILTERS_LISTED) {
                    builder.append(context.i18n("responses.more_in_list", filters.size - FILTERS_LISTED))
                }
                builder.append("\n\n")
                builder.append(context.globalEmote("online")).append(": ").append(context.i18n("words.enabled").toCapitalized()).append("\n")
                builder.append(context.globalEmote("dnd")).append(": ").append(context.i18n("words.disabled").toCapitalized()).append("\n")
                builder.append(context.globalEmote("offline")).append(": ").append(context.i18n("words.not_configured").toCapitalized()).append("\n")

                description = builder.toString()
            }

            field {
                name = context.i18n("words.enabled").toCapitalized() + " / " + context.i18n("words.disabled").toCapitalized() + context.i18n("words.not_configured").toCapitalized()
                value = "${filters.count { it.enabled && it.configured }} / ${filters.count { !it.enabled && it.configured }} / ${filters.count { !it.configured }} (${filters.size} ${context.i18n("words.total")})"
            }

        }))

        for (filter in filters) {
            pages.add(PageObjects.EmbedPage(filter.getFilterEmbed(context.locale)))
        }

        context.uiMessaging.sendPagedMessage(pages)
    }

    override fun command(): String = "list"

    override fun parent(): String = "filters"

    override fun permission(): CascadePermission = CascadePermission.of("filters.list", false)

}