package org.cascadebot.cascadebot.commandmeta

import net.dv8tion.jda.api.interactions.commands.build.SubcommandData

abstract class SubCommand(
    command: String,
    permission: String,
    val description: String,
    val parent: ParentCommand,
    val group: SubCommandGroup? = null
) : ExecutableCommand(
    command, parent.module, parent.permission + "." + if (group != null) {
        group.permission + "."
    } else {
        ""
    } + permission
) {

}