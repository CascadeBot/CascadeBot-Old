/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 *  Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.music;

import com.sedmelluq.discord.lavaplayer.filter.equalizer.Equalizer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import org.apache.commons.lang3.StringUtils;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.MainCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.music.CascadeLavalinkPlayer;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.interactions.CascadeActionRow;
import org.cascadebot.cascadebot.utils.interactions.CascadeButton;
import org.cascadebot.cascadebot.utils.interactions.CascadeSelectBox;
import org.cascadebot.cascadebot.utils.interactions.ComponentContainer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class EqualizerCommand extends MainCommand {

    private CascadeSelectBox selectBox;

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (!CascadeBot.INS.getMusicHandler().getLavalinkEnabled()) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.equalizer.not_lavalink"));
            return;
        }
        if (!(context.getMusicPlayer() instanceof CascadeLavalinkPlayer)) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.equalizer.not_lavalink"));
            return;
        }
        CascadeLavalinkPlayer player = (CascadeLavalinkPlayer) context.getMusicPlayer();
        List<Integer> currentBands = new ArrayList<>();
        currentBands.add(0);
        ComponentContainer container = new ComponentContainer();
        CascadeActionRow row = new CascadeActionRow();
        row.addComponent(new CascadeButton(ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.BACKWARD_ARROW), (runner, channel, message) -> {
            if (runner.getIdLong() != sender.getIdLong()) {
                return;
            }
            int newBand = currentBands.get(0) - 1;
            if (newBand < 0) {
                newBand = 0;
            }

            currentBands.clear();
            currentBands.add(newBand);

            message.editMessage(getEqualizerEmbed(player.getCurrentBands(), currentBands, runner.getUser(), context).build()).queue();
        }));
        row.addComponent(new CascadeButton(ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.VOLUME_DOWN), (runner, channel, message) -> {
            if (runner.getIdLong() != sender.getIdLong()) {
                return;
            }

            for (int currentBand : currentBands) {
                int gain = (int) (player.getCurrentBands().get(currentBand) * 20);
                gain -= 1;
                if (gain < -5) {
                    continue;
                }

                player.setBand(currentBand, ((float) gain) / 20f);

                if (context.getData().getMusic().getPreserveEqualizer()) {
                    context.getData().getMusic().getEqualizerBands().replace(currentBand, ((float) gain) / 20f);
                }
            }

            message.editMessage(getEqualizerEmbed(player.getCurrentBands(), currentBands, runner.getUser(), context).build()).queue();
        }));
        row.addComponent(new CascadeButton(ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.VOLUME_UP), (runner, channel, message) -> {
            if (runner.getIdLong() != sender.getIdLong()) {
                return;
            }

            for (int currentBand : currentBands) {
                int gain = (int) (player.getCurrentBands().get(currentBand) * 20);
                gain += 1;
                if (gain > 5) {
                    continue;
                }

                player.setBand(currentBand, ((float) gain) / 20f);

                if (context.getData().getMusic().getPreserveEqualizer()) {
                    context.getData().getMusic().getEqualizerBands().replace(currentBand, ((float) gain) / 20f);
                }
            }

            message.editMessage(getEqualizerEmbed(player.getCurrentBands(), currentBands, runner.getUser(), context).build()).queue();
        }));
        row.addComponent(new CascadeButton(ButtonStyle.SECONDARY, Emoji.fromUnicode(UnicodeConstants.FORWARD_ARROW), (runner, channel, message) -> {
            if (runner.getIdLong() != sender.getIdLong()) {
                return;
            }
            int newBand = currentBands.get(currentBands.size() - 1) + 1;
            if (newBand >= Equalizer.BAND_COUNT) {
                newBand = Equalizer.BAND_COUNT;
            }

            currentBands.clear();
            currentBands.add(newBand);

            message.editMessage(getEqualizerEmbed(player.getCurrentBands(), currentBands, runner.getUser(), context).build()).queue();
        }));
        container.addRow(row);
        CascadeActionRow selectRow = new CascadeActionRow();
        selectBox = new CascadeSelectBox("select-equalizer", (runner, channel, message, selected) -> {
            List<Integer> selectedBands = selected.stream().map(s -> Integer.parseInt(s.split(" ")[1]) - 1).collect(Collectors.toList());
            currentBands.clear();
            Collections.sort(selectedBands);
            currentBands.addAll(selectedBands);
            message.editMessage(getEqualizerEmbed(player.getCurrentBands(), currentBands, runner.getUser(), context).build()).queue();
        });
        for (int i = 1; i <= Equalizer.BAND_COUNT; i++) {
            selectBox.addOption("Band " + i, false);
        }
        selectBox.setMaxSelect(7); // limit to 7 as that's all we can really display.
        selectRow.addComponent(selectBox);
        container.addRow(selectRow);
        context.getUiMessaging().sendComponentMessage(getEqualizerEmbed(player.getCurrentBands(), currentBands, context.getUser(), context).build(), container);
    }

    private String getEqualizerString(Map<Integer, Float> bands, List<Integer> currentBands) {
        Set<Integer> bandsToDisplay = new HashSet<>();
        if (currentBands.size() == 1) {
            int lowestBand = currentBands.get(0) - 3;
            int highestBand = currentBands.get(0) + 2;

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
            for (int i = lowestBandDisplay; i <= highestBandDisplay + 1; i++) {
                bandsToDisplay.add(i);
            }
        } else if (currentBands.size() > 1 && currentBands.size() <= 3) {
            // Get bands immediately to the left/right of selected bands
            for (int selectedBand : currentBands) {
                if (selectedBand == 0) {
                    bandsToDisplay.add(selectedBand);
                    bandsToDisplay.add(selectedBand + 1);
                } else if (selectedBand == Equalizer.BAND_COUNT - 1) {
                    bandsToDisplay.add(selectedBand - 1);
                    bandsToDisplay.add(selectedBand);
                } else {
                    bandsToDisplay.add(selectedBand - 1);
                    bandsToDisplay.add(selectedBand);
                    bandsToDisplay.add(selectedBand + 1);
                }
            }
        } else {
            // Display all selected bands
            bandsToDisplay.addAll(currentBands);
        }

        List<Integer> heights = new ArrayList<>();

        List<String> footer = new ArrayList<>();
        int currentBarNumber = 0;
        List<Integer> selectedBarNumbers = new ArrayList<>();
        for (int toDisplay: bandsToDisplay) {
            if (currentBands.contains(toDisplay)) {
                if (toDisplay < 9) {
                    footer.add("[0" + (toDisplay + 1) + "]");
                } else {
                    footer.add("[" + (toDisplay + 1) + "]");
                }
                selectedBarNumbers.add(currentBarNumber);
            } else {
                if (toDisplay < 9) {
                    footer.add("(0" + (toDisplay + 1) + ")");
                } else {
                    footer.add("(" + (toDisplay + 1) + ")");
                }
            }

            int bandValue = (int) (bands.get(toDisplay) * 20 + 5);
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
                    if (selectedBarNumbers.contains(currentBar)) {
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

    public EmbedBuilder getEqualizerEmbed(Map<Integer, Float> bands, List<Integer> currentBands, User requester, CommandContext context) {
        selectBox.clearDefaults();
        for (int selected: currentBands) {
            selectBox.addDefault("Band " + (selected + 1));
        }

        String equalizer = getEqualizerString(bands, currentBands);

        EmbedBuilder builder = MessagingObjects.getClearThreadLocalEmbedBuilder(requester, context.getLocale());
        float total = 0;
        for (int band: currentBands) {
            total += bands.get(band);
        }
        float adv = total / currentBands.size();
        if (adv >= .2) {
            builder.setColor(Color.decode("#EE6767")); // Red
        } else if (adv > 0 && adv < .2) {
            builder.setColor(Color.decode("#D9E94A")); // Yellow
        } else {
            builder.setColor(Color.decode("#84D6A2")); // Green
        }

        builder.setTitle(context.i18n("commands.equalizer.embed_title", Equalizer.BAND_COUNT));

        builder.setDescription("```" + equalizer + "```");
        return builder;
    }

    @Override
    public Module module() {
        return Module.MUSIC;
    }

    @Override
    public String command() {
        return "equalizer";
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("equalizer", true);
    }

    @Override
    public Set<SubCommand> subCommands() {
        return Set.of(new EqualizerResetSubCommand());
    }
}
