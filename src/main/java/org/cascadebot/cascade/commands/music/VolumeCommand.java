package org.cascadebot.cascade.commands.music;

import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascade.commandmeta.Argument;
import org.cascadebot.cascade.commandmeta.ArgumentType;
import org.cascadebot.cascade.commandmeta.CommandContext;
import org.cascadebot.cascade.commandmeta.ICommandMain;
import org.cascadebot.cascade.commandmeta.Module;
import org.cascadebot.cascade.data.objects.Flag;
import org.cascadebot.cascade.messaging.MessageType;
import org.cascadebot.cascade.music.CascadePlayer;
import org.cascadebot.cascade.permissions.CascadePermission;
import org.cascadebot.cascade.utils.ConfirmUtils;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class VolumeCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        CascadePlayer player = context.getMusicPlayer();
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
            context.getTypedMessaging().replyWarning("Volume needs to be greater than 0%");
            return;
        } else if (volume > 100 && volume <= 200) {
            if (context.hasPermission("volume.extreme")) {
                ConfirmUtils.confirmAction(sender.getUser().getIdLong(),
                        "volume-extreme",
                        context.getChannel(),
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
                return;
            } else {
                context.getUIMessaging().sendPermissionError("volume.extreme");
                return;
            }
        } else if (volume > 200) {
            context.getTypedMessaging().replyWarning("Volume needs to be between 0% and 200%");
            return;
        }

        if (volume == context.getMusicPlayer().getPlayer().getVolume()) {
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
    public Set<Argument> getUndefinedArguments() {
        return Set.of(Argument.of("volume", "Sets the volume to this value", ArgumentType.OPTIONAL));
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Volume", "volume", getModule());
    }

    @Override
    public String description() {
        return "Returns the current volume";
    }

    @Override
    public Set<Flag> getFlags() {
        return Set.of(Flag.MUSIC_SERVICES);
    }

}
