package org.cascadebot.cascadebot.scripting.objects;

import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.Category;
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

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ScriptGuild {

    public Guild internalGuild;

    public ScriptGuild() {

    }

    public GuildChannel getAfkChannel() {
        return internalGuild.getAfkChannel();
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

    public Integer getBoostCount() {
        return internalGuild.getBoostCount();
    }

    public List<Member> getBoosters() {
        return internalGuild.getBoosters();
    }

    public Guild.BoostTier getBoostTier() {
        return internalGuild.getBoostTier();
    }

    public List<Category> getCategories() {
        return internalGuild.getCategories();
    }

    public List<Category> getCategoriesByName(String name, boolean ignoreCase) {
        return internalGuild.getCategoriesByName(name, ignoreCase);
    }

    public Category getCategoryById(String id) {
        return internalGuild.getCategoryById(id);
    }

    public List<GuildChannel> getChannels() {
        return internalGuild.getChannels();
    }

    public TextChannel getDefaultChannel() {
        return internalGuild.getDefaultChannel();
    }

    public Guild.NotificationLevel getDefaultNotificationLevel() {
        return internalGuild.getDefaultNotificationLevel();
    }

    public String getDescription() {
        return internalGuild.getDescription();
    }

    public Emote getEmoteById(String id) {
        return internalGuild.getEmoteById(id);
    }

    public List<Emote> getEmotes() {
        return internalGuild.getEmoteCache().asList();
    }

    public List<Emote> getEmotesByName(String name, boolean ignoreCase) {
        return internalGuild.getEmotesByName(name, ignoreCase);
    }

    public Guild.ExplicitContentLevel getExplicitContentLevel() {
        return internalGuild.getExplicitContentLevel();
    }

    public Set<String> getFeatures() {
        return internalGuild.getFeatures();
    }

    public GuildChannel getGuildChannelById(String id) {
        return internalGuild.getGuildChannelById(id);
    }

    public GuildChannel getGuildChannelById(ChannelType type, String id) {
        return internalGuild.getGuildChannelById(type, id);
    }

    public String getIconId() {
        return internalGuild.getIconId();
    }

    public String getIconUrl() {
        return internalGuild.getIconUrl();
    }

    public Integer getMaxBitrate() {
        return internalGuild.getMaxBitrate();
    }

    public Integer getMaxEmotes() {
        return internalGuild.getMaxEmotes();
    }

    public Integer getMaxMembers() {
        return internalGuild.getMaxMembers();
    }

    public Integer getMaxPresences() {
        return internalGuild.getMaxPresences();
    }

    public Member getMember(User user) {
        return internalGuild.getMember(user);
    }

    public Member getMemberById(String id) {
        return internalGuild.getMemberById(id);
    }

    public Member getMemberByTag(String tag) {
        return internalGuild.getMemberByTag(tag);
    }

    public Member getMemberByTag(String username, String discriminator) {
        return internalGuild.getMemberByTag(username, discriminator);
    }

    public MemberCacheView getMembers() {
        return internalGuild.getMemberCache();
    }

    public List<Member> getMembersByEffectiveName(String name, boolean ignoreCase) {
        return internalGuild.getMembersByEffectiveName(name, ignoreCase);
    }

    public List<Member> getMembersByName(String name, boolean ignoreCase) {
        return internalGuild.getMembersByName(name, ignoreCase);
    }

    public List<Member> getMembersByNickname(String name, boolean ignoreCase) {
        return internalGuild.getMembersByNickname(name, ignoreCase);
    }

    public List<Member> getMembersWithRoles(Collection<Role> roles) {
        return internalGuild.getMembersWithRoles(roles);
    }

    public String getName() {
        return internalGuild.getName();
    }

    public Member getOwner() {
        return internalGuild.getOwner();
    }

    public String getOwnerId() {
        return internalGuild.getOwnerId();
    }

    public Role getPublicRole() {
        return internalGuild.getPublicRole();
    }

    public Region getRegion() {
        return internalGuild.getRegion();
    }

    public Guild.MFALevel getRequiredMFALevel() {
        return internalGuild.getRequiredMFALevel();
    }

    public Role getRoleById(String id) {
        return internalGuild.getRoleById(id);
    }

    public SortedSnowflakeCacheView<Role> getRoles() {
        return internalGuild.getRoleCache();
    }

    public List<Role> getRolesByName(String name, boolean ignoreCase) {
        return internalGuild.getRolesByName(name, ignoreCase);
    }

    public String getSplashId() {
        return internalGuild.getSplashId();
    }

    public String getSplashUrl() {
        return internalGuild.getSplashUrl();
    }

    public StoreChannel getStoreChannelById(String id) {
        return internalGuild.getStoreChannelById(id);
    }

    public List<StoreChannel> getStoreChannels() {
        return internalGuild.getStoreChannels();
    }

    public List<StoreChannel> getStoreChannelsByName(String name, boolean ignoreCase) {
        return internalGuild.getStoreChannelsByName(name, ignoreCase);
    }

    public TextChannel getSystemChannel() {
        return internalGuild.getSystemChannel();
    }

    public TextChannel getTextChannelById(String id) {
        return internalGuild.getTextChannelById(id);
    }

    public List<TextChannel> getTextChannels() {
        return internalGuild.getTextChannelCache().asList();
    }

    public List<TextChannel> getTextChannelsByName(String name, boolean ignoreCase) {
        return internalGuild.getTextChannelsByName(name, ignoreCase);
    }

    public String getVanityCode() {
        return internalGuild.getVanityCode();
    }

    public String getVanityUrl() {
        return internalGuild.getVanityUrl();
    }

    public VoiceChannel getVoiceChannelById(String id) {
        return internalGuild.getVoiceChannelById(id);
    }

    public List<VoiceChannel> getVoiceChannels() {
        return internalGuild.getVoiceChannelCache().asList();
    }

    public List<VoiceChannel> getVoiceChannelsByName(String name, boolean ignoreCase) {
        return internalGuild.getVoiceChannelsByName(name, ignoreCase);
    }

    public List<GuildVoiceState> getVoiceStates() {
        return internalGuild.getVoiceStates();
    }

    public boolean isMember(User user) {
        return internalGuild.isMember(user);
    }

}
