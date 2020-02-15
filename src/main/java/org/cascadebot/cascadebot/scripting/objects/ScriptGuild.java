package org.cascadebot.cascadebot.scripting.objects;

import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import org.bukkit.block.data.type.Snow;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.util.List;

@AllArgsConstructor
public class ScriptGuild extends ScriptableObject {

    private final Guild internalGuild;

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

    public void getCategoryById(String id) {
        /* Category */
    }

    public void getChannels() {
        /* Use getChannelCache internally */
        /* List<GuildChannel> */
    }

    public void getDefaultChannel() {
        /* TextChannel */
    }

    public void getDefaultNotificationLevel() {
        /* Guild.NotificationLevel */
    }

    public void getDescription() {
        /* String */
    }

    public void getEmoteById(String id) {
        /* Emote */
    }

    public void getEmotes() {
        /* Use getEmoteCache internally */
        /* List<Emote> */
    }

    public void getEmotesByName(String name, boolean ignoreCase) {
        /* List<Emote> */
    }

    public void getExplicitContentLevel() {
        /* Guild.ExplicitContentLevel */
    }

    public void getFeatures() {
        /* Set<String> */
    }

    public void getGuildChannelById(String id) {
        /* GuildChannel */
    }

    public void getGuildChannelById(ChannelType type, String id) {
        /* GuildChannel */
    }

    public void getIconId() {
        /* String */
    }

    public void getIconUrl() {
        /* String */
    }

    public void getMaxBitrate() {
        /* Integer */
    }

    public void getMaxEmotes() {
        /* Integer */
    }

    public void getMaxMembers() {
        /* Integer */
    }

    public void getMaxPresences() {
        /* Integer */
    }

    public void getMember(ScriptUser user) {
        /* Member */
    }

    public void getMemberById(String id) {
        /* Member */
    }

    public void getMemberByTag(String tag) {
        /* Member */
    }

    public void getMemberByTag(String username, String discriminator) {
        /* Member */
    }

    public void getMembers() {
        /* Use getMemberCache internally */
        /* List<Member> */
    }

    public void getMembersByEffectiveName(String name, boolean ignoreCase) {
        /* List<Member> */
    }

    public void getMembersByName(String name, boolean ignoreCase) {
        /* List<Member> */
    }

    public void getMembersByNickname(String name, boolean ignoreCase) {
        /* List<Member> */
    }

    public void getMembersWithRoles(List<String> roles) {
        /* List<Member> */
    }

    public void getMembersWithRoles(/*List<Role> roles*/) {
        /* List<Member> */
    }

    public void getName() {
        /* String */
    }

    public void getOwner() {
        /* Member */
    }

    public void getOwnerId() {
        /* String */
    }

    public void getPublicRole() {
        /* Role */
    }

    public void getRegion() {
        /* Region */
    }

    public void getRequiredMFALevel() {
        /* Guild.MFALevel */
    }

    public void getRoleById(String id) {
        /* Role */
    }

    public void getRoles() {
        /* Use getRoleCache internally */
        /* List<Role> */
    }

    public void getRolesByName(String name, boolean ignoreCase) {
        /* List<Role> */
    }

    public void getSplashId() {
        /* String */
    }

    public void getSplashUrl() {
        /* String */
    }

    public void getStoreChannelById(String id) {
        /* StoreChannel */
    }

    public void getStoreChannels() {
        /* Use cache internally
        * List<StoreChannel> */
    }

    public void getStoreChannelsByName(String name, boolean ignoreCase) {
        /* Use cache internally
        * List<StoreChannel> */
    }

    public void getSystemChannel() {
        /* TextChannel */
    }

    public void getTextChannelById(String id) {
        /* TextChannel */
    }

    public void getTextChannels() {
        /* Use cache internally
        * List<TextChannel> */
    }

    public void getTextChannelsByName(String name, boolean ignoreCase) {
        /* Use cache internally
         * List<TextChannel> */
    }

    public void getVanityCode() {
        /* String */
    }

    public void getVanityUrl() {
        /* String */
    }

    public void getVoiceChannelById(String id) {
        /* VoiceChannel */
    }

    public void getVoiceChannels() {
        /* Use cache internally
        List<VoiceChannel> */
    }

    public void getVoiceChannelsByName(String name, boolean ignoreCase) {
        /* Use cache internally
        List<VoiceChannel> */
    }

    public void getVoiceStates() {
        /* List<GuildVoiceStates> */
    }

    public void isMember(ScriptUser user) {
        /* boolean */
    }

    @Override
    public String getClassName() {
        return "Guild";
    }

}
