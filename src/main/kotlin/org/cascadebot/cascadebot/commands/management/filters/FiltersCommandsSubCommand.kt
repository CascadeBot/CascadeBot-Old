/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.filters

import net.dv8tion.jda.api.entities.Member
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.DeprecatedSubCommand
import org.cascadebot.cascadebot.messaging.MessageType
import org.cascadebot.cascadebot.messaging.embed
import org.cascadebot.cascadebot.permissions.CascadePermission

class FiltersCommandsSubCommand : DeprecatedSubCommand() {
    
    override fun onCommand(sender: Member, context: CommandContext) {
        if (context.args.isEmpty()) {
            context.uiMessaging.replyUsage()
            return
        }

        val args = context.args.copyOfRange(1, context.args.size)

        if (args.isEmpty()) {
            context.uiMessaging.replyUsage()
            return
        }

        val filterName = args[0]
        val filter = context.data.management.filters.find { it.name == filterName }

        if (filter == null) {
            context.typedMessaging.replyDanger(context.i18n("commands.filters.doesnt_exist", filterName))
            return
        }

        when {
            context.testForArg("link") -> {
                if (args.size < 2) {
                    context.uiMessaging.replyUsage()
                    return
                }

                val commandInput = if (args[1].startsWith(context.data.core.prefix)) args[1].substring(context.data.core.prefix.length) else args[1]
                val command: String? = CascadeBot.INS.commandManager.getCommand(commandInput, context.data)?.command()

                if (command == null) {
                    context.typedMessaging.replyDanger(context.i18n("responses.cannot_find_command_matching", args[1]))
                    return
                }

                if (filter.commands.add(command)) {
                    context.typedMessaging.replySuccess(context.i18n("commands.filters.commands.link.success", context.data.core.prefix + command, filterName))
                } else {
                    context.typedMessaging.replyInfo(context.i18n("commands.filters.commands.link.already_exists", context.data.core.prefix + command, filterName))
                }
            }
            context.testForArg("unlink") -> {
                if (args.size < 2) {
                    context.uiMessaging.replyUsage()
                    return
                }

                val commandInput = if (args[1].startsWith(context.data.core.prefix)) args[1].substring(context.data.core.prefix.length) else args[1]
                val command: String? = CascadeBot.INS.commandManager.getCommand(commandInput, context.data)?.command()

                if (command == null) {
                    context.typedMessaging.replyDanger(context.i18n("responses.cannot_find_command_matching", args[1]))
                    return
                }

                if (filter.commands.remove(command)) {
                    context.typedMessaging.replySuccess(context.i18n("commands.filters.commands.unlink.success", context.data.core.prefix + command, filterName))
                } else {
                    context.typedMessaging.replyDanger(context.i18n("commands.filters.commands.unlink.didnt_exist", context.data.core.prefix + command, filterName))
                }
            }

            context.testForArg("list") -> {
                context.typedMessaging.replyInfo(embed(MessageType.INFO) {
                    title {
                        name = context.i18n("commands.filters.commands.list.embed_title", filterName)
                    }
                    description = filter.commands
                            .joinToString("\n") { context.data.core.prefix + it }
                            .ifBlank { context.i18n("commands.filters.commands.list.no_filters") }
                })
            }
            else -> {
                context.uiMessaging.replyUsage()
            }
        }
    }

    override fun command(): String = "commands"

    override fun parent(): String = "filters"

    override fun permission(): CascadePermission = CascadePermission.of("filters.commands", false)
    
}