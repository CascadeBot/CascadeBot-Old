package org.cascadebot.cascadebot.commands.music;

import com.sedmelluq.discord.lavaplayer.filter.equalizer.Equalizer;
import net.dv8tion.jda.api.entities.Member;
import org.apache.commons.lang3.StringUtils;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.music.CascadeLavalinkPlayer;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.buttons.Button;
import org.cascadebot.cascadebot.utils.buttons.ButtonGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class EqualizerCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        CascadeLavalinkPlayer player = (CascadeLavalinkPlayer) context.getMusicPlayer();
        AtomicInteger currentBand = new AtomicInteger();
        ButtonGroup buttonGroup = new ButtonGroup(context.getUser().getIdLong(), context.getChannel().getIdLong(), context.getGuild().getIdLong());
        buttonGroup.addButton(new Button.UnicodeButton(UnicodeConstants.BACKWARD_ARROW, (runner, channel, message) -> {
            if (runner.getIdLong() != buttonGroup.getOwnerId()) {
                return;
            }
            int newBand = currentBand.get() - 1;
            if (newBand < 0) {
                return;
            }

            currentBand.set(newBand);

            message.editMessage("```" + getEqualizerString(player.getCurrentBands(), currentBand.get()) + "```").override(true).queue();
        }));
        buttonGroup.addButton(new Button.UnicodeButton(UnicodeConstants.FORWARD_ARROW, (runner, channel, message) -> {
            if (runner.getIdLong() != buttonGroup.getOwnerId()) {
                return;
            }
            int newBand = currentBand.get() + 1;
            if (newBand >= Equalizer.BAND_COUNT) {
                return;
            }

            currentBand.set(newBand);

            message.editMessage("```" + getEqualizerString(player.getCurrentBands(), currentBand.get()) + "```").override(true).queue();
        }));
        buttonGroup.addButton(new Button.UnicodeButton(UnicodeConstants.VOLUME_DOWN, (runner, channel, message) -> {
            if (runner.getIdLong() != buttonGroup.getOwnerId()) {
                return;
            }

            float gain = player.getCurrentBands().get(currentBand.get());
            gain -= 0.05f;
            if (gain < -0.25) {
                return;
            }

            player.setBand(currentBand.get(), gain);

            message.editMessage("```" + getEqualizerString(player.getCurrentBands(), currentBand.get()) + "```").override(true).queue();
        }));
        buttonGroup.addButton(new Button.UnicodeButton(UnicodeConstants.VOLUME_UP, (runner, channel, message) -> {
            if (runner.getIdLong() != buttonGroup.getOwnerId()) {
                return;
            }

            float gain = player.getCurrentBands().get(currentBand.get());
            gain += 0.05f;
            if (gain > 0.25) {
                return;
            }

            player.setBand(currentBand.get(), gain);

            message.editMessage("```" + getEqualizerString(player.getCurrentBands(), currentBand.get()) + "```").override(true).queue();
        }));
        context.getUIMessaging().sendButtonedMessage("```" + getEqualizerString(player.getCurrentBands(), currentBand.get()) + "```", buttonGroup);
    }

    private String getEqualizerString(Map<Integer, Float> bands, int currentBand) {
        int lowestBand = currentBand - 3;
        int highestBand = currentBand + 2;
        int lowestBandDisplay = lowestBand;
        int highestBandDisplay = highestBand;

        if (lowestBand < 0) {
            highestBandDisplay += Math.abs(lowestBand);
            lowestBandDisplay = 0;
        }

        if (highestBand >= Equalizer.BAND_COUNT - 1) {
            lowestBandDisplay -= highestBand - (Equalizer.BAND_COUNT - 2);
            highestBandDisplay = Equalizer.BAND_COUNT - 2;
        }

        List<Integer> heights = new ArrayList<>();

        List<String> footer = new ArrayList<>();
        int currentBarNumber = 0;
        int selectedBarNumber = 0;
        for (int i = lowestBandDisplay; i <= highestBandDisplay + 1; i++) {
            if (i == currentBand) {
                if (i < 10) {
                    footer.add("[0" + (i + 1) + "]");
                } else {
                    footer.add("[" + (i + 1) + "]");
                }
                selectedBarNumber = currentBarNumber;
            } else {
                if (i < 10) {
                    footer.add("(0" + (i + 1) + ")");
                } else {
                    footer.add("(" + (i + 1) + ")");
                }
            }

            int bandValue = (int) (bands.get(i) * 20 + 5);
            heights.add(bandValue);
            currentBarNumber++;
        }

        StringBuilder equalizerBuilder = new StringBuilder();

        List<Integer> barLocations = new ArrayList<>();
        StringBuilder footerBuilder = new StringBuilder();
        footerBuilder.append("  ");
        for (String footerPart : footer) {
            int currentLength = footerBuilder.length();
            int barPos = currentLength + (footerPart.length() / 2);
            barLocations.add(barPos);
            footerBuilder.append(footerPart).append(' ');
        }

        for (int i = 0; i < 10; i++) {
            int currentHeight = 10 - i;

            String lineString = "|" + StringUtils.repeat(" ", footerBuilder.length() - 1);
            char[] lineChars = lineString.toCharArray();

            int currentBar = 0;
            for (int barHeight : heights) {
                if (currentHeight <= barHeight) {
                    int barLocation = barLocations.get(currentBar);
                    char barChar = '░';
                    if (currentBar == selectedBarNumber) {
                        barChar = '█';
                    }
                    lineChars[barLocation] = barChar;
                }
                currentBar++;
            }

            equalizerBuilder.append(String.valueOf(lineChars)).append('\n');
        }

        equalizerBuilder.append('+').append(StringUtils.repeat("-", footerBuilder.length() - 1))
                .append('\n').append(footerBuilder.toString());

        return equalizerBuilder.toString();
    }

    @Override
    public Module getModule() {
        return Module.MUSIC;
    }

    @Override
    public String command() {
        return "equalizer";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("equalizer", true);
    }

}
