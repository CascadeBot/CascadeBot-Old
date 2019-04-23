/*

  * Copyright (c) 2019 CascadeBot. All rights reserved.
  * Licensed under the MIT license.

 */

package org.cascadebot.cascadebot.commands.tags;

import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandExecutable;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commands.subcommands.module.ModuleDisableSubCommand;
import org.cascadebot.cascadebot.commands.subcommands.module.ModuleEnableSubCommand;
import org.cascadebot.cascadebot.commands.subcommands.module.ModuleListSubCommand;
import org.cascadebot.cascadebot.commands.subcommands.tag.TagCreateSubCommand;
import org.cascadebot.cascadebot.commands.subcommands.tag.TagDeleteSubCommand;
import org.cascadebot.cascadebot.commands.subcommands.tag.TagListSubCommand;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import java.util.Set;

public class TagCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        context.getUIMessaging().replyUsage(this);
    }

    @Override
    public Module getModule() {
        return Module.TAGS;
    }

    @Override
    public String command() {
        return "tag";
    }

    @Override
    public Set<ICommandExecutable> getSubCommands() {
        return Set.of(new TagCreateSubCommand(), new TagDeleteSubCommand(), new TagListSubCommand());
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Tag command", "tag", false);
    }

    @Override
    public String description() {
        return "Tag command";
    }

}
