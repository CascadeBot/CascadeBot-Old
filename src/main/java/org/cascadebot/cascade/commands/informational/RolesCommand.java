/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascade.commands.informational;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import org.cascadebot.cascade.commandmeta.CommandContext;
import org.cascadebot.cascade.commandmeta.ICommandMain;
import org.cascadebot.cascade.commandmeta.Module;
import org.cascadebot.cascade.permissions.CascadePermission;
import org.cascadebot.cascade.utils.Table;
import org.cascadebot.cascade.utils.pagination.PageUtils;

import java.util.Set;

public class RolesCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        Table.TableBuilder builder = new Table.TableBuilder("Role ID", "Name", "No. Users", "Colour");
        
        for (Role role : context.getGuild().getRoles()) {
            if (role.getName().equals("@everyone")) continue;
            builder.addRow(
                    role.getId(),
                    role.getName(),
                    String.valueOf(context.getGuild().getMembers().stream().filter(member -> member.getRoles().contains(role)).count()),
                    role.getColor() == null ?  "Default" : "#" + Integer.toHexString(role.getColor().getRGB())
            );
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
    public String description() {
        return "Returns the server's roles";
    }

    @Override
    public Set<String> getGlobalAliases() {
        return Set.of("roleinfo");
    }

}
