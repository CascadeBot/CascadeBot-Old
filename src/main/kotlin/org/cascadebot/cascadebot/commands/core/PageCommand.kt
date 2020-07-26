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
        val pageIndex = context.getArgAsInteger(0)
        val buttonCaches = context.data.buttonsCache.mapValues { it.value.values }[context.channel.idLong]?.filter { it.ownerId == context.user.idLong }
        if (buttonCaches != null && buttonCaches.isNotEmpty()) {
            context.data.pageCache.filter { buttonCaches.find { group -> group.messageId == it.key} != null }
        }
    }

}