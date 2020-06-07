package org.cascadebot.cascadebot.commands.moderation;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class ModlogPruneSubCommand extends SubCommand {

    public void onCommand(Member sender, CommandContext context) {
        int amount = 0;
        for (long id : context.getData().getModeration().getModlogEvents().keySet()) {
            if (CascadeBot.INS.getShardManager().getTextChannelById(id) == null) {
                context.getData().getModeration().getModlogEvents().remove(id);
                amount++;
            }
        }
        context.getTypedMessaging().replySuccess("Removed all events from " + amount + " deleted channels");
    }

    public String command() {
        return "prune";
    }

    public CascadePermission permission() {
        return CascadePermission.of("modlog.prune", false, Module.MODERATION);
    }

    public String parent() {
        return "modlog";
    }

}
