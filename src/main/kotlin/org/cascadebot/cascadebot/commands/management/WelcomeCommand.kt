/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management

import net.dv8tion.jda.api.entities.Member
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.MainCommand
import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.permissions.CascadePermission

class WelcomeCommand : MainCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.isNotEmpty()) {
            context.uiMessaging.replyUsage()
            return
        }
        TODO("Implement functions...")
    }

    override fun command(): String = "welcome"

    override fun module(): Module = Module.MANAGEMENT

    override fun permission(): CascadePermission = CascadePermission.of("welcome", false)

    override fun subCommands(): Set<SubCommand> = setOf(WelcomeAddSubCommand(), WelcomeChannelSubCommand(), WelcomeClearSubCommand(), WelcomeRemoveSubCommand(), WelcomeWeightSubCommand())

}