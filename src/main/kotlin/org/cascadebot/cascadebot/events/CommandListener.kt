package org.cascadebot.cascadebot.events

import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.MDCException
import org.cascadebot.cascadebot.commandmeta.CascadeCommandData
import org.cascadebot.cascadebot.commandmeta.CommandArgs
import org.cascadebot.cascadebot.commandmeta.CommandContext
import org.cascadebot.cascadebot.commandmeta.CommandPath
import org.cascadebot.cascadebot.commandmeta.ExecutableCommand
import org.cascadebot.cascadebot.commandmeta.ExecutableRootCommand
import org.cascadebot.cascadebot.commandmeta.MainCommand
import org.cascadebot.cascadebot.commandmeta.SubCommand
import org.cascadebot.cascadebot.data.objects.CommandFilter
import org.cascadebot.cascadebot.metrics.Metrics
import org.cascadebot.cascadebot.permissions.CascadePermission
import org.cascadebot.shared.utils.ThreadPoolExecutorLogged
import org.slf4j.MDC
import java.util.Arrays
import java.util.concurrent.atomic.AtomicInteger

class CommandListener : ListenerAdapter() {

    override fun onReady(event: ReadyEvent) {
        CascadeBot.INS.client.retrieveCommands().queue{
            for (command in CascadeBot.INS.commandManager.commands.map { it.first }) {
                for (discordCom in it) {
                    if (discordCom.subcommandGroups.size > 0) {
                        for (subGroup in discordCom.subcommandGroups) {
                            for (subComm in subGroup.subcommands) {
                                val path = listOf(discordCom.name, subGroup.name, subComm.name)
                                if (command == CommandPath(0, path)) {
                                    command.rootId = discordCom.idLong
                                }
                            }
                        }
                    } else if (discordCom.subcommands.size > 0) {
                        for (subComm in discordCom.subcommands) {
                            val path = listOf(discordCom.name, subComm.name)
                            if (command == CommandPath(0, path)) {
                                command.rootId = discordCom.idLong
                            }
                        }
                    } else {
                        val path = listOf(discordCom.name)
                        if (command == CommandPath(0, path)) {
                            command.rootId = discordCom.idLong
                        }
                    }
                }
            }
        }
    }

    override fun onSlashCommand(event: SlashCommandEvent) {
        val args = CommandArgs(event.options.groupBy { it.name })

        val command = CascadeBot.INS.commandManager.getCommand(event.commandIdLong, event.commandPath) ?: return // TODO check for guild commands and if one isn't found reply with some sort of message saying the command doesn't exist

        event.deferReply()
        dispatchCommand(command, args, CommandContext(event.jda, event.textChannel, event.guild!!))
    }

    private fun processFilters(cmd: MainCommand, context: CommandContext): Boolean {
        for (filter in context.data.management.filters) {
            if (filter.evaluateFilter(
                    cmd.command(),
                    context.channel,
                    context.member
                ) === CommandFilter.FilterResult.DENY
                && !context.hasPermission("filters.bypass")
            ) {
                return false
            }
        }
        return true
    }

    private fun dispatchCommand(
        command: ExecutableCommand,
        commandArgs: CommandArgs,
        context: CommandContext
    ): Boolean {
        COMMAND_POOL.submit {
            MDC.put("cascade.sender", context.member.toString())
            MDC.put("cascade.guild", context.guild.toString())
            MDC.put("cascade.channel", context.channel.toString())
            MDC.put("cascade.shard_info", context.jda.shardInfo.shardString)
            MDC.put(
                "cascade.command",
                command.command + if (command is ExecutableRootCommand) "" else " (Sub-command)"
            )
            if (command is SubCommand) {
                MDC.put("cascade.parent", command.parent.command)
                val group = command.group
                if (group != null) {
                    MDC.put("cascade.commandgroup", group.groupName)
                }
            }
            MDC.put("cascade.trigger", context.trigger)
            //MDC.put("cascade.args", Arrays.toString(context.args))
            CascadeBot.LOGGER.info(
                "{}Command {}{} executed by {}",
                if (command is ExecutableRootCommand) "" else "Sub",
                command.command,
                if (command.command.equals(
                        context.trigger,
                        ignoreCase = true
                    )
                ) "" else " (Trigger: " + context.trigger + ")",
                context.user.asTag/*,
                Arrays.toString(context.args)*/
            )
            Metrics.INS.commandsExecuted.labels(command.javaClass.simpleName)
                .inc()
            val commandTimer =
                Metrics.INS.commandExecutionTime.labels(command.javaClass.simpleName)
                    .startTimer()
            try {
                command.onCommand(context, commandArgs, context.data)
            } catch (e: Exception) {
                Metrics.INS.commandsErrored.labels(command.javaClass.simpleName)
                    .inc()
                context.typedMessaging.replyException(context.i18n("responses.failed_to_run_command"), e)
                CascadeBot.LOGGER.error("Error while running a command!", MDCException.from(e))
            } finally {
                CascadeBot.clearCascadeMDC()
                commandTimer.observeDuration()
            }
        }
        return true
    }

    private fun isAuthorised(command: ExecutableCommand, context: CommandContext): Boolean {
        if (!CascadeBot.INS.permissionsManager.isAuthorised(command, context.data, context.member)) {
            if (context.data.core.showPermErrors) {
                context.uiMessaging.sendPermissionError(CascadePermission.of(command.permission, false))
            }
            return false
        }
        return true
    }

    companion object {
        private val COMMAND_THREADS = ThreadGroup("Command Threads")
        private val threadCounter = AtomicInteger(0)
        private val COMMAND_POOL = ThreadPoolExecutorLogged.newCachedThreadPool(
            { r: Runnable? ->
                Thread(
                    COMMAND_THREADS,
                    r,
                    "Command Pool-" + threadCounter.incrementAndGet()
                )
            }, CascadeBot.LOGGER
        )

        fun shutdownCommandPool() {
            COMMAND_POOL.shutdown()
        }
    }
}