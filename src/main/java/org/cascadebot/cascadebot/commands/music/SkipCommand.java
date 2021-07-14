/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.MainCommand;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.data.objects.VoteMessageType;
import org.cascadebot.cascadebot.messaging.MessageType;
import org.cascadebot.cascadebot.messaging.Messaging;
import org.cascadebot.cascadebot.messaging.MessagingObjects;
import org.cascadebot.cascadebot.music.TrackData;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.interactions.PersistentComponent;
import org.cascadebot.cascadebot.utils.votes.VoteFinishConsumer;
import org.cascadebot.cascadebot.utils.votes.VoteGroup;
import org.cascadebot.cascadebot.utils.votes.VoteGroupBuilder;
import org.cascadebot.cascadebot.utils.votes.VotePeriodicConsumer;
import org.cascadebot.cascadebot.utils.votes.VoteResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SkipCommand extends MainCommand {

    public static Map<Long, VoteGroup> voteMap = new HashMap<>();

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

        if (voteMap.containsKey(context.getGuild().getIdLong())) {
            VoteGroup voteGroup = voteMap.get(context.getGuild().getIdLong());
            if (context.getArgs().length > 0) {
                if (context.getArg(0).equalsIgnoreCase("yes")) {
                    if (voteGroup.isUserAllowed(sender.getIdLong())) {
                        voteGroup.addVote(sender.getUser(), UnicodeConstants.TICK);
                        context.getTypedMessaging().replyWarning(context.i18n("commands.skip.added_vote"));
                    } else {
                        context.getTypedMessaging().replyDanger(context.i18n("commands.skip.cannot_skip.no_vote"));
                    }
                    return;
                } else if (context.getArg(0).equalsIgnoreCase("no")) {
                    if (voteGroup.isUserAllowed(sender.getIdLong())) {
                        voteGroup.addVote(sender.getUser(), UnicodeConstants.RED_CROSS);
                        context.getTypedMessaging().replyWarning(context.i18n("commands.skip.added_vote"));
                    } else {
                        context.getTypedMessaging().replyDanger(context.i18n("commands.skip.cannot_skip.no_vote"));
                    }
                    return;
                }
            }
            voteGroup.addVote(sender.getUser(), UnicodeConstants.TICK);
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

        VoteGroupBuilder buttonGroupBuilder = new VoteGroupBuilder(VoteMessageType.YES_NO);
        buttonGroupBuilder.addExtraButton(PersistentComponent.SKIP_BUTTON_FORCE);
        buttonGroupBuilder.setPeriodicConsumer(VotePeriodicConsumer.SKIP);
        buttonGroupBuilder.setVoteFinishConsumer(VoteFinishConsumer.SKIP);
        VoteGroup voteGroup = buttonGroupBuilder.build(sender.getIdLong(), context.getChannel().getIdLong(), context.getGuild().getIdLong());

        // Specific settings for music skip
        voteGroup.setTimerRunTime(10);
        voteGroup.setMaxTimeRunTime(30);
        voteGroup.setTimerRunTimeSkipAddon(5);
        voteGroup.setIsDynamicTiming(true);

        for (Member member : context.getGuild().getSelfMember().getVoiceState().getChannel().getMembers()) {
            if (context.hasPermission(member, "skip")) {
                voteGroup.allowUser(member.getIdLong());
            }
        }
        //voteMap.put(context.getGuild().getIdLong(), voteGroup);
        EmbedBuilder skipVoteEmbed = MessagingObjects.getMessageTypeEmbedBuilder(MessageType.INFO, context.getUser(), context.getLocale())
                .setTitle(context.i18n("commands.skip.skip_vote_title"));
        Messaging.sendComponentMessage(context.getChannel(), skipVoteEmbed.build(), voteGroup.getComponents()).thenAccept(message -> voteGroup.setMessageId(message.getIdLong()));
        voteGroup.addVote(sender.getUser(), UnicodeConstants.TICK);
        for (Member member : context.getMusicPlayer().getConnectedChannel().getMembers()) {
            voteGroup.allowUser(member.getIdLong());
        }
        voteMap.put(context.getGuild().getIdLong(), voteGroup); // TODO I want to get rid of this map, but it's needed for adding/removing users, so maybe add id to vote groups?
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
