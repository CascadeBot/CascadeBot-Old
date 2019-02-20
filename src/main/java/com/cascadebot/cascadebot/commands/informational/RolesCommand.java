/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.commands.informational;

import com.cascadebot.cascadebot.commandmeta.CommandContext;
import com.cascadebot.cascadebot.commandmeta.ICommandMain;
import com.cascadebot.cascadebot.commandmeta.Module;
import com.cascadebot.cascadebot.permissions.CascadePermission;
import com.cascadebot.cascadebot.utils.Table;
import com.cascadebot.cascadebot.utils.pagination.PageUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

import java.util.Set;

public class RolesCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        Table.TableBuilder builder = new Table.TableBuilder("Role ID", "Role Name");

        for (Role role : context.getGuild().getRoles()) {
            builder.addRow(role.getId(), role.getName());
        }

        context.sendPagedMessage(PageUtils.splitTableDataToPages(builder, 20));
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