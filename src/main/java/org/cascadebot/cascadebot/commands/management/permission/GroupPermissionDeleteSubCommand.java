package org.cascadebot.cascadebot.commands.management.permission;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commandmeta.DeprecatedSubCommand;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.PermissionCommandUtils;

public class GroupPermissionDeleteSubCommand extends DeprecatedSubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 1) {
            context.getUiMessaging().replyUsage();
            return;
        }

        PermissionCommandUtils.tryGetGroupFromString(context, context.getArg(0), group -> {
            if (context.getData().getManagement().getPermissions().deleteGroup(group.getId())) {
                // If the group existed to delete and has been successfully deleted.
                context.getTypedMessaging().replySuccess(context.i18n("commands.groupperms.delete.success", group.getName(), group.getId()));
            } else {
                // Throwing an exception here because this *should* never happen
                throw new IllegalStateException("Couldn't delete group!");
            }
        }, sender.getIdLong());
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
