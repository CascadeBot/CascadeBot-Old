package org.cascadebot.cascadebot.commandmeta

import net.dv8tion.jda.api.interactions.commands.build.CommandData

// TODO this is a terrible name, change it
abstract class ExecutableRootCommand(command: String, module: Module, val description: String, permission: String) : ExecutableCommand(
    command, module,
    permission
) {

}