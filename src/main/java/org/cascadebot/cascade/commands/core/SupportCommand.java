/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascade.commands.core;

import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascade.Constants;
import org.cascadebot.cascade.commandmeta.CommandContext;
import org.cascadebot.cascade.commandmeta.ICommandCore;

public class SupportCommand implements ICommandCore {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        context.getTypedMessaging().replyInfo("Cascade Support Server: " + Constants.serverInvite);
    }

    @Override
    public String command() {
        return "support";
    }

    @Override
    public String description() {
        return "Returns a link to the Discord support server";
    }

}
