/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.music;

import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class SkipCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {

    }

    @Override
    public String command() {
        return "skip";
    }

    @Override
    public Module getModule() {
        return Module.MUSIC;
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Skip", "skip", Module.MUSIC);
    }

    @Override
    public String description() {
        return "skips the current song";
    }
}
