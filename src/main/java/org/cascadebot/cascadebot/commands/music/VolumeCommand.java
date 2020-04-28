package org.cascadebot.cascadebot.commands.music;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.data.objects.donation.Flag;
import org.cascadebot.cascadebot.messaging.MessageType;
import org.cascadebot.cascadebot.music.CascadePlayer;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.ConfirmUtils;

import java.util.concurrent.TimeUnit;

public class VolumeCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        CascadePlayer player = context.getMusicPlayer();
        if (context.getArgs().length == 0) {
            context.getTypedMessaging().replyInfo(context.i18n("commands.volume.current_volume", player.getVolume()));
            return;
        }

        int volume;
        if (context.isArgInteger(0)) {
            volume = context.getArgAsInteger(0);
        } else {
            context.getUiMessaging().replyUsage();
            return;
        }

        if (volume < 0) {
            context.getTypedMessaging().replyWarning(context.i18n("commands.volume.greater_than_zero"));
            return;
        } else if (volume > 100 && volume <= 200) {
            if (context.hasPermission("volume.extreme")) {
                ConfirmUtils.confirmAction(sender.getIdLong(),
                        "volume-extreme",
                        context.getChannel(),
                        MessageType.WARNING,
                        context.i18n("commands.volume.extreme_volume"),
                        0,
                        TimeUnit.SECONDS.toMillis(30),
                        true,
                        new ConfirmUtils.ConfirmRunnable() {
                            @Override
                            public void execute() {
                                player.setVolume(volume);
                                if (context.getData().getGuildMusic().getPreserveVolume()) {
                                    context.getData().getGuildMusic().setVolume(volume);
                                }
                                context.getTypedMessaging().replyInfo(context.i18n("commands.volume.volume_set", player.getVolume()));
                            }
                        });
                return;
            } else {
                context.getUiMessaging().sendPermissionError("volume.extreme");
                return;
            }
        } else if (volume > 200) {
            context.getTypedMessaging().replyWarning(context.i18n("commands.volume.volume_range"));
            return;
        }

        if (volume == context.getMusicPlayer().getVolume()) {
            context.getTypedMessaging().replyInfo(context.i18n("commands.volume.volume_already_set", player.getVolume()));
        } else {
            player.setVolume(volume);
            if (context.getData().getGuildMusic().getPreserveVolume()) {
                context.getData().getGuildMusic().setVolume(volume);
            }
            context.getTypedMessaging().replyInfo(context.i18n("commands.volume.volume_set", player.getVolume()));
        }

    }

    @Override
    public Module getModule() {
        return Module.MUSIC;
    }

    @Override
    public String command() {
        return "volume";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("volume", false);
    }

    @Override
    public String getRequiredFlag() {
        return "music_controls";
    }
}
