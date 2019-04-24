/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.informational;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.Table;
import org.cascadebot.cascadebot.utils.pagination.PageUtils;

import java.util.Set;

public class RolesCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        Table.TableBuilder builder = new Table.TableBuilder("Role ID", "Role Name");

        for (Role role : context.getGuild().getRoles()) {
            builder.addRow(role.getId(), role.getName());
        }

        context.getUIMessaging().sendPagedMessage(PageUtils.splitTableDataToPages(builder.build(), 20));
    }

    @Override
    public String command() {
        return "roles";
    }

    @Override
    public Module getModule() {
        return Module.INFORMATIONAL;
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Roles command", "roles", false);
    }

    @Override
    public Set<String> getGlobalAliases() {
        return Set.of("roleinfo");
    }

}
