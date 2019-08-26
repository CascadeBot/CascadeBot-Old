/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.informational;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
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
        Table.TableBuilder builder = new Table.TableBuilder(context.i18n("commands.roles.header_id"),
                context.i18n("commands.roles.header_name"),
                context.i18n("commands.roles.header_members"),
                context.i18n("commands.roles.header_color"));

        for (Role role : context.getGuild().getRoles()) {
            if (role.getName().equals("@everyone")) {
                continue;
            }
            builder.addRow(role.getId(), role.getName(), String.valueOf(context.getGuild()
                            .getMembers()
                            .stream()
                            .filter(member -> member.getRoles().contains(role))
                            .count()),
                    role.getColor() == null ? context.i18n("words.default") : "#" + Integer.toHexString(role.getColor().getRGB()));
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
        return CascadePermission.of("roles", false);
    }

}
