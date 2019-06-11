/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascade.commands.developer;

import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascade.commandmeta.CommandContext;
import org.cascadebot.cascade.commandmeta.ICommandExecutable;
import org.cascadebot.cascade.commandmeta.ICommandRestricted;
import org.cascadebot.cascade.commandmeta.Module;
import org.cascadebot.shared.SecurityLevel;

import java.util.Set;

public class GuildCommand implements ICommandRestricted {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        context.getUIMessaging().replyUsage(this);
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
        return Set.of(new GuildSaveSubCommand(), new GuildLeaveSubCommand(), new GuildFlagSubCommand(), new GuildInfoSubCommand());
    }

    @Override
    public String command() {
        return "guild";
    }

    @Override
    public String description() {
        return "Allows administrative actions to be run on guilds";
    }

}
