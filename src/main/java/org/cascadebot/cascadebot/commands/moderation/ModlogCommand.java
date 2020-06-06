package org.cascadebot.cascadebot.commands.moderation;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.MainCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import java.util.Set;

public class ModlogCommand extends MainCommand {

    public void onCommand(Member sender, CommandContext context) {
        context.getUiMessaging().replyUsage();

    }

    public String command() {
        return "modlog";
    }

    public CascadePermission permission() {
        return CascadePermission.of("modlog", false, Module.MODERATION);
    }

    public Module module() {
        return Module.MODERATION;
    }

    public Set<SubCommand> subCommands() {
        return Set.of(new ModlogEnableSubCommand(), new ModlogDisableSubCommand(), new ModlogEventsSubCommand(),
                new ModlogChannelSubCommand(), new ModlogPruneSubCommand(), new ModlogClearSubCommand());
    }
}
