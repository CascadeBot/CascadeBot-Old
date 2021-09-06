/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.filters

import net.dv8tion.jda.api.entities.Member
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.DeprecatedSubCommand
import org.cascadebot.cascadebot.permissions.CascadePermission

class FiltersDeleteSubCommand : DeprecatedSubCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.isEmpty()) {
            context.uiMessaging.replyUsage()
            return
        }

        val name = context.getArg(0)

        val filter = context.data.management.filters.find { it.name == name }

        if (filter == null) {
            context.typedMessaging.replyDanger(context.i18n("commands.filters.doesnt_exist", name))
            return
        }

        context.data.management.filters.removeIf { it.name == name }

        context.typedMessaging.replySuccess(context.i18n("commands.filters.delete.success", name))
    }

    override fun command(): String = "delete"

    override fun parent(): String = "filters"

    override fun permission(): CascadePermission = CascadePermission.of("filters.delete", false)

}