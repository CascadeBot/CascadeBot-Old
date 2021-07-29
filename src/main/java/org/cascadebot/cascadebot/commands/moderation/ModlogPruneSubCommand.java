package org.cascadebot.cascadebot.commands.moderation;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.permissions.CascadePermission;

import java.util.concurrent.atomic.AtomicInteger;

public class ModlogPruneSubCommand extends SubCommand {

    public void onCommand(Member sender, CommandContext context) {
        AtomicInteger amount = new AtomicInteger(0);
        context.getData().write(guildData -> {
            for (String id : context.getData().getModeration().getModlogEvents().keySet()) {
                if (CascadeBot.INS.getShardManager().getTextChannelById(id) == null) {
                    context.getData().getModeration().removeModlogEvent(id);
                    amount.getAndIncrement();
                }
            }
        });
        context.getTypedMessaging().replySuccess("Removed all events from " + amount.get() + " deleted channels");
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
