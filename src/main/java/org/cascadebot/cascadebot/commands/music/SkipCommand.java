/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.UnicodeConstants;
import org.cascadebot.cascadebot.commandmeta.Argument;
import org.cascadebot.cascadebot.commandmeta.ArgumentType;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.buttons.Button;
import org.cascadebot.cascadebot.utils.votes.VoteButtonGroup;
import org.cascadebot.cascadebot.utils.votes.VoteButtonGroupBuilder;
import org.cascadebot.cascadebot.utils.votes.VoteMessageType;
import org.cascadebot.cascadebot.utils.votes.VoteResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class SkipCommand implements ICommandMain {

    public static Map<Long, VoteButtonGroup> voteMap = new HashMap<>();

    @Override
    public void onCommand(Member sender, CommandContext context) {
        AudioTrack track = context.getMusicPlayer().getPlayer().getPlayingTrack();
        if (track == null) {
            context.getTypedMessaging().replyDanger("I'm not playing anything!");
            return;
        }

        if (context.getMusicPlayer().getTracks().peek() == null) {
            context.getTypedMessaging().replyDanger("No song to skip to!");
            return;
        }

        if (context.getArgs().length > 0) {
            if (context.getArg(0).equalsIgnoreCase("force")) {
                if (context.hasPermission("skip.force")) {
                    context.getMusicPlayer().skip();
                    context.getTypedMessaging().replySuccess("Forcefully skipped the song");
                } else {
                    context.getUIMessaging().sendPermissionError("skip.force");
                }
                return;
            }
        }

        if (!sender.getVoiceState().inVoiceChannel() && !sender.getVoiceState().getChannel().equals(context.getGuild().getSelfMember().getVoiceState().getChannel())) {
            context.getTypedMessaging().replyDanger("Can't skip if you aren't listening to music!");
            return;
        }

        VoteButtonGroup voteButtonGroup = voteMap.get(context.getGuild().getIdLong());
        if (voteMap.containsKey(context.getGuild().getIdLong())) {
            if (context.getArgs().length > 0) {
                if (context.getArg(0).equalsIgnoreCase("yes")) {
                    if (voteButtonGroup.isUserAllowed(context.getGuild().getIdLong())) {
                        voteButtonGroup.addVote(sender.getUser(), UnicodeConstants.TICK);
                    } else {
                        context.getTypedMessaging().replyDanger("Cannot vote if you aren't listening!");
                    }
                    return;
                } else if (context.getArg(0).equalsIgnoreCase("no")) {
                    if (voteButtonGroup.isUserAllowed(context.getGuild().getIdLong())) {
                        voteButtonGroup.addVote(sender.getUser(), UnicodeConstants.RED_CROSS);
                    } else {
                        context.getTypedMessaging().replyDanger("Cannot vote if you aren't listening!");
                    }
                    return;
                }
            }
            voteButtonGroup.addVote(sender.getUser(), UnicodeConstants.TICK);
            context.getTypedMessaging().replyWarning("A skip vote is already running, but we added your vote automatically");
            return;
        }

        if (Objects.equals(track.getUserData(), sender.getUser().getIdLong())) {
            context.getMusicPlayer().skip();
            context.getTypedMessaging().replySuccess("Skipped currently playing song because you queued it");
            return;
        }

        VoteButtonGroupBuilder buttonGroupBuilder = new VoteButtonGroupBuilder(VoteMessageType.YES_NO);
        buttonGroupBuilder.addExtraButton(new Button.UnicodeButton(UnicodeConstants.FAST_FORWARD, (runner, channel, message) -> {
            //TODO check perm
            message.delete().queue();
            voteButtonGroup.stopVote();
            voteMap.remove(context.getGuild().getIdLong());
            context.getMusicPlayer().skip();
            context.getTypedMessaging().replySuccess("Forcefully skipped the song");
        }));
        buttonGroupBuilder.setPeriodicConsumer((results, message) -> {
            StringBuilder resultsBuilder = new StringBuilder();
            for (VoteResult result : results) {
                resultsBuilder.append(result.getVote()).append(" - ").append(result.getAmount()).append(' ');
            }
            message.editMessage("Skip Vote\n" + resultsBuilder.toString()).queue();
        });
        buttonGroupBuilder.setVoteFinishConsumer(voteResults -> {
            if (voteResults.get(0).getVote().equals(UnicodeConstants.TICK)) {
                context.getTypedMessaging().replyInfo("Skipping song!");
                context.getMusicPlayer().skip();
                voteMap.remove(context.getGuild().getIdLong());
            } else {
                voteMap.remove(context.getGuild().getIdLong());
                context.getTypedMessaging().replyInfo("Not skipping the song!");
            }
        });
        VoteButtonGroup buttonGroup = buttonGroupBuilder.build(sender.getUser().getIdLong(), context.getChannel().getIdLong(), context.getGuild().getIdLong());
        for (Member member : context.getGuild().getSelfMember().getVoiceState().getChannel().getMembers()) {
            buttonGroup.allowUser(member.getUser().getIdLong());
        }
        voteMap.put(context.getGuild().getIdLong(), buttonGroup);
        context.getUIMessaging().sendButtonedMessage("Skip Vote", buttonGroup);
        buttonGroup.addVote(sender.getUser(), UnicodeConstants.TICK);
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
        return CascadePermission.of("Skip", "skip", Module.MUSIC);
    }

    @Override
    public String description() {
        return "skips the current song";
    }

    @Override
    public Set<Argument> getUndefinedArguments() {
        return Set.of(Argument.of("force", "force skips a track", ArgumentType.OPTIONAL),
                Argument.of("yes", "votes yes for skipping the song", ArgumentType.COMMAND),
                Argument.of("no", "votes no for skipping the song", ArgumentType.COMMAND));
    }

}
