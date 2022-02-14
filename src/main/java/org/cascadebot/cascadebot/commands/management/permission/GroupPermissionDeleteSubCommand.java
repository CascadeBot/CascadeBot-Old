package org.cascadebot.cascadebot.commands.management.permission;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.data.entities.GuildPermissionGroupEntity;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.DatabaseUtilsKt;
import org.cascadebot.cascadebot.utils.PermissionCommandUtils;

import java.util.Map;

public class GroupPermissionDeleteSubCommand extends SubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getUiMessaging().replyUsage();
            return;
        }

        Integer lines = context.transaction(session -> {
            return DatabaseUtilsKt.deleteById(session, GuildPermissionGroupEntity.class,
                    Map.of("guild_id", context.getGuildId(), "name", context.getArg(0)));
        });

        if (lines != null && lines > 0) {
            context.getTypedMessaging().replySuccess(context.i18n("commands.groupperms.delete.success", context.getArg(0)));
        } else {
            context.getTypedMessaging().replyWarning(context.i18n("commands.groupperms.delete.failed", context.getArg(0)));
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
