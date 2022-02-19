/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.filters

import net.dv8tion.jda.api.entities.Member
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.data.entities.GuildFilterEntity
import org.cascadebot.cascadebot.data.entities.GuildFilterId
import org.cascadebot.cascadebot.permissions.CascadePermission

class FiltersDeleteSubCommand : SubCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.isEmpty()) {
            context.uiMessaging.replyUsage()
            return
        }

        val name = context.getArg(0)

        val filter = context.getDataObject(GuildFilterEntity::class.java, GuildFilterId(name, context.getGuildId()))

        if (filter == null) {
            context.typedMessaging.replyDanger(context.i18n("commands.filters.doesnt_exist", name))
            return
        }

        context.transactionNoReturn {
            delete(filter)
        }

        context.typedMessaging.replySuccess(context.i18n("commands.filters.delete.success", name))
    }

    override fun command(): String = "delete"

    override fun parent(): String = "filters"

    override fun permission(): CascadePermission = CascadePermission.of("filters.delete", false)

}