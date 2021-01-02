package org.cascadebot.cascadebot.scripting.objects;

import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.StoreChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.cascadebot.cascadebot.scripting.Promise;
import org.graalvm.polyglot.Value;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ScriptChannel extends ScriptSnowflake {

    protected GuildChannel internalChannel;

    public ScriptChannel(ScriptContext scriptContext) {
        super(scriptContext);
    }

    public ScriptCategory getParent() {
        Category parent = internalChannel.getParent();
        if (parent == null) {
            return null;
        }
        ScriptCategory scriptCategory = new ScriptCategory(scriptContext);
        scriptCategory.setInternalCategory(parent);
        return scriptCategory;
    }

    public boolean hasParent() {
        return internalChannel.getParent() != null;
    }

    public List<ScriptUser> getUsers() {
        return internalChannel.getMembers().stream().map(member -> {
            ScriptUser scriptUser = new ScriptUser(scriptContext);
            scriptUser.setInternalUser(member);
            return scriptUser;
        }).collect(Collectors.toList());
    }

    public ChannelType getType() {
        return internalChannel.getType();
    }

    public String getName() {
        return internalChannel.getName();
    }

    public int getPosition() {
        return internalChannel.getPosition();
    }

    public int getPositionRaw() {
        return internalChannel.getPositionRaw();
    }

    public ScriptPermissionOverride getPermissionOverride(ScriptPermissionHolder scriptPermissionHolder) {
        PermissionOverride permissionOverride = internalChannel.getPermissionOverride(scriptPermissionHolder.internalPermissionHolder);
        if (permissionOverride == null) {
            return null;
        }
        ScriptPermissionOverride scriptPermissionOverride = new ScriptPermissionOverride(scriptContext);
        scriptPermissionOverride.setInternalPermissionOverride(permissionOverride);
        return scriptPermissionOverride;
    }

    public List<ScriptPermissionOverride> getPermissionOverrides() {
        return internalChannel.getPermissionOverrides().stream().map(permissionOverride -> {
            ScriptPermissionOverride scriptPermissionOverride = new ScriptPermissionOverride(scriptContext);
            scriptPermissionOverride.setInternalPermissionOverride(permissionOverride);
            return scriptPermissionOverride;
        }).collect(Collectors.toList());
    }

    public List<ScriptPermissionOverride> getUserPermissionOverrides() {
        return internalChannel.getMemberPermissionOverrides().stream().map(permissionOverride -> {
            ScriptPermissionOverride scriptPermissionOverride = new ScriptPermissionOverride(scriptContext);
            scriptPermissionOverride.setInternalPermissionOverride(permissionOverride);
            return scriptPermissionOverride;
        }).collect(Collectors.toList());
    }

    public List<ScriptPermissionOverride> getRolePermissionOverrides() {
        return internalChannel.getRolePermissionOverrides().stream().map(permissionOverride -> {
            ScriptPermissionOverride scriptPermissionOverride = new ScriptPermissionOverride(scriptContext);
            scriptPermissionOverride.setInternalPermissionOverride(permissionOverride);
            return scriptPermissionOverride;
        }).collect(Collectors.toList());
    }

    public Promise createPermissionOverride(ScriptPermissionHolder permissionHolder) {
        CompletableFuture<PermissionOverride> completableFuture = internalChannel.createPermissionOverride(permissionHolder.internalPermissionHolder).submit();

        return new Promise() {
            @NotNull
            @Override
            public Promise intThen(@NotNull Value resolve, @NotNull Value reject, @NotNull Value callback) {
                completableFuture.whenComplete((permissionOverride, throwable) -> {
                    if (throwable == null) {
                        resolve.executeVoid(permissionOverride);
                    } else {
                        reject.executeVoid(throwable);
                    }
                    callback.executeVoid();
                });
                return this;
            }
        };
    }

    public Promise getInvites() {
        CompletableFuture<List<Invite>> completableFuture = internalChannel.retrieveInvites().submit();

        return new Promise() {
            @NotNull
            @Override
            public Promise intThen(@NotNull Value resolve, @NotNull Value reject, @NotNull Value callback) {
                completableFuture.whenComplete((invites, throwable) -> {
                    if (throwable == null) {
                        resolve.executeVoid(invites.stream().map(invite -> {
                            ScriptInvite scriptInvite = new ScriptInvite(scriptContext);
                            scriptInvite.setInternalInvite(invite);
                            return scriptInvite;
                        }).collect(Collectors.toList()));
                    } else {
                        reject.executeVoid(throwable);
                    }
                    callback.executeVoid();
                });
                return this;
            }
        };
    }

    public Promise createInvite() {
        CompletableFuture<Invite> completableFuture = internalChannel.createInvite().submit();
        return new Promise() {
            @NotNull
            @Override
            public Promise intThen(@NotNull Value resolve, @NotNull Value reject, @NotNull Value callback) {
                completableFuture.whenComplete((invite, throwable) -> {
                    if (throwable == null) {
                        ScriptInvite scriptInvite = new ScriptInvite(scriptContext);
                        scriptInvite.setInternalInvite(invite);
                        resolve.executeVoid(scriptInvite);
                    } else {
                        reject.executeVoid(throwable);
                    }
                    callback.executeVoid();
                });
                return this;
            }
        };
    }

    public Promise move(int position) {
        if (internalChannel.getParent() == null) {
            return moveRoot(position);
        }
        CompletableFuture<Void> voidCompletableFuture = internalChannel.getGuild().modifyVoiceChannelPositions(internalChannel.getParent())
                .selectPosition(internalChannel).moveTo(position)
                .submit();

        return scriptContext.handleVoidCompletableFuture(voidCompletableFuture);
    }

    public Promise moveRoot(int position) {
        CompletableFuture<Void> voidCompletableFuture = internalChannel.getGuild().modifyVoiceChannelPositions()
                .selectPosition(internalChannel).moveTo(position)
                .submit();

        return scriptContext.handleVoidCompletableFuture(voidCompletableFuture);
    }

    public Promise setName(String name) {
        CompletableFuture<Void> voidCompletableFuture = internalChannel.getManager().setName(name).submit();

        return scriptContext.handleVoidCompletableFuture(voidCompletableFuture);
    }

    public Promise delete() {
        CompletableFuture<Void> voidCompletableFuture = internalChannel.delete().submit();

        return scriptContext.handleVoidCompletableFuture(voidCompletableFuture);
    }

    public static ScriptChannel fromJda(ScriptContext scriptContext, GuildChannel channel) {
        if (channel instanceof Category) {
            ScriptCategory scriptCategory = new ScriptCategory(scriptContext);
            scriptCategory.setInternalCategory((Category) channel);
            return scriptCategory;
        } else if (channel instanceof TextChannel) {
            ScriptTextChannel scriptTextChannel = new ScriptTextChannel(scriptContext);
            scriptTextChannel.setInternalTextChannel((TextChannel) channel);
            return scriptTextChannel;
        } else if (channel instanceof VoiceChannel) {
            ScriptVoiceChannel scriptVoiceChannel = new ScriptVoiceChannel(scriptContext);
            scriptVoiceChannel.setInternalVoiceChannel((VoiceChannel) channel);
            return scriptVoiceChannel;
        } else if (channel instanceof StoreChannel) {
            ScriptStoreChannel scriptStoreChannel = new ScriptStoreChannel(scriptContext);
            scriptStoreChannel.setInternalStoreChannel((StoreChannel) channel);
            return scriptStoreChannel;
        } else {
            return null;
        }
    }

}
