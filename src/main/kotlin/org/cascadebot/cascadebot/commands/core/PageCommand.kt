/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.core

import net.dv8tion.jda.api.entities.Member
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.CoreCommand

class PageCommand : CoreCommand() {

    override fun command(): String = "page"

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.isEmpty()) {
            context.uiMessaging.replyUsage()
            return
        }
        if (!context.isArgInteger(0)) {
            context.typedMessaging.replyDanger(context.i18n("commands.page.page_must_number"))
            return
        }
        val pageIndex = context.getArgAsInteger(0)!!
        val buttonCaches = context.data.buttonsCache.mapValues { it.value.values }[context.channel.idLong]?.filter { it.ownerId == context.user.idLong }
        if (buttonCaches != null && buttonCaches.isNotEmpty()) {
            val pagesEntry = context.data.pageCache
                    .filter { buttonCaches.find { group -> group.messageId == it.key} != null }
                    .entries
                    .toList()
                    .sortedByDescending { it.key }
                    .getOrNull(0)
            if (pagesEntry != null) {
                if (pageIndex - 1 !in 0..pagesEntry.value.pageCount) {
                    context.typedMessaging.replyDanger(context.i18n("commands.page.not_in_range", 1, pagesEntry.value.pageCount))
                    return
                }
                context.channel.retrieveMessageById(pagesEntry.key).queue({
                    pagesEntry.value.currentPage = pageIndex
                    pagesEntry.value.getPage(pageIndex).pageShow(it, pageIndex, pagesEntry.value.pageCount)
                }, {
                    context.typedMessaging.replyException(context.i18n("commands.page.message_error"), it)
                })
                return
            } else {
                context.typedMessaging.replyDanger(context.i18n("commands.page.no_pages"))
            }
        } else {
            context.typedMessaging.replyDanger(context.i18n("commands.page.no_pages"))
        }
    }

}