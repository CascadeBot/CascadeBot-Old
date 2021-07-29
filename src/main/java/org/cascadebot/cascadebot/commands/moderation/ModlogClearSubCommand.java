package org.cascadebot.cascadebot.commands.moderation;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.data.objects.GuildSettingsModeration;
import org.cascadebot.cascadebot.messaging.MessageType;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.ConfirmUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ModlogClearSubCommand extends SubCommand {

    String clearAction = "modlog_clear";
    String clearDisabledAction = "modlog_disabled";

    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 0) {
            if (ConfirmUtils.hasRegisteredAction(clearAction, sender.getIdLong())) {
                ConfirmUtils.confirmAction(clearAction, sender.getIdLong());
            } else {
                ConfirmUtils.registerForConfirmation(sender.getIdLong(), clearAction, context.getChannel(), MessageType.WARNING,
                        "Confirm that you want to remove ALL events", 0, TimeUnit.SECONDS.toMillis(5),
                        true, () -> {
                            context.getData().getModeration().clearModlogEvents();
                            context.getTypedMessaging().replySuccess("Removed all modlog events!");
                        });
            }
            return;
        }
        if (context.getArg(0).equalsIgnoreCase("disabled")) {
            if (ConfirmUtils.hasRegisteredAction(clearDisabledAction, sender.getIdLong())) {
                ConfirmUtils.confirmAction(clearDisabledAction, sender.getIdLong());
            } else {
                ConfirmUtils.registerForConfirmation(sender.getIdLong(), clearDisabledAction, context.getChannel(), MessageType.WARNING,
                        "Confirm that you want to remove all events in disabled channels", 0, TimeUnit.SECONDS.toMillis(5),
                        true, () -> {
                            List<String> delete = new ArrayList<>();
                            for (Map.Entry<String, GuildSettingsModeration.ChannelModlogEventsInfo> entry : context.getData().getModeration().getModlogEvents().entrySet()) {
                                TextChannel textChannel = CascadeBot.INS.getClient().getTextChannelById(entry.getKey());
                                if (textChannel == null) {
                                    continue;
                                }
                                List<Webhook> webhooks = textChannel.retrieveWebhooks().complete();
                                for (Webhook webhook : webhooks) {
                                    if (webhook.getIdLong() == entry.getValue().getWebhookId()) {
                                        delete.add(entry.getKey());
                                    }
                                }
                            }
                            context.getData().write(guildData -> {
                                for (String id : delete) {
                                    guildData.getModeration().removeModlogEvent(id);
                                }
                            });
                            context.getTypedMessaging().replySuccess("Deleted events from " + delete.size() + " disabled channel");
                        });
            }
            return;
        }
        context.getUiMessaging().replyUsage();
    }

    public String command() {
        return "clear";
    }

    public CascadePermission permission() {
        return CascadePermission.of("modlog.clear", false, Module.MODERATION);
    }

    public String parent() {
        return "modlog";
    }

}
