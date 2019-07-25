/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.DiscordUtils;
import org.cascadebot.cascadebot.utils.buttons.Button;
import org.cascadebot.cascadebot.utils.votes.VoteButtonGroup;
import org.cascadebot.cascadebot.utils.votes.VoteButtonGroupBuilder;
import org.cascadebot.cascadebot.utils.votes.VoteMessageType;
import org.cascadebot.cascadebot.utils.votes.VoteResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SkipCommand implements ICommandMain {

    public static Map<Long, VoteButtonGroup> voteMap = new HashMap<>();

    @Override
    public void onCommand(Member sender, CommandContext context) {
        AudioTrack track = context.getMusicPlayer().getPlayer().getPlayingTrack();
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
                    context.getUIMessaging().sendPermissionError("skip.force");
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

        if (Objects.equals(track.getUserData(), sender.getIdLong())) {
            context.getMusicPlayer().skip();
            context.getTypedMessaging().replySuccess(context.i18n("commands.skip.skipped_user_queued"));
            return;
        }

        VoteButtonGroupBuilder buttonGroupBuilder = new VoteButtonGroupBuilder(VoteMessageType.YES_NO);
        buttonGroupBuilder.addExtraButton(new Button.UnicodeButton(UnicodeConstants.FAST_FORWARD, (runner, channel, message) -> {
            if (context.hasPermission(runner, "skip.force")) {
                message.delete().queue(null, DiscordUtils.handleExpectedErrors(ErrorResponse.UNKNOWN_MESSAGE));
                voteButtonGroup.stopVote();
                voteMap.remove(context.getGuild().getIdLong());
                context.getMusicPlayer().skip();
                context.getTypedMessaging().replySuccess(context.i18n("commands.skip.forcefully_skipped"));
            } else {
                context.getUIMessaging().sendPermissionError("skip.force");
            }
        }));
        buttonGroupBuilder.setPeriodicConsumer((results, message) -> {
            StringBuilder resultsBuilder = new StringBuilder();
            for (VoteResult result : results) {
                resultsBuilder.append(result.getVote()).append(" - ").append(result.getAmount()).append(' ');
            }
            message.editMessage("Skip Vote\n" + resultsBuilder.toString()).queue();
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
        for (Member member : context.getGuild().getSelfMember().getVoiceState().getChannel().getMembers()) {
            if (context.hasPermission(member, "skip")) {
                buttonGroup.allowUser(member.getIdLong());
            }
        }
        voteMap.put(context.getGuild().getIdLong(), buttonGroup);
        context.getUIMessaging().sendButtonedMessage("Skip Vote", buttonGroup);
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
    public Module getModule() {
        return Module.MUSIC;
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("skip", true);
    }

}
