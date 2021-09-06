package org.cascadebot.cascadebot.commandmeta

import org.cascadebot.cascadebot.data.objects.GuildData

abstract class ExecutableCommand(
    val command: String,
    val module: Module,
    val permission: String
) {

    abstract fun onCommand(context: CommandContext, args: CommandArgs, data: GuildData)
    
    abstract val commandData: CascadeCommandData

}