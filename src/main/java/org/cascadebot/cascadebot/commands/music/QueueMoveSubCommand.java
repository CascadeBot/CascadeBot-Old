/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.music;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ISubCommand;
import org.cascadebot.cascadebot.messaging.MessageType;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.music.MovableAudioTrack;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.buttons.Button;
import org.cascadebot.cascadebot.utils.buttons.ButtonGroup;
import org.cascadebot.cascadebot.utils.move.MovableList;

import java.util.List;
import java.util.stream.Collectors;

public class QueueMoveSubCommand implements ISubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length == 2) {
            if (!context.isArgInteger(0) || !context.isArgInteger(1)) {
                context.getTypedMessaging().replyDanger(context.i18n("commands.queue.move.numbers"));
                return;
            }

            int track = context.getArgAsInteger(0) - 1;
            int pos = context.getArgAsInteger(1) - 1;

            if (track < 0 || track >= context.getMusicPlayer().getQueue().size()) {
                context.getTypedMessaging().replyDanger(context.i18n("commands.queue.track_number", track + 1));
                return;
            }

            if (pos < 0) {
                context.getTypedMessaging().replyDanger(context.i18n("commands.queue.move.zero"));
                return;
            }

            if (track == pos) {
                context.getTypedMessaging().replyDanger(context.i18n("commands.queue.move.self"));
                return;
            }

            context.getMusicPlayer().moveTrack(track, pos);
            context.getTypedMessaging().replySuccess(context.i18n("commands.queue.move.moved", track + 1, pos + 1));
        } else if (context.getArgs().length < 2) {
            int start = 0;
            if (context.getArgs().length > 0 && context.isArgInteger(0)) {
                start = context.getArgAsInteger(0);
            }
            List<MovableAudioTrack> movableAudioTracks = context.getMusicPlayer().getQueue().stream().map(MovableAudioTrack::new).collect(Collectors.toList());;
            MovableList<MovableAudioTrack> movableList = new MovableList<>(movableAudioTracks);
            movableList.moveSelection(start);
            ButtonGroup group = new ButtonGroup(context.getMember().getIdLong(), context.getChannel().getIdLong(), context.getGuild().getIdLong());
            group.addButton(new Button.UnicodeButton(UnicodeConstants.ARROW_UP, (runner, channel, message) -> {
                if (runner.getIdLong() != context.getMember().getIdLong()) {
                    return;
                }
                movableList.notifyListChange(context.getMusicPlayer().getQueue().stream().map(MovableAudioTrack::new).collect(Collectors.toList()));
                movableList.moveSelection(-1);
                message.editMessage(getMoveEmbed(movableList).build()).override(true).queue();
            }));
            group.addButton(new Button.UnicodeButton(UnicodeConstants.ARROW_DOWN, (runner, channel, message) -> {
                if (runner.getIdLong() != context.getMember().getIdLong()) {
                    return;
                }
                movableList.notifyListChange(context.getMusicPlayer().getQueue().stream().map(MovableAudioTrack::new).collect(Collectors.toList()));
                movableList.moveSelection(1);
                message.editMessage(getMoveEmbed(movableList).build()).override(true).queue();
            }));
            group.addButton(new Button.UnicodeButton(UnicodeConstants.TICK, (runner, channel, message) -> {
                if (runner.getIdLong() != context.getMember().getIdLong()) {
                    return;
                }
                movableList.notifyListChange(context.getMusicPlayer().getQueue().stream().map(MovableAudioTrack::new).collect(Collectors.toList()));
                if (movableList.isMoving()) {
                    List<MovableAudioTrack> movedAudioTracks = movableList.confirmMove();
                    context.getMusicPlayer().getQueue().clear();
                    context.getMusicPlayer().getQueue().addAll(movedAudioTracks.stream().map(MovableAudioTrack::getTrack).collect(Collectors.toList()));
                } else {
                    movableList.startMovingItem();
                }
                message.editMessage(getMoveEmbed(movableList).build()).override(true).queue();
            }));
            group.addButton(new Button.UnicodeButton(UnicodeConstants.RED_CROSS, (runner, channel, message) -> {
                if (runner.getIdLong() != context.getMember().getIdLong()) {
                    return;
                }
                movableList.notifyListChange(context.getMusicPlayer().getQueue().stream().map(MovableAudioTrack::new).collect(Collectors.toList()));
                if (movableList.isMoving()) {
                    movableList.cancelMovingItem();
                    message.editMessage(getMoveEmbed(movableList).build()).override(true).queue();
                } else {
                    message.delete().queue();
                }
            }));
            context.getUIMessaging().sendButtonedMessage(getMoveEmbed(movableList).build(), group);
        } else {
            context.getUIMessaging().replyUsage();
        }
    }

    public EmbedBuilder getMoveEmbed(MovableList<MovableAudioTrack> list) {
        EmbedBuilder builder = MessagingObjects.getMessageTypeEmbedBuilder(MessageType.INFO);
        builder.setTitle("Queue Move");
        builder.appendDescription(list.getFrontendText());
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
        return "queue";
    }

}
