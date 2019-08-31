/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.developer;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandRestricted;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.shared.SecurityLevel;

import java.util.Set;

public class GuildCommand implements ICommandRestricted {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        context.getUIMessaging().replyUsage();
    }

    @Override
    public Module getModule() {
        return Module.DEVELOPER;
    }

    @Override
    public String description() {
        return "Allows administrative actions to be run on guilds.";
    }

    @Override
    public SecurityLevel getCommandLevel() {
        return SecurityLevel.DEVELOPER;
    }

    @Override
    public Set<ISubCommand> getSubCommands() {
        return Set.of(new GuildSaveSubCommand(), new GuildLeaveSubCommand(), new GuildFlagSubCommand(), new GuildInfoSubCommand());
    }

    @Override
    public String command() {
        return "guild";
    }

}
