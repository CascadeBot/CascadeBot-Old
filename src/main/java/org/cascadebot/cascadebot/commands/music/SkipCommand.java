/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.MainCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.data.objects.VoteMessageType;
import org.cascadebot.cascadebot.messaging.MessageType;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.music.TrackData;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.buttons.PersistentButton;
import org.cascadebot.cascadebot.utils.votes.VoteButtonGroup;
import org.cascadebot.cascadebot.utils.votes.VoteButtonGroupBuilder;
import org.cascadebot.cascadebot.utils.votes.VoteResult;

import java.util.HashMap;
import java.util.Map;

public class SkipCommand extends MainCommand {

    public static Map<Long, VoteButtonGroup> voteMap = new HashMap<>();

    @Override
    public void onCommand(Member sender, CommandContext context) {
        AudioTrack track = context.getMusicPlayer().getPlayingTrack();
        if (track == null) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.skip.not_playing"));
            return;
        }

        if (context.getArgs().length > 0) {
            if (context.getArg(0).equalsIgnoreCase("force")) {
                if (context.hasPermission("skip.force")) {
                    context.getMusicPlayer().skip();
                    context.getTypedMessaging().replySuccess(context.i18n("commands.skip.forcefully_skipped"));
                } else {
                    context.getUiMessaging().sendPermissionError("skip.force");
                }
                return;
            }
        }

        if (!sender.getVoiceState().inVoiceChannel() || !sender.getVoiceState().getChannel().equals(context.getGuild().getSelfMember().getVoiceState().getChannel())) {
            context.getTypedMessaging().replyDanger(context.i18n("commands.skip.cannot_skip.no_music"));
            return;
        }

        VoteButtonGroup voteButtonGroup = voteMap.get(context.getGuild().getIdLong());
        if (voteMap.containsKey(context.getGuild().getIdLong())) {
            if (context.getArgs().length > 0) {
                if (context.getArg(0).equalsIgnoreCase("yes")) {
                    if (voteButtonGroup.isUserAllowed(sender.getIdLong())) {
                        voteButtonGroup.addVote(sender.getUser(), UnicodeConstants.TICK);
                        context.getTypedMessaging().replyWarning(context.i18n("commands.skip.added_vote"));
                    } else {
                        context.getTypedMessaging().replyDanger(context.i18n("commands.skip.cannot_skip.no_vote"));
                    }
                    return;
                } else if (context.getArg(0).equalsIgnoreCase("no")) {
                    if (voteButtonGroup.isUserAllowed(sender.getIdLong())) {
                        voteButtonGroup.addVote(sender.getUser(), UnicodeConstants.RED_CROSS);
                        context.getTypedMessaging().replyWarning(context.i18n("commands.skip.added_vote"));
                    } else {
                        context.getTypedMessaging().replyDanger(context.i18n("commands.skip.cannot_skip.no_vote"));
                    }
                    return;
                }
            }
            voteButtonGroup.addVote(sender.getUser(), UnicodeConstants.TICK);
            context.getTypedMessaging().replyWarning(context.i18n("commands.skip.added_vote"));
            return;
        }

        if (track.getUserData() instanceof TrackData) {
            if (((TrackData) track.getUserData()).getUserId() == context.getMember().getIdLong()) {
                context.getMusicPlayer().skip();
                context.getTypedMessaging().replySuccess(context.i18n("commands.skip.skipped_user_queued"));
                return;
            }
        }

        VoteButtonGroupBuilder buttonGroupBuilder = new VoteButtonGroupBuilder(VoteMessageType.YES_NO);
        buttonGroupBuilder.addExtraButton(PersistentButton.SKIP_BUTTON_FORCE);
        buttonGroupBuilder.setPeriodicConsumer((results, message) -> {
            StringBuilder resultsBuilder = new StringBuilder();
            for (VoteResult result : results) {
                resultsBuilder.append(result.getVote()).append(" (").append(result.getAmount()).append(")\n");
            }
            EmbedBuilder skipVoteEmbed = MessagingObjects.getMessageTypeEmbedBuilder(MessageType.INFO, context.getUser())
                    .setTitle(context.i18n("commands.skip.skip_vote_title"));
            if (resultsBuilder.length() > 0) {
                skipVoteEmbed.setDescription(resultsBuilder.toString());
            }
            message.editMessage(skipVoteEmbed.build()).queue();
        });
        buttonGroupBuilder.setVoteFinishConsumer(voteResults -> {
            if (voteResults.size() != 0 && voteResults.get(0).getVote().equals(UnicodeConstants.TICK)) {
                context.getTypedMessaging().replyInfo(context.i18n("commands.skip.skipping"));
                context.getMusicPlayer().skip();
                voteMap.remove(context.getGuild().getIdLong());
            } else {
                voteMap.remove(context.getGuild().getIdLong());
                context.getTypedMessaging().replyInfo(context.i18n("commands.skip.not_skipping"));
            }
        });
        VoteButtonGroup buttonGroup = buttonGroupBuilder.build(sender.getIdLong(), context.getChannel().getIdLong(), context.getGuild().getIdLong());

        // Specific settings for music skip
        buttonGroup.setTimerRunTime(10);
        buttonGroup.setMaxTimeRunTime(30);
        buttonGroup.setTimerRunTimeSkipAddon(5);
        buttonGroup.setIsDynamicTiming(true);

        for (Member member : context.getGuild().getSelfMember().getVoiceState().getChannel().getMembers()) {
            if (context.hasPermission(member, "skip")) {
                buttonGroup.allowUser(member.getIdLong());
            }
        }
        voteMap.put(context.getGuild().getIdLong(), buttonGroup);
        EmbedBuilder skipVoteEmbed = MessagingObjects.getMessageTypeEmbedBuilder(MessageType.INFO, context.getUser())
                .setTitle(context.i18n("commands.skip.skip_vote_title"));
        context.getUiMessaging().sendButtonedMessage(skipVoteEmbed.build(), buttonGroup);
        buttonGroup.addVote(sender.getUser(), UnicodeConstants.TICK);
        for (Member member : context.getMusicPlayer().getConnectedChannel().getMembers()) {
            buttonGroup.allowUser(member.getIdLong());
        }
    }

    @Override
    public String command() {
        return "skip";
    }

    @Override
    public Module module() {
        return Module.MUSIC;
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("skip", true);
    }

}
