/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.welcome

import net.dv8tion.jda.api.entities.Member
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.MainCommand
import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.commandmeta.SubCommand

class WelcomeDMCommand : MainCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        context.uiMessaging.replyUsage()
    }

    override fun command(): String = "welcomedm"

    override fun subCommands(): Set<SubCommand> =
            setOf(
                    WelcomeDMAddSubCommand(),
                    WelcomeDMRemoveSubCommand(),
                    WelcomeDMClearSubCommand(),
                    WelcomeDMWeightSubCommand(),
                    WelcomeDMListSubCommand()
            )

    override fun module(): Module = Module.MANAGEMENT

}