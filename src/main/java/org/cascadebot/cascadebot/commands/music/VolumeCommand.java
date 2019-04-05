package org.cascadebot.cascadebot.commands.music;

import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.commandmeta.Argument;
import org.cascadebot.cascadebot.commandmeta.ArgumentType;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.data.objects.Flag;
import org.cascadebot.cascadebot.messaging.MessageType;
import org.cascadebot.cascadebot.music.CascadePlayer;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.ConfirmUtils;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class VolumeCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        CascadePlayer player = context.getData().getMusicPlayer();
        if (context.getArgs().length == 0) {
            context.getTypedMessaging().replyInfo("Current volume is %d%%", player.getPlayer().getVolume());
            return;
        }

        int volume;
        if (context.isArgInteger(0)) {
            volume = context.getArgAsInteger(0);
        } else {
            context.getUIMessaging().replyUsage(this);
            return;
        }

        if (volume < 0) {
            context.getTypedMessaging().replyWarning("Volume needs to be greater than 0");
            return;
        } else if (volume > 100 && volume <= 200) {
            if (context.hasPermission("volume.extreme")) {
                ConfirmUtils.confirmAction(sender.getUser().getIdLong(),
                        "volume-extreme", context.getChannel(),
                        MessageType.WARNING,
                        "Are you sure you want to exceed 100% volume?",
                        0,
                        TimeUnit.SECONDS.toMillis(30),
                        new ConfirmUtils.ConfirmRunnable() {
                            @Override
                            public void execute() {
                                player.getPlayer().setVolume(volume);
                                context.getTypedMessaging().replyInfo("Volume set to %d%%", player.getPlayer().getVolume());
                            }
                        });
            } else {
                context.getUIMessaging().sendPermissionError("volume.extreme");
                return;
            }
        }

        if (volume == context.getData().getMusicPlayer().getPlayer().getVolume()) {
            context.getTypedMessaging().replyInfo("Volume is already %d%%", player.getPlayer().getVolume());
        } else {
            player.getPlayer().setVolume(volume);
            context.getTypedMessaging().replyInfo("Volume set to %d%%", player.getPlayer().getVolume());
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
    public Set<Argument> getArguments() {
        return Set.of(Argument.of("volume", "sets the volume to this value", ArgumentType.OPTIONAL));
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Volume", "volume", getModule());
    }

    @Override
    public String description() {
        return "Changes the players volume";
    }

    @Override
    public Set<Flag> getFlags() {
        return Set.of(Flag.MUSIC_SERVICES);
    }

}
