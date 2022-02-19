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

class FiltersEnableSubCommand : SubCommand() {

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

        if (!filter.enabled) {
            filter.enabled = true
            context.typedMessaging.replySuccess(context.i18n("commands.filters.enable.success", name))
        } else {
            context.typedMessaging.replyInfo(context.i18n("commands.filters.enable.already_disabled", name))
        }

        context.saveDataObject(filter)
    }

    override fun command(): String = "enable"

    override fun parent(): String = "filters"

    override fun permission(): CascadePermission = CascadePermission.of("filters.enable", false)

}