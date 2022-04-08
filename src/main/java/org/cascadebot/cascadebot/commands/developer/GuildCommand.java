/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.developer;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commandmeta.RestrictedCommand;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.shared.SecurityLevel;

import java.util.Set;

public class GuildCommand extends RestrictedCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        context.getUiMessaging().replyUsage();
    }

    @Override
    public Module module() {
        return Module.DEVELOPER;
    }

    @Override
    public String description() {
        return "Allows administrative actions to be run on guilds.";
    }

    @Override
    public SecurityLevel commandLevel() {
        return SecurityLevel.DEVELOPER;
    }

    @Override
    public Set<SubCommand> subCommands() {
        return Set.of(new GuildLeaveSubCommand());
    }

    @Override
    public String command() {
        return "guild";
    }

}
