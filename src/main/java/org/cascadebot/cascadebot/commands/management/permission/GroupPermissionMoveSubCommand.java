/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.permission;

import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.data.entities.GuildPermissionGroupEntity;
import org.cascadebot.cascadebot.data.entities.GuildPermissionGroupId;
import org.cascadebot.cascadebot.data.entities.GuildSettingsManagementEntity;
import org.cascadebot.cascadebot.data.objects.PermissionMode;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.DatabaseUtilsKt;
import org.cascadebot.cascadebot.utils.interactions.CascadeActionRow;
import org.cascadebot.cascadebot.utils.interactions.CascadeButton;
import org.cascadebot.cascadebot.utils.interactions.ComponentContainer;
import org.cascadebot.cascadebot.utils.move.MovableList;

import javax.persistence.Query;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class GroupPermissionMoveSubCommand extends SubCommand {

    private CascadeButton selectButton;
    private CascadeButton confirmButton;
    private CascadeButton cancelButton;

    private final int SHOW_MOVE_ELEMENTS = 10;

    @Override
    public void onCommand(Member sender, CommandContext context) { // TODO add a confirm button, primary to the left
        GuildSettingsManagementEntity management = context.getDataObject(GuildSettingsManagementEntity.class);
        if (management.getPermissionMode() == PermissionMode.MOST_RESTRICTIVE) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.groupperms.move.wrong_mode")); //TODO provide docs link
            return;
        }

        GuildPermissionGroupEntity target = null;

        if (context.getArgs().length >= 1) {
            GuildPermissionGroupEntity group = context.getDataObject(GuildPermissionGroupEntity.class, new GuildPermissionGroupId(context.getArg(0), context.getGuildId()));

            target = group;

            if (context.getArgs().length > 1 && context.isArgInteger(1)) {
                if (moveGroup(context, group, context.getArgAsInteger(1))) {
                    context.getTypedMessaging().replySuccess(context.i18n("commands.groupperms.move.moved", group.getName(), context.getArg(1)));
                } else {
                    context.getTypedMessaging().replyDanger(context.i18n("commands.groupperms.move.failed", group.getName()));
                }
                return;
            }
        }

        List<GuildPermissionGroupEntity> groupEntities = context.transaction(session -> {
            return DatabaseUtilsKt.listOf(session, GuildPermissionGroupEntity.class, "guild_id", context.getGuildId());
        });

        if (groupEntities == null) {
            throw new UnsupportedOperationException("TODO"); // TODO this message
        }

        MovableList<GuildPermissionGroupEntity> movableList = MovableList.wrap(groupEntities);
        if (target != null) {
            movableList.setSelectedItem(target);
        }
        movableList.setUsageRestriction(aLong -> aLong == sender.getIdLong());
        movableList.setMovedConsumer(movedInfo -> {
            moveGroup(context, movedInfo.getMovedItem(), movedInfo.getMovedTo());
        });
        movableList.send(context.getChannel());
        // TODO add refresh button?
    }

    private boolean moveGroup(CommandContext context, GuildPermissionGroupEntity group, int pos) {
        if (group.getPosition() == pos) {
            throw new UnsupportedOperationException("Group position already matches supplied position");
        }
        boolean positive = pos - group.getPosition() >= 0;
        String sql;
        if (positive) {
            sql = "UPDATE GuildPermissionGroupEntity SET position = position - 1 WHERE position <= :pos AND position > :group";
        } else {
            sql = "UPDATE GuildPermissionGroupEntity SET position = position + 1 WHERE position >= :pos AND position < :group";
        }
        Boolean success = context.transaction(session -> {
            Query query = session.createQuery(sql);
            query.setParameter("pos", pos);
            query.setParameter("group", group.getPosition());
            int updated = query.executeUpdate();
            if (updated == Math.abs(pos - group.getPosition())) {
                group.setPosition(pos);
                session.save(group);
                return true;
            } else {
                session.getTransaction().rollback();
                return false;
            }
        });

        if (success == null) {
            return false;
        }

        return success;
    }

    @Override
    public String command() {
        return "move";
    }

    @Override
    public String parent() {
        return "groupperms";
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("permissions.group.move", false, Module.MANAGEMENT);
    }

    @Override
    public String description() {
        return null;
    }

}
