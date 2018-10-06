package com.cascadebot.cascadebot.events;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.Config;
import com.cascadebot.cascadebot.Constants;
import com.cascadebot.cascadebot.commands.ICommand;
import com.cascadebot.cascadebot.commands.CommandContext;
import com.cascadebot.cascadebot.objects.GuildData;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommandListener extends ListenerAdapter {

    private static final ThreadGroup COMMAND_THREADS = new ThreadGroup("Command Threads");
    private static final ExecutorService COMMAND_POOL = Executors.newFixedThreadPool(5, r ->
            new Thread(COMMAND_THREADS, r, "Command Pool-" + COMMAND_THREADS.activeCount()));

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        String message = Constants.MULTISPACE_REGEX.matcher(event.getMessage().getContentRaw()).replaceAll(" ");
        String prefix = Config.VALUES.defaultPrefix; //TODO: Add guild data prefix here
        GuildData guildData = new GuildData(event.getGuild().getIdLong());
        if (message.startsWith(prefix)) {
            String command = message.substring(prefix.length()); // Remove prefix from command
            String commandString = command.split(" ")[0]; // Get first string before a space
            String[] args = ArrayUtils.remove(command.split(" "), 0); // Remove the command portion of the string

            CommandContext context = new CommandContext();
            context.setChannel(event.getChannel());
            context.setArgs(args);
            context.setGuildData(guildData);
            context.setMember(event.getMember());

            ICommand cmd = CascadeBot.instance().getCommandManager().getCommand(commandString, event.getAuthor(), guildData);
            if (cmd != null) {
                context.setOriginalCommand(commandString);
                dispatchCommand(cmd, context);
            }
        }
    }

    private void dispatchCommand(final ICommand command, final CommandContext context) {
        COMMAND_POOL.submit(() -> {
            CascadeBot.LOGGER.info("ICommand {} executed by {} with args: {}",
                    command.defaultCommand() + (command.defaultCommand().equalsIgnoreCase(context.getOriginalCommand()) ? "" : context.getOriginalCommand()),
                    context.getUser().getName() + "#" + context.getUser().getDiscriminator(), // TODO: Util this
                    Arrays.toString(context.getArgs()));
            command.onCommand(context.getMember(), context);
        });
    }

    public static void shutdownCommandPool() {
        COMMAND_POOL.shutdown();
    }


}
