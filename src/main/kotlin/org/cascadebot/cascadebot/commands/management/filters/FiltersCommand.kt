/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.filters

import net.dv8tion.jda.api.entities.Member
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.MainCommand
import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.permissions.CascadePermission

class FiltersCommand : MainCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        context.uiMessaging.replyUsage()
    }

    override fun command(): String = "filters"

    override fun module(): Module = Module.MANAGEMENT

    override fun subCommands(): Set<SubCommand> = setOf(
            FiltersCreateSubCommand(),
            FiltersDeleteSubCommand(),
            FiltersEnableSubCommand(),
            FiltersDisableSubCommand(),
            FiltersTypeSubCommand(),
            FiltersOperatorSubCommand(),
            FiltersChannelsSubCommand()
    )

    override fun permission(): CascadePermission? = null

}