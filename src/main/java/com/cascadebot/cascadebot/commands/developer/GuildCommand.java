package com.cascadebot.cascadebot.commands.developer;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import com.cascadebot.cascadebot.commandmeta.ICommandRestricted;
import com.cascadebot.cascadebot.commandmeta.Module;
import com.cascadebot.cascadebot.commands.subcommands.guild.GuildSaveSubCommand;
import com.cascadebot.shared.SecurityLevel;
import net.dv8tion.jda.core.entities.Member;

import java.util.Set;

public class GuildCommand implements ICommandRestricted {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        context.replyUsage(this);
    }

    @Override
    public Module getModule() {
        return Module.DEVELOPER;
    }

    @Override
    public SecurityLevel getCommandLevel() {
        return SecurityLevel.DEVELOPER;
    }

    @Override
    public Set<ICommandExecutable> getSubCommands() {
        return Set.of(new GuildSaveSubCommand());
    }

    @Override
    public Set<Argument> getUndefinedArguments() {
        return Set.of(Argument.of("flag", "", ArgumentType.OPTIONAL,
                Set.of(
                        Argument.of("guild_id", "Toggles a flag for this guild [or a different guild].", ArgumentType.OPTIONAL)
                )
        ));
    }

    @Override
    public String command() {
        return "guild";
    }

    @Override
    public String description() {
        return "interact with the guild";
    }

}
