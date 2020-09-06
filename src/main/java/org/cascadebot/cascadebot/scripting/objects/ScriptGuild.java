package org.cascadebot.cascadebot.scripting.objects;

import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.StoreChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.utils.cache.MemberCacheView;
import net.dv8tion.jda.api.utils.cache.SortedSnowflakeCacheView;
import org.cascadebot.cascadebot.scripting.objects.channel.ScriptCategory;
import org.cascadebot.cascadebot.scripting.objects.channel.ScriptChannel;
import org.cascadebot.cascadebot.scripting.objects.channel.ScriptStoreChannel;
import org.cascadebot.cascadebot.scripting.objects.channel.ScriptTextChannel;
import org.cascadebot.cascadebot.scripting.objects.channel.ScriptVoiceChannel;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ScriptGuild extends ScriptSnowflake {

    private Guild internalGuild;

    public ScriptGuild() {

    }

    public ScriptVoiceChannel getAfkChannel() {
        //return internalGuild.getAfkChannel();
        return null;
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
            ScriptUser scriptUser = new ScriptUser();
            scriptUser.setInternalUser(member);
            return scriptUser;
        })).collect(Collectors.toList());
    }

    public Guild.BoostTier getBoostTier() {
        return internalGuild.getBoostTier();
    }

    public List<ScriptCategory> getCategories() {
        return internalGuild.getCategories().stream().map((category -> {
            ScriptCategory scriptCategory = new ScriptCategory();
            scriptCategory.setInternalCategory(category);
            return scriptCategory;
        })).collect(Collectors.toList());
    }

    public List<ScriptCategory> getCategoriesByName(String name, boolean ignoreCase) {
        return internalGuild.getCategoriesByName(name, ignoreCase).stream().map((category -> {
            ScriptCategory scriptCategory = new ScriptCategory();
            scriptCategory.setInternalCategory(category);
            return scriptCategory;
        })).collect(Collectors.toList());
    }

    public ScriptCategory getCategoryById(String id) {
        ScriptCategory scriptCategory = new ScriptCategory();
        scriptCategory.setInternalCategory(internalGuild.getCategoryById(id));
        return scriptCategory;
    }

    public List<ScriptChannel> getChannels() {
        return internalGuild.getChannels().stream().map(ScriptChannel::fromJda).collect(Collectors.toList());
    }

    public ScriptTextChannel getDefaultChannel() {
        ScriptTextChannel scriptTextChannel = new ScriptTextChannel();
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
        ScriptEmote scriptEmote = new ScriptEmote();
        scriptEmote.setInternalEmote(internalGuild.getEmoteById(id));
        return scriptEmote;
    }

    public List<ScriptEmote> getEmotes() {
        return internalGuild.getEmoteCache().asList().stream().map(emote -> {
            ScriptEmote scriptEmote = new ScriptEmote();
            scriptEmote.setInternalEmote(emote);
            return scriptEmote;
        }).collect(Collectors.toList());
    }

    public List<ScriptEmote> getEmotesByName(String name, boolean ignoreCase) {
        return internalGuild.getEmotesByName(name, ignoreCase).stream().map(emote -> {
            ScriptEmote scriptEmote = new ScriptEmote();
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
        return ScriptChannel.fromJda(internalGuild.getGuildChannelById(id));
    }

    public ScriptChannel getGuildChannelById(ChannelType type, String id) {
        return ScriptChannel.fromJda(internalGuild.getGuildChannelById(type, id));
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
        ScriptUser scriptUser = new ScriptUser();
        scriptUser.setInternalUser(internalGuild.getMemberById(id));
        return scriptUser;
    }

    public ScriptUser getUserByTag(String tag) {
        ScriptUser scriptUser = new ScriptUser();
        scriptUser.setInternalUser(internalGuild.getMemberByTag(tag));
        return scriptUser;
    }

    public ScriptUser getUserByTag(String username, String discriminator) {
        ScriptUser scriptUser = new ScriptUser();
        scriptUser.setInternalUser(internalGuild.getMemberByTag(username, discriminator));
        return scriptUser;
    }

    // TODO figure out how to re-do this
    /*public MemberCacheView getMembers() {
        return internalGuild.getMemberCache();
    }*/

    public List<ScriptUser> getUsersByEffectiveName(String name, boolean ignoreCase) {
        return internalGuild.getMembersByEffectiveName(name, ignoreCase).stream().map(member -> {
            ScriptUser scriptUser = new ScriptUser();
            scriptUser.setInternalUser(member);
            return scriptUser;
        }).collect(Collectors.toList());
    }

    public List<ScriptUser> getUsersByName(String name, boolean ignoreCase) {
        return internalGuild.getMembersByName(name, ignoreCase).stream().map(member -> {
            ScriptUser scriptUser = new ScriptUser();
            scriptUser.setInternalUser(member);
            return scriptUser;
        }).collect(Collectors.toList());
    }

    public List<ScriptUser> getUsersByNickname(String name, boolean ignoreCase) {
        return internalGuild.getMembersByNickname(name, ignoreCase).stream().map(member -> {
            ScriptUser scriptUser = new ScriptUser();
            scriptUser.setInternalUser(member);
            return scriptUser;
        }).collect(Collectors.toList());
    }

    public List<ScriptUser> getUsersWithRoles(Collection<ScriptRole> roles) {
        return internalGuild.getMembersWithRoles(roles.stream().map(scriptRole -> scriptRole.internalRole)
                .collect(Collectors.toList())).stream().map(member -> {
            ScriptUser scriptUser = new ScriptUser();
            scriptUser.setInternalUser(member);
            return scriptUser;
        }).collect(Collectors.toList());
    }

    public String getName() {
        return internalGuild.getName();
    }

    public ScriptUser getOwner() {
        ScriptUser scriptUser = new ScriptUser();
        scriptUser.setInternalUser(internalGuild.getOwner());
        return scriptUser;
    }

    public String getOwnerId() {
        return internalGuild.getOwnerId();
    }

    public ScriptRole getPublicRole() {
        ScriptRole scriptRole = new ScriptRole();
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
        ScriptRole scriptRole = new ScriptRole();
        scriptRole.setInternalRole(internalGuild.getRoleById(id));
        return scriptRole;
    }

    // TODO figure out how to re-do this
    /*public SortedSnowflakeCacheView<Role> getRoles() {
        return internalGuild.getRoleCache();
    }*/

    public List<ScriptRole> getRolesByName(String name, boolean ignoreCase) {
        return internalGuild.getRolesByName(name, ignoreCase).stream().map(role -> {
            ScriptRole scriptRole = new ScriptRole();
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
        ScriptStoreChannel scriptStoreChannel = new ScriptStoreChannel();
        scriptStoreChannel.setInternalStoreChannel(internalGuild.getStoreChannelById(id));
        return scriptStoreChannel;
    }

    public List<ScriptStoreChannel> getStoreChannels() {
        return internalGuild.getStoreChannels().stream().map(storeChannel -> {
            ScriptStoreChannel scriptStoreChannel = new ScriptStoreChannel();
            scriptStoreChannel.setInternalStoreChannel(storeChannel);
            return scriptStoreChannel;
        }).collect(Collectors.toList());
    }

    public List<ScriptStoreChannel> getStoreChannelsByName(String name, boolean ignoreCase) {
        return internalGuild.getStoreChannelsByName(name, ignoreCase).stream().map(storeChannel -> {
            ScriptStoreChannel scriptStoreChannel = new ScriptStoreChannel();
            scriptStoreChannel.setInternalStoreChannel(storeChannel);
            return scriptStoreChannel;
        }).collect(Collectors.toList());
    }

    public ScriptTextChannel getSystemChannel() {
        ScriptTextChannel scriptTextChannel = new ScriptTextChannel();
        scriptTextChannel.setInternalTextChannel(internalGuild.getSystemChannel());
        return scriptTextChannel;
    }

    public ScriptTextChannel getTextChannelById(String id) {
        ScriptTextChannel scriptTextChannel = new ScriptTextChannel();
        scriptTextChannel.setInternalTextChannel(internalGuild.getTextChannelById(id));
        return scriptTextChannel;
    }

    public List<ScriptTextChannel> getTextChannels() {
        return internalGuild.getTextChannelCache().asList().stream().map(textChannel -> {
            ScriptTextChannel scriptTextChannel = new ScriptTextChannel();
            scriptTextChannel.setInternalTextChannel(textChannel);
            return scriptTextChannel;
        }).collect(Collectors.toList());
    }

    public List<ScriptTextChannel> getTextChannelsByName(String name, boolean ignoreCase) {
        return internalGuild.getTextChannelsByName(name, ignoreCase).stream().map(textChannel -> {
            ScriptTextChannel scriptTextChannel = new ScriptTextChannel();
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
        ScriptVoiceChannel scriptVoiceChannel = new ScriptVoiceChannel();
        scriptVoiceChannel.setInternalVoiceChannel(internalGuild.getVoiceChannelById(id));
        return scriptVoiceChannel;
    }

    public List<ScriptVoiceChannel> getVoiceChannels() {
        return internalGuild.getVoiceChannelCache().asList().stream().map(voiceChannel -> {
            ScriptVoiceChannel scriptVoiceChannel = new ScriptVoiceChannel();
            scriptVoiceChannel.setInternalVoiceChannel(voiceChannel);
            return scriptVoiceChannel;
        }).collect(Collectors.toList());
    }

    public List<ScriptVoiceChannel> getVoiceChannelsByName(String name, boolean ignoreCase) {
        return internalGuild.getVoiceChannelsByName(name, ignoreCase).stream().map(voiceChannel -> {
            ScriptVoiceChannel scriptVoiceChannel = new ScriptVoiceChannel();
            scriptVoiceChannel.setInternalVoiceChannel(voiceChannel);
            return scriptVoiceChannel;
        }).collect(Collectors.toList());
    }

    public List<GuildVoiceState> getVoiceStates() {
        return internalGuild.getVoiceStates();
    }

    protected void setInternalGuild(Guild guild) {
        this.internalGuild = guild;
        this.internalSnowflake = internalGuild;
    }

}
