package org.cascadebot.cascadebot.commands.management.filters;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import java.util.Set;

public class FiltersCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {

    }

    @Override
    public Module getModule() {
        return Module.MANAGEMENT;
    }

    @Override
    public Set<ISubCommand> getSubCommands() {
        return Set.of(
                new FiltersChannelsSubCommand(),
                new FiltersCommandsSubCommand(),
                new FiltersCreateSubCommand(),
                new FiltersDeleteSubCommand(),
                new FiltersDisableSubCommand(),
                new FiltersEnableSubCommand(),
                new FiltersOperatorSubCommand(),
                new FiltersRolesSubCommand(),
                new FiltersTypeSubCommand(),
                new FiltersUsersSubCommand(),
                new FiltersListSubCommand()
        );
    }

    @Override
    public String command() {
        return "filters";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("filters", false);
    }

}
