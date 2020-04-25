/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 *  Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.music;

import com.sedmelluq.discord.lavaplayer.filter.equalizer.Equalizer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.StringUtils;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.music.CascadeLavalinkPlayer;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.buttons.Button;
import org.cascadebot.cascadebot.utils.buttons.ButtonGroup;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class EqualizerCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (!CascadeBot.INS.getMusicHandler().isLavalinkEnabled()) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.equalizer.not_lavalink"));
            return;
        }
        if (!(context.getMusicPlayer() instanceof CascadeLavalinkPlayer)) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.equalizer.not_lavalink"));
            return;
        }
        CascadeLavalinkPlayer player = (CascadeLavalinkPlayer) context.getMusicPlayer();
        AtomicInteger currentBand = new AtomicInteger();
        ButtonGroup buttonGroup = new ButtonGroup(context.getUser().getIdLong(), context.getChannel().getIdLong(), context.getGuild().getIdLong());
        buttonGroup.addButton(new Button.UnicodeButton(UnicodeConstants.BACKWARD_ARROW, (runner, channel, message) -> {
            if (runner.getIdLong() != buttonGroup.getOwnerId()) {
                return;
            }
            int newBand = currentBand.decrementAndGet();
            if (newBand < 0) {
                return;
            }

            currentBand.set(newBand);

            message.editMessage(getEqualizerEmbed(player.getCurrentBands(), currentBand.get(), runner.getUser(), context).build()).override(true).queue();
        }));
        buttonGroup.addButton(new Button.UnicodeButton(UnicodeConstants.FORWARD_ARROW, (runner, channel, message) -> {
            if (runner.getIdLong() != buttonGroup.getOwnerId()) {
                return;
            }
            int newBand = currentBand.incrementAndGet();
            if (newBand >= Equalizer.BAND_COUNT) {
                return;
            }

            currentBand.set(newBand);

            message.editMessage(getEqualizerEmbed(player.getCurrentBands(), currentBand.get(), runner.getUser(), context).build()).override(true).queue();
        }));
        buttonGroup.addButton(new Button.UnicodeButton(UnicodeConstants.VOLUME_DOWN, (runner, channel, message) -> {
            if (runner.getIdLong() != buttonGroup.getOwnerId()) {
                return;
            }

            int gain = (int) (player.getCurrentBands().get(currentBand.get()) * 20);
            gain -= 1;
            if (gain < -5) {
                return;
            }

            player.setBand(currentBand.get(), ((float) gain) / 20f);
            if (context.getData().getGuildMusic().isPreserveEqualizer()) {
                context.getData().getGuildMusic().getEqualizerBands().replace(currentBand.get(), ((float) gain) / 20f);
            }

            message.editMessage(getEqualizerEmbed(player.getCurrentBands(), currentBand.get(), runner.getUser(), context).build()).override(true).queue();
        }));
        buttonGroup.addButton(new Button.UnicodeButton(UnicodeConstants.VOLUME_UP, (runner, channel, message) -> {
            if (runner.getIdLong() != buttonGroup.getOwnerId()) {
                return;
            }

            int gain = (int) (player.getCurrentBands().get(currentBand.get()) * 20);
            gain += 1;
            if (gain > 5) {
                return;
            }

            player.setBand(currentBand.get(), ((float) gain) / 20f);
            if (context.getData().getGuildMusic().isPreserveEqualizer()) {
                context.getData().getGuildMusic().getEqualizerBands().replace(currentBand.get(), ((float) gain) / 20f);
            }

            message.editMessage(getEqualizerEmbed(player.getCurrentBands(), currentBand.get(), runner.getUser(), context).build()).override(true).queue();
        }));
        context.getUIMessaging().sendButtonedMessage(getEqualizerEmbed(player.getCurrentBands(), currentBand.get(), context.getUser(), context).build(), buttonGroup);
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
                if (i < 9) {
                    footer.add("[0" + (i + 1) + "]");
                } else {
                    footer.add("[" + (i + 1) + "]");
                }
                selectedBarNumber = currentBarNumber;
            } else {
                if (i < 9) {
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
                    char barChar = UnicodeConstants.DOTTED_SQUARE;
                    if (currentBar == selectedBarNumber) {
                        barChar = UnicodeConstants.SQUARE;
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

    public EmbedBuilder getEqualizerEmbed(Map<Integer, Float> bands, int currentBand, User requester, CommandContext context) {
        String equalizer = getEqualizerString(bands, currentBand);

        EmbedBuilder builder = MessagingObjects.getClearThreadLocalEmbedBuilder(requester);
        if (bands.get(currentBand) >= .2) {
            builder.setColor(Color.decode("#EE6767")); // Red
        } else if (bands.get(currentBand) > 0 && bands.get(currentBand) < .2) {
            builder.setColor(Color.decode("#D9E94A")); // Yellow
        } else {
            builder.setColor(Color.decode("#84D6A2")); // Green
        }

        builder.setTitle(context.i18n("commands.equalizer.embed_title", Equalizer.BAND_COUNT));

        builder.setDescription("```" + equalizer + "```");
        return builder;
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

    @Override
    public Set<ISubCommand> getSubCommands() {
        return Set.of(new EqualizerResetSubCommand());
    }
}
