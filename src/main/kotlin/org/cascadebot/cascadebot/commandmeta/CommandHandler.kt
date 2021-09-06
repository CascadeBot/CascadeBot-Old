package org.cascadebot.cascadebot.commandmeta

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData
import org.cascadebot.cascadebot.CascadeBot

class CommandHandler(val commandManager: CommandManager) {

    private fun buildSubCommands(command: SubCommand, subCommands: MutableMap<ParentCommand, MutableList<SubCommand>>, subCommandGroups: MutableMap<ParentCommand, MutableList<SubCommandGroup>>, subCommandsOfGroup: MutableMap<SubCommandGroup, MutableList<SubCommand>>) {
        if (command.group != null) {
            if (!subCommandGroups.contains(command.parent)) {
                subCommandGroups[command.parent] = mutableListOf(command.group)
            } else {
                if (!subCommandGroups[command.parent]?.contains(command.group)!!) {
                    subCommandGroups[command.parent]?.add(command.group)
                }
            }
            if (!subCommandsOfGroup.contains(command.group)) {
                subCommandsOfGroup[command.group] = mutableListOf(command)
            } else {
                if (!subCommandsOfGroup[command.group]?.contains(command)!!) {
                    subCommandsOfGroup[command.group]?.add(command)
                }
            }
        } else {
            if (!subCommands.contains(command.parent)) {
                subCommands[command.parent] = mutableListOf(command)
            } else {
                subCommands[command.parent]?.add(command)
            }
        }
    }

    private fun upsertSubCommands(jda: JDA, parentCommand: ParentCommand, subCommands: MutableMap<ParentCommand, MutableList<SubCommand>>, subCommandGroups: MutableMap<ParentCommand, MutableList<SubCommandGroup>>, subCommandsOfGroup: MutableMap<SubCommandGroup, MutableList<SubCommand>>) {
        CascadeBot.LOGGER.info("Upserting command " + parentCommand.command)
        val data = CommandData(parentCommand.command, parentCommand.description)
        if (subCommands.contains(parentCommand)) {
            for (subCommand in subCommands[parentCommand]!!) {
                CascadeBot.LOGGER.info("\tWith sub command " + subCommand.command)
                data.addSubcommands(subCommand.commandData.buildSubCommandData(subCommand))
            }
        }
        if (subCommandGroups.contains(parentCommand)) {
            for (subCommandGroup in subCommandGroups[parentCommand]!!) {
                CascadeBot.LOGGER.info("\tWith sub command group " + subCommandGroup.groupName)
                val groupData = SubcommandGroupData(subCommandGroup.groupName, subCommandGroup.description)
                for (subCommand in subCommandsOfGroup[subCommandGroup]!!) {
                    CascadeBot.LOGGER.info("\t\tWith sub command " + subCommand.command)
                    groupData.addSubcommands(subCommand.commandData.buildSubCommandData(subCommand))
                }
                data.addSubcommandGroups(groupData)
            }
        }
        jda.upsertCommand(data).queue { CascadeBot.LOGGER.info("Command " + it.name + " upserted with id " + it.id) } // TODO store these ids somewhere?
    }

    fun updateCommands(jda: JDA) {
        val subCommands: MutableMap<ParentCommand, MutableList<SubCommand>> = mutableMapOf()
        val subCommandGroups: MutableMap<ParentCommand, MutableList<SubCommandGroup>> = mutableMapOf()
        val subCommandsOfGroup: MutableMap<SubCommandGroup, MutableList<SubCommand>> = mutableMapOf()

        for (command in commandManager.commands) {
            when (command) {
                is SubCommand -> {
                    buildSubCommands(command, subCommands, subCommandGroups, subCommandsOfGroup)
                }
                is ExecutableRootCommand -> {
                    CascadeBot.LOGGER.info("Upserting command " + command.command)
                    jda.upsertCommand(command.commandData.buildCommandData(command)).queue { CascadeBot.LOGGER.info("Command " + it.name + " upserted with id " + it.id) } // TODO store these ids somewhere?
                }
            }
        }

        for (parentCommand in ParentCommand.values()) {
            upsertSubCommands(jda, parentCommand, subCommands, subCommandGroups, subCommandsOfGroup)
        }

        CascadeBot.LOGGER.info("All commands queued for upsert")
        // TODO exit when jda is done, maybe don't use jda?
    }

    fun pushCommands(jda: JDA) {
        val subCommands: MutableMap<ParentCommand, MutableList<SubCommand>> = mutableMapOf()
        val subCommandGroups: MutableMap<ParentCommand, MutableList<SubCommandGroup>> = mutableMapOf()
        val subCommandsOfGroup: MutableMap<SubCommandGroup, MutableList<SubCommand>> = mutableMapOf()

        jda.retrieveCommands().queue {
            for (command in commandManager.commands) {
                when(command) {
                    is SubCommand -> {
                        buildSubCommands(command, subCommands, subCommandGroups, subCommandsOfGroup)
                    }
                    is ExecutableRootCommand -> {
                        if (!it.map { com -> com.name }.contains(command.command)) {
                            CascadeBot.LOGGER.info("Upserting command " + command.command)
                            jda.upsertCommand(command.commandData.buildCommandData(command)).queue { CascadeBot.LOGGER.info("Command " + it.name + " upserted with id " + it.id) }
                        }
                    }
                }
            }

            for (parentCommand in ParentCommand.values()) {
                if (!it.map { com -> com.name }.contains(parentCommand.command)) {
                    upsertSubCommands(jda, parentCommand, subCommands, subCommandGroups, subCommandsOfGroup)
                }
            }

            CascadeBot.LOGGER.info("All commands queued for upsert")
            // TODO exit when jda is done, maybe don't use jda?
        }
    }
    
}