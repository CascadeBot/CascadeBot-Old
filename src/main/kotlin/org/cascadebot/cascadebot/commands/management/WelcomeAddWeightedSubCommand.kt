/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management

import net.dv8tion.jda.api.entities.Member
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.permissions.CascadePermission

class WelcomeAddWeightedSubCommand : SubCommand() {

    override fun onCommand(sender: Member, context: CommandContext) {
        TODO("Not yet implemented")
    }

    override fun command(): String = "addweighted"

    override fun parent(): String = "welcome"

    override fun permission(): CascadePermission? = CascadePermission.of("welcome.add", false)

}