package org.cascadebot.cascadebot.commandmeta

import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData

class CascadeCommandData {

    val optionData: MutableList<OptionData> = mutableListOf()

    fun addOption(type: OptionType, name: String, description: String, required: Boolean = true): CascadeCommandData {
        optionData.add(OptionData(type, name, description, required))
        return this
    }

    fun buildCommandData(command: ExecutableRootCommand): CommandData {
        return CommandData(command.command, command.description)
            .addOptions(optionData)
    }

    fun buildSubCommandData(command: SubCommand): SubcommandData {
        return SubcommandData(command.command, command.description)
            .addOptions(optionData)
    }

}