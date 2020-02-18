/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.messaging.MessageType;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.buttons.Button;
import org.cascadebot.cascadebot.utils.buttons.ButtonGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class QueueMoveSubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length >= 2) {
            if (!context.isArgInteger(0) || !context.isArgInteger(1)) {
                context.getTypedMessaging().replyDanger("Must use numbers to move tracks!");
                return;
            }

            int track = context.getArgAsInteger(0) - 1;
            int pos = context.getArgAsInteger(1) - 1;

            if (track < 0 || track >= context.getMusicPlayer().getQueue().size()) {
                context.getTypedMessaging().replyDanger("Cannot find track number " + (track + 1));
                return;
            }

            if (pos < 0) {
                context.getTypedMessaging().replyDanger("Cannot move a track to a number less then 0");
                return;
            }

            if (track == pos) {
                context.getTypedMessaging().replyDanger("Cannot move track to its own position!");
                return;
            }

            context.getMusicPlayer().moveTrack(track, pos);
            context.getTypedMessaging().replySuccess("Moved track at position " + (track + 1) + " to position " + (pos + 1));
        } else {
            AtomicInteger selected = new AtomicInteger(-1);
            if (context.getArgs().length == 1) {
                if (context.isArgInteger(0)) {
                    int posStart = context.getArgAsInteger(0) - 1;
                    if (posStart > 0 && posStart < context.getMusicPlayer().getQueue().size() - 1) {
                        selected.set(posStart);
                    }
                }
            }
            AtomicBoolean movingTrack = new AtomicBoolean(selected.get() > -1);
            AtomicInteger start = new AtomicInteger(selected.get());
            if (!movingTrack.get()) {
                selected.set(0);
            }

            List<AudioTrack> audioTracks = new ArrayList<>(context.getMusicPlayer().getQueue());
            ButtonGroup group = new ButtonGroup(context.getMember().getIdLong(), context.getChannel().getIdLong(), context.getGuild().getIdLong());
            //TODO I should probably move this code to a util in case this need to be used else where
            group.addButton(new Button.UnicodeButton(UnicodeConstants.ARROW_UP, (runner, channel, message) -> {
                if (runner.getIdLong() != context.getMember().getIdLong()) {
                    return;
                }
                if (context.getMusicPlayer().getQueue().size() < audioTracks.size()) {
                    int diff = context.getMusicPlayer().getQueue().size() - audioTracks.size();
                    if (start.get() > -1) {
                        start.addAndGet(-diff);
                        if (start.get() < -1) start.set(-1);
                    }
                    selected.addAndGet(-diff);
                    if (selected.get() < 0) {
                        selected.set(0);
                    }
                }
                audioTracks.clear();
                audioTracks.addAll(context.getMusicPlayer().getQueue());
                if (selected.get() == 0) {
                    return;
                }
                int current = selected.getAndAdd(-1);
                if (movingTrack.get()) {
                    int newPos = selected.get();
                    AudioTrack track = audioTracks.remove(current);
                    audioTracks.add(newPos, track);
                }
                message.editMessage(getMoveEmbed(audioTracks, selected.get(), movingTrack.get()).build()).override(true).queue();
            }));
            group.addButton(new Button.UnicodeButton(UnicodeConstants.ARROW_DOWN, (runner, channel, message) -> {
                if (runner.getIdLong() != context.getMember().getIdLong()) {
                    return;
                }
                if (context.getMusicPlayer().getQueue().size() < audioTracks.size()) {
                    int diff = context.getMusicPlayer().getQueue().size() - audioTracks.size();
                    if (start.get() > -1) {
                        start.addAndGet(-diff);
                        if (start.get() < -1) start.set(-1);
                    }
                    selected.addAndGet(-diff);
                    if (selected.get() < 0) {
                        selected.set(0);
                    }
                }
                audioTracks.clear();
                audioTracks.addAll(context.getMusicPlayer().getQueue());
                if (selected.get() >= audioTracks.size() - 1) {
                    return;
                }
                int current = selected.getAndAdd(1);
                if (movingTrack.get()) {
                    int newPos = selected.get();
                    AudioTrack track = audioTracks.remove(current);
                    audioTracks.add(newPos, track);
                }
                message.editMessage(getMoveEmbed(audioTracks, selected.get(), movingTrack.get()).build()).override(true).queue();
            }));
            group.addButton(new Button.UnicodeButton(UnicodeConstants.TICK, (runner, channel, message) -> {
                if (runner.getIdLong() != context.getMember().getIdLong()) {
                    return;
                }
                if (context.getMusicPlayer().getQueue().size() < audioTracks.size()) {
                    int diff = context.getMusicPlayer().getQueue().size() - audioTracks.size();
                    if (start.get() > -1) {
                        start.addAndGet(-diff);
                        if (start.get() < -1) start.set(-1);
                    }
                    selected.addAndGet(-diff);
                    if (selected.get() < 0) {
                        selected.set(0);
                    }
                }
                audioTracks.clear();
                audioTracks.addAll(context.getMusicPlayer().getQueue());

                if (movingTrack.get()) {
                    movingTrack.set(false);
                    int trackPos = start.getAndSet(-1);
                    context.getMusicPlayer().moveTrack(trackPos, selected.get());
                } else {
                    movingTrack.set(true);
                }
                message.editMessage(getMoveEmbed(audioTracks, selected.get(), movingTrack.get()).build()).override(true).queue();
            }));
            group.addButton(new Button.UnicodeButton(UnicodeConstants.RED_CROSS, (runner, channel, message) -> {
                if (runner.getIdLong() != context.getMember().getIdLong()) {
                    return;
                }
                if (context.getMusicPlayer().getQueue().size() < audioTracks.size()) {
                    int diff = context.getMusicPlayer().getQueue().size() - audioTracks.size();
                    if (start.get() > -1) {
                        start.addAndGet(-diff);
                        if (start.get() < -1) start.set(-1);
                    }
                    selected.addAndGet(-diff);
                    if (selected.get() < 0) {
                        selected.set(0);
                    }
                }
                audioTracks.clear();
                audioTracks.addAll(context.getMusicPlayer().getQueue());

                if (movingTrack.get()) {
                    movingTrack.set(false);
                    message.editMessage(getMoveEmbed(audioTracks, selected.get(), movingTrack.get()).build()).override(true).queue();
                } else {
                    message.delete().queue();
                }
            }));
            context.getUIMessaging().sendButtonedMessage(getMoveEmbed(audioTracks, selected.get(), movingTrack.get()).build(), group);
        }
    }

    public EmbedBuilder getMoveEmbed(List<AudioTrack> tracks, int selected, boolean isMoving) {
        int currentPage = selected / 10 + 1;
        int start = currentPage * 10 - 10;
        int end = start + 9;

        StringBuilder pageBuilder = new StringBuilder();

        for (int i = start; i <= end; i++) {
            if (i >= tracks.size()) {
                break;
            }
            AudioTrack item = tracks.get(i);
            if (i == selected) {
                pageBuilder.append(UnicodeConstants.SMALL_ORANGE_DIAMOND).append(" ");
            } else {
                pageBuilder.append(UnicodeConstants.WHITE_SMALL_SQUARE).append(" ");
            }
            pageBuilder.append(i + 1).append(": ");
            if (i == selected && isMoving) {
                pageBuilder.append("**");
            }
            pageBuilder.append(item.getInfo().title).append('\n');
            if (i == selected && isMoving) {
                pageBuilder.append("**");
            }
        }
        EmbedBuilder builder = MessagingObjects.getMessageTypeEmbedBuilder(MessageType.INFO);
        builder.setTitle("Queue Move");
        builder.appendDescription(pageBuilder.toString());
        return builder;
    }

    @Override
    public String command() {
        return "move";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("queue.move", false);
    }

    @Override
    public String parent() {
        return "move";
    }

}
