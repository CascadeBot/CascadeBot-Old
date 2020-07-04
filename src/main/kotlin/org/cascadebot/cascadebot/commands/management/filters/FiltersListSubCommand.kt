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

class FiltersListSubCommand : SubCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {

        val pages: MutableList<Page> = mutableListOf()

        pages.add(PageObjects.EmbedPage(embed(MessageType.NEUTRAL) {

        }))

        for (filter in context.data.management.filters) {
            pages.add(PageObjects.EmbedPage(filter.getFilterEmbed(context.locale)))
        }

        context.uiMessaging.sendPagedMessage(pages)
    }

    override fun command(): String = "list"

    override fun parent(): String = "filters"

    override fun permission(): CascadePermission = CascadePermission.of("filters.list", false)

}