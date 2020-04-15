package org.cascadebot.cascadebot.commands.management.filters;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class FiltersListSubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {

    }

    @Override
    public String command() {
        return "list";
    }

    @Override
    public String parent() {
        return "filters";
    }

    @Override
    public CascadePermission getPermission() {
        return null;
    }

}