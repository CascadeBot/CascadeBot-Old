package org.cascadebot.cascadebot.commands.management.permission;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.data.entities.GuildPermissionGroupEntity;
import org.cascadebot.cascadebot.data.entities.GuildPermissionGroupId;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.DatabaseUtilsKt;

import java.util.List;

public class GroupPermissionDeleteSubCommand extends SubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getUiMessaging().replyUsage();
            return;
        }

        GuildPermissionGroupEntity group = context.getDataObject(GuildPermissionGroupEntity.class, new GuildPermissionGroupId(context.getArg(0), context.getGuildId()));

        if (group == null) {
            context.getTypedMessaging().replyWarning(context.i18n("commands.groupperms.no_group", context.getArg(0)));
            return;
        }

        int pos = group.getPosition();

        context.transactionNoReturn(session -> {
            session.delete(group);
        });

        context.getTypedMessaging().replySuccess(context.i18n("commands.groupperms.delete.success", context.getArg(0)));

        List<GuildPermissionGroupEntity> groupEntities = context.transaction(session -> { // TODO get all groups with position bigger then the delete one instead of ALL groups
            return DatabaseUtilsKt.listOf(session, GuildPermissionGroupEntity.class, "guild_id", context.getGuildId());
        });
        
        if (groupEntities == null) {
            throw new UnsupportedOperationException("Group entities returned null in group delete. This shouldn't happen!");
        }

        for (GuildPermissionGroupEntity entity : groupEntities) { // move everything higher than the deleted group down one
            if (entity.getPosition() > pos) {
                entity.setPosition(entity.getPosition() - 1);
                context.saveDataObject(entity);
            }
        }
    }

    @Override
    public String parent() {
        return "groupperms";
    }

    @Override
    public String command() {
        return "delete";
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("permissions.group.delete", false, Module.MANAGEMENT);
    }

}
