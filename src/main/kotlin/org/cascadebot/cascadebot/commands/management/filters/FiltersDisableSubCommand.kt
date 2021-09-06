/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.filters

import net.dv8tion.jda.api.entities.Member
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.DeprecatedSubCommand
import org.cascadebot.cascadebot.permissions.CascadePermission

class FiltersDisableSubCommand : DeprecatedSubCommand() {

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

        if (filter.enabled) {
            filter.enabled = false
            context.typedMessaging.replySuccess(context.i18n("commands.filters.disable.success", name))
        } else {
            context.typedMessaging.replyInfo(context.i18n("commands.filters.disable.already_disabled", name))
        }
    }

    override fun command(): String = "disable"

    override fun parent(): String = "filters"

    override fun permission(): CascadePermission = CascadePermission.of("filters.disable", false)

}