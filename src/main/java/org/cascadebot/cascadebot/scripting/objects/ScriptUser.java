package org.cascadebot.cascadebot.scripting.objects;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.data.managers.ScheduledActionManager;
import org.cascadebot.cascadebot.scheduler.ActionType;
import org.cascadebot.cascadebot.scheduler.ScheduledAction;
import org.cascadebot.cascadebot.scripting.Promise;
import org.cascadebot.cascadebot.utils.ExtensionsKt;
import org.cascadebot.shared.SecurityLevel;
import org.graalvm.polyglot.Value;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ScriptUser extends ScriptPermissionHolder {

    private Member internalUser;

    public ScriptUser(ScriptContext scriptContext) {
        super(scriptContext);
    }

    public String getAsTag() {
        return internalUser.getUser().getAsTag();
    }

    public String getAvatarId() {
        return internalUser.getUser().getAvatarId();
    }

    public String getAvatarUrl() {
        return internalUser.getUser().getAvatarUrl();
    }

    public String getDefaultAvatarId() {
        return internalUser.getUser().getDefaultAvatarId();
    }

    public String getDefaultAvatarUrl() {
        return internalUser.getUser().getDefaultAvatarUrl();
    }

    public String getDiscriminator() {
        return internalUser.getUser().getDiscriminator();
    }

    public String getEffectiveAvatarUrl() {
        return internalUser.getUser().getEffectiveAvatarUrl();
    }

    public String getName() {
        return internalUser.getUser().getName();
    }

    public boolean isBot() {
        return internalUser.getUser().isBot();
    }

    public String getNickname() {
        return internalUser.getNickname();
    }

    public String getTimeJoined() {
        return String.valueOf(internalUser.getTimeJoined().toInstant().toEpochMilli());
    }

    public String getTimeBoosted() {
        OffsetDateTime offsetDateTime = internalUser.getTimeBoosted();
        if (offsetDateTime == null) {
            return null;
        } else {
            return String.valueOf(internalUser.getTimeBoosted().toInstant().toEpochMilli());
        }
    }

    public ScriptGuildVoiceState getVoiceState() {
        ScriptGuildVoiceState voiceState = new ScriptGuildVoiceState(scriptContext);
        voiceState.setInternalVoiceState(internalUser.getVoiceState());
        return voiceState;
    }

    public List<Activity> getActivities() {
        return internalUser.getActivities();
    }

    public OnlineStatus getOnlineStatus() {
        return internalUser.getOnlineStatus();
    }

    public String getEffectiveName() {
        return internalUser.getEffectiveName();
    }

    public List<ScriptRole> getRoles() {
        return internalUser.getRoles().stream().map(role -> {
            ScriptRole scriptRole = new ScriptRole(scriptContext);
            scriptRole.setInternalRole(role);
            return scriptRole;
        }).collect(Collectors.toList());
    }

    public Color getColor() {
        return internalUser.getColor();
    }

    public boolean isOwner() {
        return internalUser.isOwner();
    }

    public List<User.UserFlag> getFlags() {
        return new ArrayList<>(internalUser.getUser().getFlags());
    }

    public SecurityLevel getCascadeOfficialRoleLevel() {
        return CascadeBot.INS.getPermissionsManager().getUserSecurityLevel(internalUser.getIdLong());
    }

    public Promise ban(String reason) {
        CompletableFuture<Void> voidCompletableFuture = internalUser.ban(7, reason).submit();
        return scriptContext.handleVoidCompletableFuture(voidCompletableFuture);
    }

    public Promise ban(String reason, String time) {
        CompletableFuture<Void> voidCompletableFuture = internalUser.getGuild().ban(internalUser, 7).reason(reason).submit();
        return new Promise() {
            @NotNull
            @Override
            public Promise intThen(@NotNull Value resolve, @NotNull Value reject, @NotNull Value callback) {
                voidCompletableFuture.whenComplete((unused, throwable) -> {
                    if (throwable == null) {
                        ScheduledActionManager.registerScheduledAction(new ScheduledAction(
                                ActionType.UNBAN,
                                new ScheduledAction.ModerationActionData(internalUser.getIdLong()),
                                internalUser.getGuild().getIdLong(),
                                scriptContext.getChannel().getIdLong(),
                                scriptContext.getRunner().getIdLong(),
                                Instant.now(),
                                Long.parseLong(time)
                        ));
                        resolve.executeVoid(true);
                    } else {
                        reject.executeVoid(throwable);
                    }
                    callback.executeVoid();
                });
                return this;
            }
        };
    }

    public Promise softBan(String reason) {
        CompletableFuture<Void> banCompletableFuture = internalUser.getGuild().ban(internalUser, 7).reason(reason).submit();
        return new Promise() {
            @NotNull
            @Override
            public Promise intThen(@NotNull Value resolve, @NotNull Value reject, @NotNull Value callback) {
                banCompletableFuture.whenComplete((banUnused, banThrowable) -> {
                    if (banThrowable == null) {
                        CompletableFuture<Void> unbanCompletableFuture = internalUser.getGuild().unban(internalUser.getUser()).submit();
                        unbanCompletableFuture.whenComplete((unbanUnused, unbanThrowable) -> {
                            if (unbanThrowable == null) {
                                resolve.executeVoid(true);
                            } else {
                                reject.executeVoid(unbanThrowable);
                            }
                            callback.executeVoid();
                        });
                    } else {
                        reject.executeVoid(banThrowable);
                        callback.executeVoid();
                    }
                });
                return this;
            }
        };
    }

    public Promise kick(String reason) {
        CompletableFuture<Void> voidCompletableFuture = internalUser.getGuild().kick(internalUser).submit();
        return scriptContext.handleVoidCompletableFuture(voidCompletableFuture);
    }

    public Promise mute(String reason) {
        CompletableFuture<Void> voidCompletableFuture = internalUser.getGuild()
                .addRoleToMember(internalUser, ExtensionsKt.getMutedRole(internalUser.getGuild())).reason(reason)
                .submit();
        return scriptContext.handleVoidCompletableFuture(voidCompletableFuture);
    }

    public Promise mute(String reason, String time) {
        CompletableFuture<Void> voidCompletableFuture = internalUser.getGuild()
                .addRoleToMember(internalUser, ExtensionsKt.getMutedRole(internalUser.getGuild())).reason(reason)
                .submit();

        return new Promise() {
            @NotNull
            @Override
            public Promise intThen(@NotNull Value resolve, @NotNull Value reject, @NotNull Value callback) {
                voidCompletableFuture.whenComplete((unused, throwable) -> {
                    if (throwable == null) {
                        ScheduledActionManager.registerScheduledAction(new ScheduledAction(
                                ActionType.UNMUTE,
                                new ScheduledAction.ModerationActionData(internalUser.getIdLong()),
                                internalUser.getGuild().getIdLong(),
                                scriptContext.getChannel().getIdLong(),
                                scriptContext.getRunner().getIdLong(),
                                Instant.now(),
                                Long.parseLong(time)
                        ));
                        resolve.executeVoid(true);
                    } else {
                        reject.executeVoid(throwable);
                    }
                    callback.executeVoid();
                });
                return this;
            }
        };
    }

    public Promise addRole(ScriptRole role) {
        CompletableFuture<Void> voidCompletableFuture = internalUser.getGuild().addRoleToMember(internalUser, role.internalRole).submit();
        return scriptContext.handleVoidCompletableFuture(voidCompletableFuture);
    }

    public Promise removeRole(ScriptRole role) {
        CompletableFuture<Void> voidCompletableFuture = internalUser.getGuild().removeRoleFromMember(internalUser, role.internalRole).submit();
        return scriptContext.handleVoidCompletableFuture(voidCompletableFuture);
    }

    public static ScriptUser getUser(Guild guild, String id, ScriptContext scriptContext) {
        User user = CascadeBot.INS.getShardManager().getUserById(id);
        if (user == null) {
            return null;
        } else {
            Member member = guild.getMember(user);
            ScriptUser scriptUser = new ScriptUser(scriptContext);
            scriptUser.internalUser = member;
            return scriptUser;
        }
    }

    protected void setInternalUser(Member member) {
        this.internalUser = member;
        this.internalPermissionHolder = member;
        this.internalSnowflake = member;
    }

}
