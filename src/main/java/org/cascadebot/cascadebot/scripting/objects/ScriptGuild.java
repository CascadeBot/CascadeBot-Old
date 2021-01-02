package org.cascadebot.cascadebot.scripting.objects;

import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.cascadebot.cascadebot.scripting.Promise;
import org.graalvm.polyglot.Value;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ScriptGuild extends ScriptSnowflake {

    private Guild internalGuild;

    public ScriptGuild(ScriptContext scriptContext) {
        super(scriptContext);
    }

    public ScriptVoiceChannel getAfkChannel() {
        ScriptVoiceChannel scriptVoiceChannel = new ScriptVoiceChannel(scriptContext);
        scriptVoiceChannel.setInternalVoiceChannel(internalGuild.getAfkChannel());
        return scriptVoiceChannel;
    }

    public Guild.Timeout getAfkTimeout() {
        return internalGuild.getAfkTimeout();
    }

    public String getBannerId() {
        return internalGuild.getBannerId();
    }

    public String getBannerUrl() {
        return internalGuild.getBannerUrl();
    }

    public int getBoostCount() {
        return internalGuild.getBoostCount();
    }

    public List<ScriptUser> getBoosters() {
        return internalGuild.getBoosters().stream().map((member -> {
            ScriptUser scriptUser = new ScriptUser(scriptContext);
            scriptUser.setInternalUser(member);
            return scriptUser;
        })).collect(Collectors.toList());
    }

    public Guild.BoostTier getBoostTier() {
        return internalGuild.getBoostTier();
    }

    public List<ScriptCategory> getCategories() {
        return internalGuild.getCategories().stream().map((category -> {
            ScriptCategory scriptCategory = new ScriptCategory(scriptContext);
            scriptCategory.setInternalCategory(category);
            return scriptCategory;
        })).collect(Collectors.toList());
    }

    public List<ScriptCategory> getCategoriesByName(String name, boolean ignoreCase) {
        return internalGuild.getCategoriesByName(name, ignoreCase).stream().map((category -> {
            ScriptCategory scriptCategory = new ScriptCategory(scriptContext);
            scriptCategory.setInternalCategory(category);
            return scriptCategory;
        })).collect(Collectors.toList());
    }

    public ScriptCategory getCategoryById(String id) {
        ScriptCategory scriptCategory = new ScriptCategory(scriptContext);
        scriptCategory.setInternalCategory(internalGuild.getCategoryById(id));
        return scriptCategory;
    }

    public List<ScriptChannel> getChannels() {
        return internalGuild.getChannels().stream().map(channel -> ScriptChannel.fromJda(scriptContext, channel)).collect(Collectors.toList());
    }

    public ScriptTextChannel getDefaultChannel() {
        ScriptTextChannel scriptTextChannel = new ScriptTextChannel(scriptContext);
        scriptTextChannel.setInternalTextChannel(internalGuild.getDefaultChannel());
        return scriptTextChannel;
    }

    public Guild.NotificationLevel getDefaultNotificationLevel() {
        return internalGuild.getDefaultNotificationLevel();
    }

    public String getDescription() {
        return internalGuild.getDescription();
    }

    public ScriptEmote getEmoteById(String id) {
        ScriptEmote scriptEmote = new ScriptEmote(scriptContext);
        scriptEmote.setInternalEmote(internalGuild.getEmoteById(id));
        return scriptEmote;
    }

    public List<ScriptEmote> getEmotes() {
        return internalGuild.getEmoteCache().asList().stream().map(emote -> {
            ScriptEmote scriptEmote = new ScriptEmote(scriptContext);
            scriptEmote.setInternalEmote(emote);
            return scriptEmote;
        }).collect(Collectors.toList());
    }

    public List<ScriptEmote> getEmotesByName(String name, boolean ignoreCase) {
        return internalGuild.getEmotesByName(name, ignoreCase).stream().map(emote -> {
            ScriptEmote scriptEmote = new ScriptEmote(scriptContext);
            scriptEmote.setInternalEmote(emote);
            return scriptEmote;
        }).collect(Collectors.toList());
    }

    public Guild.ExplicitContentLevel getExplicitContentLevel() {
        return internalGuild.getExplicitContentLevel();
    }

    public Set<String> getFeatures() {
        return internalGuild.getFeatures();
    }

    public ScriptChannel getGuildChannelById(String id) {
        return ScriptChannel.fromJda(scriptContext, internalGuild.getGuildChannelById(id));
    }

    public ScriptChannel getGuildChannelById(ChannelType type, String id) {
        return ScriptChannel.fromJda(scriptContext, internalGuild.getGuildChannelById(type, id));
    }

    public String getIconId() {
        return internalGuild.getIconId();
    }

    public String getIconUrl() {
        return internalGuild.getIconUrl();
    }

    public int getMaxBitrate() {
        return internalGuild.getMaxBitrate();
    }

    public int getMaxEmotes() {
        return internalGuild.getMaxEmotes();
    }

    public int getMaxMembers() {
        return internalGuild.getMaxMembers();
    }

    public int getMaxPresences() {
        return internalGuild.getMaxPresences();
    }

    public ScriptUser getUserById(String id) {
        ScriptUser scriptUser = new ScriptUser(scriptContext);
        scriptUser.setInternalUser(internalGuild.getMemberById(id));
        return scriptUser;
    }

    public ScriptUser getUserByTag(String tag) {
        ScriptUser scriptUser = new ScriptUser(scriptContext);
        scriptUser.setInternalUser(internalGuild.getMemberByTag(tag));
        return scriptUser;
    }

    public ScriptUser getUserByTag(String username, String discriminator) {
        ScriptUser scriptUser = new ScriptUser(scriptContext);
        scriptUser.setInternalUser(internalGuild.getMemberByTag(username, discriminator));
        return scriptUser;
    }

    public List<ScriptUser> getUsers() {
        return internalGuild.getMembers().stream().map(member -> {
            ScriptUser scriptUser = new ScriptUser(scriptContext);
            scriptUser.setInternalUser(member);
            return scriptUser;
        }).collect(Collectors.toList());
    }

    public List<ScriptUser> getUsersByEffectiveName(String name, boolean ignoreCase) {
        return internalGuild.getMembersByEffectiveName(name, ignoreCase).stream().map(member -> {
            ScriptUser scriptUser = new ScriptUser(scriptContext);
            scriptUser.setInternalUser(member);
            return scriptUser;
        }).collect(Collectors.toList());
    }

    public List<ScriptUser> getUsersByName(String name, boolean ignoreCase) {
        return internalGuild.getMembersByName(name, ignoreCase).stream().map(member -> {
            ScriptUser scriptUser = new ScriptUser(scriptContext);
            scriptUser.setInternalUser(member);
            return scriptUser;
        }).collect(Collectors.toList());
    }

    public List<ScriptUser> getUsersByNickname(String name, boolean ignoreCase) {
        return internalGuild.getMembersByNickname(name, ignoreCase).stream().map(member -> {
            ScriptUser scriptUser = new ScriptUser(scriptContext);
            scriptUser.setInternalUser(member);
            return scriptUser;
        }).collect(Collectors.toList());
    }

    public List<ScriptUser> getUsersWithRoles(Collection<ScriptRole> roles) {
        return internalGuild.getMembersWithRoles(roles.stream().map(scriptRole -> scriptRole.internalRole)
                .collect(Collectors.toList())).stream().map(member -> {
            ScriptUser scriptUser = new ScriptUser(scriptContext);
            scriptUser.setInternalUser(member);
            return scriptUser;
        }).collect(Collectors.toList());
    }

    public String getName() {
        return internalGuild.getName();
    }

    public ScriptUser getOwner() {
        ScriptUser scriptUser = new ScriptUser(scriptContext);
        scriptUser.setInternalUser(internalGuild.getOwner());
        return scriptUser;
    }

    public String getOwnerId() {
        return internalGuild.getOwnerId();
    }

    public ScriptRole getEveryoneRole() {
        ScriptRole scriptRole = new ScriptRole(scriptContext);
        scriptRole.setInternalRole(internalGuild.getPublicRole());
        return scriptRole;
    }

    public Region getRegion() {
        return internalGuild.getRegion();
    }

    public Guild.MFALevel getRequiredMFALevel() {
        return internalGuild.getRequiredMFALevel();
    }

    public ScriptRole getRoleById(String id) {
        ScriptRole scriptRole = new ScriptRole(scriptContext);
        scriptRole.setInternalRole(internalGuild.getRoleById(id));
        return scriptRole;
    }

    public List<ScriptRole> getRoles() {
        return internalGuild.getRoles().stream().map(role -> {
            ScriptRole scriptRole = new ScriptRole(scriptContext);
            scriptRole.setInternalRole(role);
            return scriptRole;
        }).collect(Collectors.toList());
    }

    public List<ScriptRole> getRolesByName(String name, boolean ignoreCase) {
        return internalGuild.getRolesByName(name, ignoreCase).stream().map(role -> {
            ScriptRole scriptRole = new ScriptRole(scriptContext);
            scriptRole.setInternalRole(role);
            return scriptRole;
        }).collect(Collectors.toList());
    }

    public String getSplashId() {
        return internalGuild.getSplashId();
    }

    public String getSplashUrl() {
        return internalGuild.getSplashUrl();
    }

    public ScriptStoreChannel getStoreChannelById(String id) {
        ScriptStoreChannel scriptStoreChannel = new ScriptStoreChannel(scriptContext);
        scriptStoreChannel.setInternalStoreChannel(internalGuild.getStoreChannelById(id));
        return scriptStoreChannel;
    }

    public List<ScriptStoreChannel> getStoreChannels() {
        return internalGuild.getStoreChannels().stream().map(storeChannel -> {
            ScriptStoreChannel scriptStoreChannel = new ScriptStoreChannel(scriptContext);
            scriptStoreChannel.setInternalStoreChannel(storeChannel);
            return scriptStoreChannel;
        }).collect(Collectors.toList());
    }

    public List<ScriptStoreChannel> getStoreChannelsByName(String name, boolean ignoreCase) {
        return internalGuild.getStoreChannelsByName(name, ignoreCase).stream().map(storeChannel -> {
            ScriptStoreChannel scriptStoreChannel = new ScriptStoreChannel(scriptContext);
            scriptStoreChannel.setInternalStoreChannel(storeChannel);
            return scriptStoreChannel;
        }).collect(Collectors.toList());
    }

    public ScriptTextChannel getSystemChannel() {
        ScriptTextChannel scriptTextChannel = new ScriptTextChannel(scriptContext);
        scriptTextChannel.setInternalTextChannel(internalGuild.getSystemChannel());
        return scriptTextChannel;
    }

    public ScriptTextChannel getTextChannelById(String id) {
        ScriptTextChannel scriptTextChannel = new ScriptTextChannel(scriptContext);
        scriptTextChannel.setInternalTextChannel(internalGuild.getTextChannelById(id));
        return scriptTextChannel;
    }

    public List<ScriptTextChannel> getTextChannels() {
        return internalGuild.getTextChannelCache().asList().stream().map(textChannel -> {
            ScriptTextChannel scriptTextChannel = new ScriptTextChannel(scriptContext);
            scriptTextChannel.setInternalTextChannel(textChannel);
            return scriptTextChannel;
        }).collect(Collectors.toList());
    }

    public List<ScriptTextChannel> getTextChannelsByName(String name, boolean ignoreCase) {
        return internalGuild.getTextChannelsByName(name, ignoreCase).stream().map(textChannel -> {
            ScriptTextChannel scriptTextChannel = new ScriptTextChannel(scriptContext);
            scriptTextChannel.setInternalTextChannel(textChannel);
            return scriptTextChannel;
        }).collect(Collectors.toList());
    }

    public String getVanityCode() {
        return internalGuild.getVanityCode();
    }

    public String getVanityUrl() {
        return internalGuild.getVanityUrl();
    }

    public ScriptVoiceChannel getVoiceChannelById(String id) {
        ScriptVoiceChannel scriptVoiceChannel = new ScriptVoiceChannel(scriptContext);
        scriptVoiceChannel.setInternalVoiceChannel(internalGuild.getVoiceChannelById(id));
        return scriptVoiceChannel;
    }

    public List<ScriptVoiceChannel> getVoiceChannels() {
        return internalGuild.getVoiceChannelCache().asList().stream().map(voiceChannel -> {
            ScriptVoiceChannel scriptVoiceChannel = new ScriptVoiceChannel(scriptContext);
            scriptVoiceChannel.setInternalVoiceChannel(voiceChannel);
            return scriptVoiceChannel;
        }).collect(Collectors.toList());
    }

    public List<ScriptVoiceChannel> getVoiceChannelsByName(String name, boolean ignoreCase) {
        return internalGuild.getVoiceChannelsByName(name, ignoreCase).stream().map(voiceChannel -> {
            ScriptVoiceChannel scriptVoiceChannel = new ScriptVoiceChannel(scriptContext);
            scriptVoiceChannel.setInternalVoiceChannel(voiceChannel);
            return scriptVoiceChannel;
        }).collect(Collectors.toList());
    }

    public List<ScriptGuildVoiceState> getVoiceStates() {
        return internalGuild.getVoiceStates().stream().map(guildVoiceState -> {
            ScriptGuildVoiceState scriptGuildVoiceState = new ScriptGuildVoiceState(scriptContext);
            scriptGuildVoiceState.setInternalVoiceState(guildVoiceState);
            return scriptGuildVoiceState;
        }).collect(Collectors.toList());
    }

    public Promise createTextChannel(String name) {
        CompletableFuture<TextChannel> channelCompletableFuture = internalGuild.createTextChannel(name).submit();
        return new Promise() {
            @NotNull
            @Override
            public Promise intThen(@NotNull Value resolve, @NotNull Value reject, @NotNull Value callback) {
                channelCompletableFuture.whenComplete((textChannel, throwable) -> {
                    if (throwable == null) {
                        ScriptTextChannel scriptTextChannel = new ScriptTextChannel(scriptContext);
                        scriptTextChannel.setInternalTextChannel(textChannel);
                        resolve.executeVoid(scriptTextChannel);
                    } else {
                        reject.executeVoid(throwable);
                    }
                    callback.executeVoid();
                });
                return this;
            }
        };
    }

    public Promise createTextChannel(ScriptCategory parent, String name) {
        return parent.createTextChannel(name);
    }

    public Promise createVoiceChannel(String name) {
        CompletableFuture<VoiceChannel> channelCompletableFuture = internalGuild.createVoiceChannel(name).submit();
        return new Promise() {
            @NotNull
            @Override
            public Promise intThen(@NotNull Value resolve, @NotNull Value reject, @NotNull Value callback) {
                channelCompletableFuture.whenComplete((voiceChannel, throwable) -> {
                    if (throwable == null) {
                        ScriptVoiceChannel scriptVoiceChannel = new ScriptVoiceChannel(scriptContext);
                        scriptVoiceChannel.setInternalVoiceChannel(voiceChannel);
                        resolve.executeVoid(scriptVoiceChannel);
                    } else {
                        reject.executeVoid(throwable);
                    }
                    callback.executeVoid();
                });
                return this;
            }
        };
    }

    public Promise createVoiceChanel(ScriptCategory parent, String name) {
        return parent.createVoiceChannel(name);
    }

    public Promise createCategory(String name) {
        CompletableFuture<Category> categoryCompletableFuture = internalGuild.createCategory(name).submit();
        return new Promise() {
            @NotNull
            @Override
            public Promise intThen(@NotNull Value resolve, @NotNull Value reject, @NotNull Value callback) {
                categoryCompletableFuture.whenComplete((category, throwable) -> {
                    if (throwable == null) {
                        ScriptCategory scriptCategory = new ScriptCategory(scriptContext);
                        scriptCategory.setInternalCategory(category);
                        resolve.executeVoid(scriptCategory);
                    } else {
                        reject.executeVoid(throwable);
                    }
                    callback.executeVoid();
                });
                return this;
            }
        };
    }

    public Promise createRole(String name) {
        CompletableFuture<Role> roleCompletableFuture = internalGuild.createRole().setName(name).submit();
        return new Promise() {
            @NotNull
            @Override
            public Promise intThen(@NotNull Value resolve, @NotNull Value reject, @NotNull Value callback) {
                roleCompletableFuture.whenComplete((role, throwable) -> {
                    if (throwable == null) {
                        ScriptRole scriptRole = new ScriptRole(scriptContext);
                        scriptRole.setInternalRole(role);
                        resolve.executeVoid(scriptRole);
                    } else {
                        reject.executeVoid(throwable);
                    }
                    callback.executeVoid();
                });
                return this;
            }
        };
    }

    public Promise createEmote(String name, String iconUrl) {
        Throwable thrown = null;
        CompletableFuture<Emote> emoteCompletableFuture = null;
        try {
            emoteCompletableFuture = internalGuild.createEmote(name, Icon.from(new URL(iconUrl).openStream())).submit();
        } catch (IOException e) {
            thrown = e;
        }
        Throwable finalThrown = thrown;
        CompletableFuture<Emote> finalEmoteCompletableFuture = emoteCompletableFuture;
        return new Promise() {
            @NotNull
            @Override
            public Promise intThen(@NotNull Value resolve, @NotNull Value reject, @NotNull Value callback) {
                if (finalThrown != null) {
                    reject.executeVoid(finalThrown);
                    callback.executeVoid();
                } else {
                    finalEmoteCompletableFuture.whenComplete((emote, throwable) -> {
                        if (throwable == null) {
                            ScriptEmote scriptEmote = new ScriptEmote(scriptContext);
                            scriptEmote.setInternalEmote(emote);
                            resolve.executeVoid(scriptEmote);
                        } else {
                            reject.executeVoid(throwable);
                        }
                        callback.executeVoid();
                    });
                }
                return this;
            }
        };
    }

    public Promise setName(String name) {
        CompletableFuture<Void> voidCompletableFuture = internalGuild.getManager().setName(name).submit();
        return scriptContext.handleVoidCompletableFuture(voidCompletableFuture);
    }

    public Promise setIcon(String iconUrl) {
        Throwable thrown = null;
        CompletableFuture<Void> voidCompletableFuture = null;
        try {
            voidCompletableFuture = internalGuild.getManager().setIcon(Icon.from(new URL(iconUrl).openStream())).submit();
        } catch (IOException e) {
            thrown = e;
        }
        Throwable finalThrown = thrown;
        CompletableFuture<Void> finalVoidCompletableFuture = voidCompletableFuture;
        return new Promise() {
            @NotNull
            @Override
            public Promise intThen(@NotNull Value resolve, @NotNull Value reject, @NotNull Value callback) {
                if (finalThrown != null) {
                    reject.executeVoid(finalThrown);
                    callback.executeVoid();
                } else {
                    finalVoidCompletableFuture.whenComplete((unused, throwable) -> {
                        if (throwable == null) {
                            resolve.executeVoid(true);
                        } else {
                            reject.executeVoid(throwable);
                        }
                        callback.executeVoid();
                    });
                }
                return this;
            }
        };
    }

    public Promise setSplash(String splashUrl) {
        Throwable thrown = null;
        CompletableFuture<Void> voidCompletableFuture = null;
        try {
            voidCompletableFuture = internalGuild.getManager().setSplash(Icon.from(new URL(splashUrl).openStream())).submit();
        } catch (IOException e) {
            thrown = e;
        }
        Throwable finalThrown = thrown;
        CompletableFuture<Void> finalVoidCompletableFuture = voidCompletableFuture;
        return new Promise() {
            @NotNull
            @Override
            public Promise intThen(@NotNull Value resolve, @NotNull Value reject, @NotNull Value callback) {
                if (finalThrown != null) {
                    reject.executeVoid(finalThrown);
                    callback.executeVoid();
                } else {
                    finalVoidCompletableFuture.whenComplete((unused, throwable) -> {
                        if (throwable == null) {
                            resolve.executeVoid(true);
                        } else {
                            reject.executeVoid(throwable);
                        }
                        callback.executeVoid();
                    });
                }
                return this;
            }
        };
    }

    public Promise setBanner(String bannerUrl) {
        Throwable thrown = null;
        CompletableFuture<Void> voidCompletableFuture = null;
        try {
            voidCompletableFuture = internalGuild.getManager().setBanner(Icon.from(new URL(bannerUrl).openStream())).submit();
        } catch (IOException e) {
            thrown = e;
        }
        Throwable finalThrown = thrown;
        CompletableFuture<Void> finalVoidCompletableFuture = voidCompletableFuture;
        return new Promise() {
            @NotNull
            @Override
            public Promise intThen(@NotNull Value resolve, @NotNull Value reject, @NotNull Value callback) {
                if (finalThrown != null) {
                    reject.executeVoid(finalThrown);
                    callback.executeVoid();
                } else {
                    finalVoidCompletableFuture.whenComplete((unused, throwable) -> {
                        if (throwable == null) {
                            resolve.executeVoid(true);
                        } else {
                            reject.executeVoid(throwable);
                        }
                        callback.executeVoid();
                    });
                }
                return this;
            }
        };
    }

    public Promise setDescription(String description) {
        CompletableFuture<Void> voidCompletableFuture = internalGuild.getManager().setDescription(description).submit();
        return scriptContext.handleVoidCompletableFuture(voidCompletableFuture);
    }

    public Promise setRequiredMFALevel(Guild.MFALevel mfaLevel) {
        CompletableFuture<Void> voidCompletableFuture = internalGuild.getManager().setRequiredMFALevel(mfaLevel).submit();
        return scriptContext.handleVoidCompletableFuture(voidCompletableFuture);
    }

    public Promise setDefaultNotificationLevel(Guild.NotificationLevel notificationLevel) {
        CompletableFuture<Void> voidCompletableFuture = internalGuild.getManager().setDefaultNotificationLevel(notificationLevel).submit();
        return scriptContext.handleVoidCompletableFuture(voidCompletableFuture);
    }

    protected void setInternalGuild(Guild guild) {
        this.internalGuild = guild;
        this.internalSnowflake = internalGuild;
    }

}
