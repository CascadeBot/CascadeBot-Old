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

import javax.persistence.Query;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class GroupPermissionMoveSubCommand extends SubCommand {

    private CascadeButton selectButton;
    private CascadeButton confirmButton;
    private CascadeButton cancelButton;

    @Override
    public void onCommand(Member sender, CommandContext context) { // TODO add a confirm button, primary to the left
        GuildSettingsManagementEntity management = context.getDataObject(GuildSettingsManagementEntity.class);
        if (management.getPermissionMode() == PermissionMode.MOST_RESTRICTIVE) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.groupperms.move.wrong_mode")); //TODO provide docs link
            return;
        }

        AtomicReference<GuildPermissionGroupEntity> target = new AtomicReference<>();

        if (context.getArgs().length >= 1) {
            GuildPermissionGroupEntity group = context.getDataObject(GuildPermissionGroupEntity.class, new GuildPermissionGroupId(context.getArg(0), context.getGuildId()));

            target.set(group);

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

        AtomicReference<List<GuildPermissionGroupEntity>> baseList = new AtomicReference<>(groupEntities);
        AtomicReference<List<GuildPermissionGroupEntity>> movingList = new AtomicReference<>(groupEntities);

        AtomicInteger currentPos = new AtomicInteger();

        CascadeActionRow actionRow = new CascadeActionRow();

        selectButton = CascadeButton.success("Select", Emoji.fromUnicode(UnicodeConstants.TICK), (runner, channel, message) -> {
            if (runner.getIdLong() != context.getUser().getIdLong()) {
                return;
            }
            target.set(groupEntities.get(currentPos.get()));
            movingList.set(baseList.get());
            actionRow.setComponent(0, confirmButton);
            actionRow.setComponent(1, cancelButton);
            message.editMessage(getGroupMessage(target.get() != null, movingList.get(), currentPos.get(), target.get() == null ? 0 : target.get().getPosition()));
        });

        confirmButton = CascadeButton.success("Move", Emoji.fromUnicode(UnicodeConstants.TICK), (runner, channel, message) -> {
            if (runner.getIdLong() != context.getUser().getIdLong()) {
                return;
            }
            if (!moveGroup(context, target.get(), currentPos.get())) {
                message.deleteMessage().queue();
                context.getTypedMessaging().replyDanger(context.i18n("commands.groupperms.move.failed", target.get().getName()));
                return;
            }
            // We get a new list here in case any groups where added.
            List<GuildPermissionGroupEntity> groupEntitiesNew = context.transaction(session -> {
                return DatabaseUtilsKt.listOf(session, GuildPermissionGroupEntity.class, "guild_id", context.getGuildId());
            });
            baseList.set(groupEntitiesNew);
            movingList.set(groupEntitiesNew);
            target.set(null);
            actionRow.setComponent(0, selectButton);
            actionRow.deleteComponent(1);
            message.editMessage(getGroupMessage(target.get() != null, movingList.get(), currentPos.get(), target.get() == null ? 0 : target.get().getPosition()));
        });

        cancelButton = CascadeButton.danger("Cancel", Emoji.fromUnicode(UnicodeConstants.RED_CROSS), (runner, channel, message) -> {
            if (runner.getIdLong() != context.getUser().getIdLong()) {
                return;
            }
            target.set(null);
            movingList.set(baseList.get());
            actionRow.setComponent(0, selectButton);
            actionRow.deleteComponent(1);
            message.editMessage(getGroupMessage(target.get() != null, movingList.get(), currentPos.get(), target.get() == null ? 0 : target.get().getPosition()));
        });

        ComponentContainer container = new ComponentContainer();
        if (target.get() == null) {
            actionRow.addComponent(selectButton);
        } else {
            actionRow.addComponent(confirmButton);
            actionRow.addComponent(cancelButton);
        }

        actionRow.addComponent(CascadeButton.primary(Emoji.fromUnicode(UnicodeConstants.ARROW_UP), (runner, channel, message) -> {
            if (runner.getIdLong() != context.getUser().getIdLong()) {
                return;
            }
            if (currentPos.get() == 0) {
                return;
            }
            currentPos.getAndDecrement();
            message.editMessage(getGroupMessage(target.get() != null, movingList.get(), currentPos.get(), target.get() == null ? 0 : target.get().getPosition()));
        }));

        actionRow.addComponent(CascadeButton.primary(Emoji.fromUnicode(UnicodeConstants.ARROW_DOWN), (runner, channel, message) -> {
            if (runner.getIdLong() != context.getUser().getIdLong()) {
                return;
            }
            if (currentPos.get() >= baseList.get().size()) {
                return;
            }
            currentPos.getAndIncrement();
            message.editMessage(getGroupMessage(target.get() != null, movingList.get(), currentPos.get(), target.get() == null ? 0 : target.get().getPosition()));
        }));
        container.addRow(actionRow);
    }

    private String getGroupMessage(boolean moving, List<GuildPermissionGroupEntity> entities, int currentPos, int targetPos) {
        int show = 10;
        int min = Math.max(0, currentPos - (show / 2));
        StringBuilder stringBuilder = new StringBuilder();
        if (moving) {
            for (int i = (currentPos - 1); i >= min; i--) {
                show--;
                // Filled diamond || Hollow Diamond
                stringBuilder.append(i == targetPos ? "\u25c6" : "\u25c7");

                stringBuilder.append(" ").append(entities.get(i).getName());
            }
            // Black filled arrow
            stringBuilder.append("\u27a1 ").append(entities.get(currentPos).getName());
            int max = Math.min(entities.size() - 1, currentPos + 1 + show);
            for (int i = (currentPos + 1); i <= max; i++) {
                // Filled diamond || Hollow Diamond
                stringBuilder.append(i == targetPos ? "\u25c6" : "\u25c7");

                stringBuilder.append(" ").append(entities.get(i).getName());
            }
        } else {
            for (int i = (currentPos - 1); i >= min; i--) {
                show--;
                // Hollow diamond
                stringBuilder.append("\u25c7").append(" ").append(entities.get(i).getName());
            }
            // Rightwards hollow arrow
            stringBuilder.append("\u21e8 ").append(entities.get(currentPos).getName());
            int max = Math.min(entities.size() - 1, currentPos + 1 + show);
            for (int i = (currentPos + 1); i <= max; i++) {
                // Hollow diamond
                stringBuilder.append("\u25c7").append(" ").append(entities.get(i).getName());
            }
        }
        return stringBuilder.toString();
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
