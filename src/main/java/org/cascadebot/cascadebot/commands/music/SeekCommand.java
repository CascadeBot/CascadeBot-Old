package org.cascadebot.cascadebot.commands.music;

import com.ibm.icu.text.MeasureFormat;
import com.ibm.icu.util.Measure;
import com.ibm.icu.util.ULocale;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.ParserUtils;
import org.cascadebot.shared.Regex;

import java.util.Arrays;
import java.util.regex.Pattern;

public class SeekCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        String time = context.getMessage(0);

        if (context.getArgs().length == 0) {
            context.getUIMessaging().replyUsage();
            return;
        }

        long millis;

        if (context.getMusicPlayer().getPlayer().getPlayingTrack() == null) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.seek.no_music_playing"));
            return;
        }

        if (context.isArgInteger(0)) {
            millis = Long.parseLong(time);
        } else {
            millis = ParserUtils.parseTime(time, true);
        }

        if (millis < 0) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.seek.negative_value"));
            return;
        } else if (millis > context.getMusicPlayer().getPlayer().getPlayingTrack().getDuration()) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.seek.duration_shorter"));
            return;
        }

        context.getMusicPlayer().getPlayer().seekTo(millis);
    }

    @Override
    public Module getModule() {
        return Module.MUSIC;
    }

    @Override
    public String command() {
        return "seek";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("seek", true);
    }

}
