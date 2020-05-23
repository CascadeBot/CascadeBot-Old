package org.cascadebot.cascadebot.events;

import club.minnced.discord.webhook.send.WebhookEmbed;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogChange;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.channel.category.CategoryCreateEvent;
import net.dv8tion.jda.api.events.channel.category.CategoryDeleteEvent;
import net.dv8tion.jda.api.events.channel.category.GenericCategoryEvent;
import net.dv8tion.jda.api.events.channel.category.update.CategoryUpdateNameEvent;
import net.dv8tion.jda.api.events.channel.category.update.CategoryUpdatePermissionsEvent;
import net.dv8tion.jda.api.events.channel.category.update.CategoryUpdatePositionEvent;
import net.dv8tion.jda.api.events.channel.store.GenericStoreChannelEvent;
import net.dv8tion.jda.api.events.channel.store.StoreChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.store.StoreChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.store.update.StoreChannelUpdateNameEvent;
import net.dv8tion.jda.api.events.channel.store.update.StoreChannelUpdatePermissionsEvent;
import net.dv8tion.jda.api.events.channel.store.update.StoreChannelUpdatePositionEvent;
import net.dv8tion.jda.api.events.channel.text.GenericTextChannelEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.text.update.TextChannelUpdateNameEvent;
import net.dv8tion.jda.api.events.channel.text.update.TextChannelUpdatePermissionsEvent;
import net.dv8tion.jda.api.events.channel.text.update.TextChannelUpdatePositionEvent;
import net.dv8tion.jda.api.events.channel.voice.GenericVoiceChannelEvent;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.voice.update.VoiceChannelUpdateNameEvent;
import net.dv8tion.jda.api.events.channel.voice.update.VoiceChannelUpdatePermissionsEvent;
import net.dv8tion.jda.api.events.channel.voice.update.VoiceChannelUpdatePositionEvent;
import net.dv8tion.jda.api.events.emote.EmoteAddedEvent;
import net.dv8tion.jda.api.events.emote.EmoteRemovedEvent;
import net.dv8tion.jda.api.events.emote.GenericEmoteEvent;
import net.dv8tion.jda.api.events.emote.update.EmoteUpdateNameEvent;
import net.dv8tion.jda.api.events.emote.update.EmoteUpdateRolesEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GenericGuildMemberEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.guild.update.GenericGuildUpdateEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateAfkChannelEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateAfkTimeoutEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBannerEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateDescriptionEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateExplicitContentLevelEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateFeaturesEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateIconEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateMFALevelEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateMaxMembersEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateMaxPresencesEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNotificationLevelEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateRegionEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateSplashEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateSystemChannelEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateVanityCodeEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateVerificationLevelEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.events.role.GenericRoleEvent;
import net.dv8tion.jda.api.events.role.RoleCreateEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateColorEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateHoistedEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateMentionableEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateNameEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdatePermissionsEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdatePositionEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateDiscriminatorEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.data.Config;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.data.objects.GuildData;
import org.cascadebot.cascadebot.data.objects.ModlogEventStore;
import org.cascadebot.cascadebot.moderation.ModlogEvent;
import org.cascadebot.cascadebot.utils.CryptUtils;
import org.cascadebot.cascadebot.utils.FormatUtils;
import org.cascadebot.cascadebot.utils.LanguageEmbedField;
import org.jetbrains.annotations.NotNull;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ModlogEventListener extends ListenerAdapter {

    //TODO move everything over to using language paths instead of hard coded string
    public void onGenericEmote(GenericEmoteEvent event) {
        GuildData guildData = GuildDataManager.getGuildData(event.getGuild().getIdLong());
        Emote emote = event.getEmote();
        event.getGuild().retrieveAuditLogs().limit(1).queue(auditLogEntries -> {
            AuditLogEntry entry = auditLogEntries.get(0);
            User user = null;
            if (entry.getType().equals(ActionType.EMOTE_UPDATE) || entry.getType().equals(ActionType.EMOTE_CREATE) || entry.getType().equals(ActionType.EMOTE_DELETE)) {
                user = auditLogEntries.get(0).getUser();
            }
            List<LanguageEmbedField> embedFieldList = new ArrayList<>();
            ModlogEvent modlogEvent;
            if (event instanceof EmoteAddedEvent) {
                modlogEvent = ModlogEvent.EMOTE_CREATED;
            } else if (event instanceof EmoteRemovedEvent) {
                modlogEvent = ModlogEvent.EMOTE_DELETED;
            } else if (event instanceof EmoteUpdateNameEvent) {
                modlogEvent = ModlogEvent.EMOTE_UPDATED_NAME;
                LanguageEmbedField languageEmbedField = new LanguageEmbedField(true, "modlog.general.old_name", "modlog.general.variable");
                languageEmbedField.addValueObjects(((EmoteUpdateNameEvent) event).getOldName());
                embedFieldList.add(languageEmbedField
                        //new WebhookEmbed.EmbedField(true, "Old Name", ((EmoteUpdateNameEvent) event).getOldName())
                );
            } else if (event instanceof EmoteUpdateRolesEvent) {
                modlogEvent = ModlogEvent.EMOTE_UPDATED_ROLES;
                List<Role> oldRoles = ((EmoteUpdateRolesEvent) event).getOldRoles();
                List<Role> newRoles = ((EmoteUpdateRolesEvent) event).getNewRoles();
                ListChanges<Role> roleListChanges = new ListChanges<>(oldRoles, newRoles);
                LanguageEmbedField addedRolesEmbed = new LanguageEmbedField(false, "modlog.general.added_roles", "modlog.general.variable");
                addedRolesEmbed.addValueObjects(roleListChanges.getAdded().stream().map(role -> role.getName() + " (" + role.getId() + ")")
                        .collect(Collectors.joining("\n")));
                LanguageEmbedField removedRolesEmbed = new LanguageEmbedField(false, "modlog.general.removed_roles", "modlog.general.variable");
                removedRolesEmbed.addValueObjects( roleListChanges.getRemoved().stream().map(role -> role.getName() + " (" + role.getId() + ")")
                        .collect(Collectors.joining("\n")));
                embedFieldList.add(addedRolesEmbed);
                embedFieldList.add(removedRolesEmbed);
            } else {
                return;
            }
            ModlogEventStore eventStore = new ModlogEventStore(modlogEvent, user, emote, embedFieldList);
            guildData.getModeration().sendModlogEvent(eventStore);
        });
    }

    public void onGenericGuildMember(GenericGuildMemberEvent event) {
        GuildData guildData = GuildDataManager.getGuildData(event.getGuild().getIdLong());
        User user = event.getMember().getUser();
        event.getGuild().retrieveAuditLogs().limit(1).queue(auditLogEntries -> {
            AuditLogEntry entry = auditLogEntries.get(0);
            List<LanguageEmbedField> embedFieldList = new ArrayList<>();
            ModlogEvent modlogEvent;
            User responsible = null;
            if (event instanceof GuildMemberJoinEvent) {
                modlogEvent = ModlogEvent.GUILD_MEMBER_JOINED;
            } else if (event instanceof GuildMemberLeaveEvent) {
                if (entry.getType().equals(ActionType.KICK)) {
                    LanguageEmbedField respLanguageEmbedField = new LanguageEmbedField(true, "modlog.general.responsible", "modlog.general.variable");
                    respLanguageEmbedField.addValueObjects(Objects.requireNonNull(entry.getUser()).getAsTag());
                    embedFieldList.add(respLanguageEmbedField);
                    if (entry.getReason() != null) {
                        LanguageEmbedField reasonEmbedField = new LanguageEmbedField(false, "modlog.general.reason", "modlog.general.variable");
                        respLanguageEmbedField.addValueObjects(entry.getReason());
                        embedFieldList.add(reasonEmbedField);
                    }
                    modlogEvent = ModlogEvent.GUILD_MEMBER_KICKED;
                } else { //TODO not assume leave if audit log entry for kick was not found.
                    modlogEvent = ModlogEvent.GUILD_MEMBER_LEFT;
                }
            } else if (event instanceof GuildMemberRoleAddEvent) {
                if (entry.getType().equals(ActionType.MEMBER_ROLE_UPDATE)) {
                    responsible = entry.getUser();
                }
                modlogEvent = ModlogEvent.GUILD_MEMBER_ROLE_ADDED;
                LanguageEmbedField addedRolesEmbedField = new LanguageEmbedField(false, "modlog.general.added_roles", "modlog.general.variable");
                addedRolesEmbedField.addValueObjects(((GuildMemberRoleAddEvent) event).getRoles().stream().map(role -> role.getName() + " (" + role.getId() + ")").collect(Collectors.joining("\n")));
                embedFieldList.add(addedRolesEmbedField);
            } else if (event instanceof GuildMemberRoleRemoveEvent) {
                if (entry.getType().equals(ActionType.MEMBER_ROLE_UPDATE)) {
                    responsible = entry.getUser();
                }
                modlogEvent = ModlogEvent.GUILD_MEMBER_ROLE_REMOVED;
                LanguageEmbedField removedRolesEmbedField = new LanguageEmbedField(false, "modlog.general.removed_roles", "modlog.general.variable");
                removedRolesEmbedField.addValueObjects(((GuildMemberRoleRemoveEvent) event).getRoles().stream().map(role -> role.getName() + " (" + role.getId() + ")").collect(Collectors.joining("\n")));
                embedFieldList.add(removedRolesEmbedField);
            } else if (event instanceof GuildMemberUpdateNicknameEvent) {
                if (((GuildMemberUpdateNicknameEvent) event).getOldValue() != null) {
                    LanguageEmbedField oldNickEmbedField = new LanguageEmbedField(true, "modlog.member.old_nick", "modlog.general.variable");
                    oldNickEmbedField.addValueObjects(((GuildMemberUpdateNicknameEvent) event).getOldValue());
                    embedFieldList.add(oldNickEmbedField);
                }
                if (((GuildMemberUpdateNicknameEvent) event).getNewValue() != null) {
                    LanguageEmbedField newNickEmbedField = new LanguageEmbedField(true, "modlog.member.new_nick", "modlog.general.variable");
                    newNickEmbedField.addValueObjects(((GuildMemberUpdateNicknameEvent) event).getNewValue());
                    embedFieldList.add(newNickEmbedField);
                }
                modlogEvent = ModlogEvent.GUILD_MEMBER_NICKNAME_UPDATED;
            } else {
                return;
            }
            ModlogEventStore eventStore = new ModlogEventStore(modlogEvent, responsible, user, embedFieldList);
            guildData.getModeration().sendModlogEvent(eventStore);
        });
    }

    //region Ban events
    public void onGuildBan(GuildBanEvent event) {
        GuildData guildData = GuildDataManager.getGuildData(event.getGuild().getIdLong());
        User user = event.getUser();
        event.getGuild().retrieveAuditLogs().limit(1).queue(auditLogEntries -> {
            AuditLogEntry entry = auditLogEntries.get(0);
            List<LanguageEmbedField> embedFieldList = new ArrayList<>();
            ModlogEvent modlogEvent = ModlogEvent.GUILD_USER_BANNED;
            User responsible = null;
            if (entry.getType().equals(ActionType.BAN)) {
                responsible = entry.getUser();
                if (entry.getReason() != null) {
                    LanguageEmbedField reasonEmbedField = new LanguageEmbedField(false, "modlog.general.reason", "modlog.general.variable");
                    reasonEmbedField.addValueObjects(entry.getReason());
                    embedFieldList.add(reasonEmbedField);
                }
            }
            ModlogEventStore eventStore = new ModlogEventStore(modlogEvent, responsible, user, embedFieldList);
            guildData.getModeration().sendModlogEvent(eventStore);
        });
    }

    public void onGuildUnban(GuildUnbanEvent event) {
        GuildData guildData = GuildDataManager.getGuildData(event.getGuild().getIdLong());
        User user = event.getUser();
        event.getGuild().retrieveAuditLogs().limit(1).queue(auditLogEntries -> {
            AuditLogEntry entry = auditLogEntries.get(0);
            List<LanguageEmbedField> embedFieldList = new ArrayList<>();
            ModlogEvent modlogEvent = ModlogEvent.GUILD_USER_UNBANNED;
            User responsible = null;
            if (entry.getType().equals(ActionType.UNBAN)) {
                responsible = entry.getUser();
            }
            ModlogEventStore eventStore = new ModlogEventStore(modlogEvent, responsible, user, embedFieldList);
            guildData.getModeration().sendModlogEvent(eventStore);
        });
    }
    //endregion

    //region Message
    public void onGuildMessageDelete(GuildMessageDeleteEvent event) {
        GuildData guildData = GuildDataManager.getGuildData(event.getGuild().getIdLong());
        String messageID = event.getMessageId();
        String messageJson = CascadeBot.INS.getRedisClient().get("message:" + messageID);
        CascadeBot.INS.getRedisClient().del("message:" + messageID);
        JsonObject jsonObject = new JsonParser().parse(messageJson).getAsJsonObject();
        long messageSender = jsonObject.get("sender").getAsLong();
        User affected = CascadeBot.INS.getClient().getUserById(messageSender);
        List<LanguageEmbedField> embedFieldList = new ArrayList<>();
        LanguageEmbedField messageEmbedField = new LanguageEmbedField(false, "modlog.message.message", "modlog.general.variable");
        messageEmbedField.addValueObjects(getMessageFromJson(jsonObject));
        embedFieldList.add(messageEmbedField);
        if (affected == null) {
            return;
        }
        event.getGuild().retrieveAuditLogs().limit(1).queue(auditLogEntries -> {
            AuditLogEntry entry = auditLogEntries.get(0);
            ModlogEvent modlogEvent;
            User responsible;
            if (entry.getType().equals(ActionType.MESSAGE_DELETE)) {
                modlogEvent = ModlogEvent.GUILD_MESSAGE_DELETED;
                responsible = entry.getUser();
            } else { //TODO not assume self delete if audit log entry for message delete was not found.
                modlogEvent = ModlogEvent.GUILD_MESSAGE_DELETED_SELF;
                responsible = affected;
            }
            ModlogEventStore eventStore = new ModlogEventStore(modlogEvent, responsible, affected, embedFieldList);
            guildData.getModeration().sendModlogEvent(eventStore);
        });
    }

    public void onGuildMessageUpdate(GuildMessageUpdateEvent event) {
        GuildData guildData = GuildDataManager.getGuildData(event.getGuild().getIdLong());
        Message message = event.getMessage();
        String messageJson = CascadeBot.INS.getRedisClient().get("message:" + message.getId());
        CascadeBot.INS.getRedisClient().del("message:" + message.getId());
        JsonObject jsonObject = new JsonParser().parse(messageJson).getAsJsonObject();
        long messageSender = jsonObject.get("sender").getAsLong();
        User affected = CascadeBot.INS.getClient().getUserById(messageSender);
        List<LanguageEmbedField> embedFieldList = new ArrayList<>();
        LanguageEmbedField oldEmbedField = new LanguageEmbedField(false, "modlog.message.old_message", "modlog.general.variable");
        oldEmbedField.addValueObjects(getMessageFromJson(jsonObject));
        LanguageEmbedField newEmbedField = new LanguageEmbedField(false, "modlog.message.new_message", "modlog.general.variable");
        newEmbedField.addValueObjects(message.getContentRaw());
        embedFieldList.add(newEmbedField);
        if (affected == null) {
            return;
        }
        ModlogEvent modlogEvent = ModlogEvent.GUILD_MESSAGE_UPDATED;
        ModlogEventStore eventStore = new ModlogEventStore(modlogEvent, null, affected, embedFieldList);
        guildData.getModeration().sendModlogEvent(eventStore);
    }

    private String getMessageFromJson(JsonObject jsonObject) {
        String message;
        if (Config.INS.getEncryptKey() != null) {
            JsonArray bytesJsonArray = jsonObject.get("content").getAsJsonArray();
            byte[] messageBytes = new byte[bytesJsonArray.size()];
            for (int i = 0; i < bytesJsonArray.size(); i++) {
                messageBytes[i] = bytesJsonArray.get(i).getAsByte();
            }
            try {
                message = CryptUtils.decryptString(Config.INS.getEncryptKey(), Config.INS.getIvSpec(), messageBytes);
            } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | IllegalBlockSizeException | ShortBufferException e) {
                e.printStackTrace();
                // TODO log these
                return "";
            } catch (BadPaddingException e) {
                e.printStackTrace();
                return "";
            }
        } else {
            message = jsonObject.get("content").getAsString();
        }
        return message;
    }
    //endregion

    public void onGenericGuildUpdate(GenericGuildUpdateEvent event) {
        Guild affected = event.getEntity();
        GuildData guildData = GuildDataManager.getGuildData(affected.getIdLong());
        event.getGuild().retrieveAuditLogs().limit(1).queue(auditLogEntries -> {
            AuditLogEntry entry = auditLogEntries.get(0);
            List<LanguageEmbedField> embedFieldList = new ArrayList<>();
            User responsible = null;
            ModlogEvent modlogEvent;
            if (entry.getType().equals(ActionType.GUILD_UPDATE)) {
                responsible = entry.getUser();
            }
            if (event instanceof GuildUpdateAfkChannelEvent) {
                VoiceChannel oldChannel = ((GuildUpdateAfkChannelEvent) event).getOldAfkChannel();
                if (oldChannel != null) {
                    embedFieldList.add(new LanguageEmbedField(true, "modlog.guild.old_channel", "modlog.general.variable", oldChannel.getName()));
                }
                VoiceChannel newChannel = ((GuildUpdateAfkChannelEvent) event).getNewAfkChannel();
                if (newChannel != null) {
                    embedFieldList.add(new LanguageEmbedField(true, "modlog.guild.new_channel", "modlog.general.variable", oldChannel.getName()));
                }
                modlogEvent = ModlogEvent.GUILD_UPDATE_AFK_CHANNEL;
            } else if (event instanceof GuildUpdateAfkTimeoutEvent) {
                embedFieldList.add(new LanguageEmbedField(true, "modlog.guild.old_timeout", "modlog.guild.timeout", String.valueOf(((GuildUpdateAfkTimeoutEvent) event).getOldAfkTimeout().getSeconds())));
                embedFieldList.add(new LanguageEmbedField(true, "modlog.guild.new_timeout", "modlog.guild.timeout", String.valueOf(((GuildUpdateAfkTimeoutEvent) event).getNewAfkTimeout().getSeconds())));
                modlogEvent = ModlogEvent.GUILD_UPDATE_AFK_TIMEOUT;
            } else if (event instanceof GuildUpdateBannerEvent) {
                embedFieldList.add(new WebhookEmbed.EmbedField(false, "Old Image", ((GuildUpdateBannerEvent) event).getOldBannerUrl()));
                embedFieldList.add(new WebhookEmbed.EmbedField(false, "New Image", ((GuildUpdateBannerEvent) event).getNewBannerIdUrl()));
                modlogEvent = ModlogEvent.GUILD_UPDATE_BANNER;
            } else if (event instanceof GuildUpdateDescriptionEvent) {
                embedFieldList.add(new WebhookEmbed.EmbedField(false, "Old Description", ((GuildUpdateDescriptionEvent) event).getOldDescription()));
                embedFieldList.add(new WebhookEmbed.EmbedField(false, "New Description", ((GuildUpdateDescriptionEvent) event).getNewDescription()));
                modlogEvent = ModlogEvent.GUILD_UPDATE_DESCRIPTION;
            } else if (event instanceof GuildUpdateExplicitContentLevelEvent) {
                embedFieldList.add(new WebhookEmbed.EmbedField(true, "Old Filter", ((GuildUpdateExplicitContentLevelEvent) event).getOldLevel().name()));
                embedFieldList.add(new WebhookEmbed.EmbedField(true, "New Filter", ((GuildUpdateExplicitContentLevelEvent) event).getNewLevel().name()));
                modlogEvent = ModlogEvent.GUILD_UPDATE_EXPLICIT_FILTER;
            } else if (event instanceof GuildUpdateFeaturesEvent) {
                ListChanges<String> featuresChanged = new ListChanges<>(((GuildUpdateFeaturesEvent) event).getOldFeatures(), ((GuildUpdateFeaturesEvent) event).getNewFeatures());
                embedFieldList.add(new WebhookEmbed.EmbedField(false, "Added Features", String.join("\n", featuresChanged.getAdded())));
                embedFieldList.add(new WebhookEmbed.EmbedField(false, "Removed Features", String.join("\n", featuresChanged.getRemoved())));
                modlogEvent = ModlogEvent.GUILD_UPDATE_FEATURES;
            } else if (event instanceof GuildUpdateIconEvent) {
                embedFieldList.add(new WebhookEmbed.EmbedField(false, "Old Image", ((GuildUpdateIconEvent) event).getOldIconUrl()));
                embedFieldList.add(new WebhookEmbed.EmbedField(false, "New Image", ((GuildUpdateIconEvent) event).getNewIconUrl()));
                modlogEvent = ModlogEvent.GUILD_UPDATE_ICON;
            } else if (event instanceof GuildUpdateMaxMembersEvent) {
                embedFieldList.add(new WebhookEmbed.EmbedField(true, "Old Max Members", ((GuildUpdateMaxMembersEvent) event).getOldMaxMembers() + " members"));
                embedFieldList.add(new WebhookEmbed.EmbedField(true, "New Max Members", ((GuildUpdateMaxMembersEvent) event).getNewMaxMembers() + " members"));
                modlogEvent = ModlogEvent.GUILD_UPDATE_MAX_MEMBERS;
            } else if (event instanceof GuildUpdateMaxPresencesEvent) {
                embedFieldList.add(new WebhookEmbed.EmbedField(true, "Old Max Presences", ((GuildUpdateMaxPresencesEvent) event).getOldMaxPresences() + " presences"));
                embedFieldList.add(new WebhookEmbed.EmbedField(true, "New Max Presences", ((GuildUpdateMaxPresencesEvent) event).getNewMaxPresences() + " presences"));
                modlogEvent = ModlogEvent.GUILD_UPDATE_MAX_PRESENCES;
            } else if (event instanceof GuildUpdateMFALevelEvent) {
                embedFieldList.add(new WebhookEmbed.EmbedField(true, "Old MFA Level", ((GuildUpdateMFALevelEvent) event).getOldMFALevel().name()));
                embedFieldList.add(new WebhookEmbed.EmbedField(true, "New MFA Level", ((GuildUpdateMFALevelEvent) event).getNewMFALevel().name()));
                modlogEvent = ModlogEvent.GUILD_UPDATE_MFA_LEVEL;
            } else if (event instanceof GuildUpdateNameEvent) {
                embedFieldList.add(new WebhookEmbed.EmbedField(true, "Old Name", ((GuildUpdateNameEvent) event).getOldName()));
                modlogEvent = ModlogEvent.GUILD_UPDATE_NAME;
            } else if (event instanceof GuildUpdateNotificationLevelEvent) {
                embedFieldList.add(new WebhookEmbed.EmbedField(true, "Old Level", ((GuildUpdateNotificationLevelEvent) event).getOldNotificationLevel().name()));
                embedFieldList.add(new WebhookEmbed.EmbedField(true, "New Level", ((GuildUpdateNotificationLevelEvent) event).getNewNotificationLevel().name()));
                modlogEvent = ModlogEvent.GUILD_UPDATE_NOTIFICATION_LEVEL;
            } else if (event instanceof GuildUpdateRegionEvent) {
                embedFieldList.add(new WebhookEmbed.EmbedField(true, "Old Region", ((GuildUpdateRegionEvent) event).getOldRegion().getName()));
                embedFieldList.add(new WebhookEmbed.EmbedField(true, "New Region", ((GuildUpdateRegionEvent) event).getNewRegion().getName()));
                modlogEvent = ModlogEvent.GUILD_UPDATE_REGION;
            } else if (event instanceof GuildUpdateSplashEvent) {
                embedFieldList.add(new WebhookEmbed.EmbedField(false, "Old Splash", ((GuildUpdateSplashEvent) event).getOldSplashUrl()));
                embedFieldList.add(new WebhookEmbed.EmbedField(false, "New Splash", ((GuildUpdateSplashEvent) event).getNewSplashUrl()));
                modlogEvent = ModlogEvent.GUILD_UPDATE_SPLASH;
            } else if (event instanceof GuildUpdateSystemChannelEvent) {
                embedFieldList.add(new WebhookEmbed.EmbedField(true, "Old System Channel", ((GuildUpdateSystemChannelEvent) event).getOldSystemChannel().getName()));
                embedFieldList.add(new WebhookEmbed.EmbedField(true, "New System Channel", ((GuildUpdateSystemChannelEvent) event).getNewSystemChannel().getName()));
                modlogEvent = ModlogEvent.GUILD_UPDATE_SYSTEM_CHANNEL;
            } else if (event instanceof GuildUpdateVanityCodeEvent) {
                embedFieldList.add(new WebhookEmbed.EmbedField(true, "Old Vanity Code", ((GuildUpdateVanityCodeEvent) event).getOldVanityCode()));
                embedFieldList.add(new WebhookEmbed.EmbedField(true, "Old Vanity Url", ((GuildUpdateVanityCodeEvent) event).getOldVanityUrl()));
                embedFieldList.add(new WebhookEmbed.EmbedField(true, "New Vanity Code", ((GuildUpdateVanityCodeEvent) event).getNewVanityCode()));
                embedFieldList.add(new WebhookEmbed.EmbedField(true, "New Vanity Url", ((GuildUpdateVanityCodeEvent) event).getNewVanityUrl()));
                modlogEvent = ModlogEvent.GUILD_UPDATE_VANITY_CODE;
            } else if (event instanceof GuildUpdateVerificationLevelEvent) {
                embedFieldList.add(new WebhookEmbed.EmbedField(true, "Old Verification Level", FormatUtils.getVerificationLevelString(((GuildUpdateVerificationLevelEvent) event).getOldVerificationLevel())));
                embedFieldList.add(new WebhookEmbed.EmbedField(true, "New Verification Level", FormatUtils.getVerificationLevelString(((GuildUpdateVerificationLevelEvent) event).getNewVerificationLevel())));
                modlogEvent = ModlogEvent.GUILD_UPDATE_VERIFICATION_LEVEL;
            } else {
                return;
            }
            ModlogEventStore eventStore = new ModlogEventStore(modlogEvent, responsible, affected, embedFieldList);
            guildData.getModeration().sendModlogEvent(eventStore);
        });
    }

    //region Channels
    public void onGenericStoreChannel(@NotNull GenericStoreChannelEvent event) {
        if (event instanceof StoreChannelCreateEvent) {
            handleChannelCreateEvents(event.getChannel().getGuild(), ChannelType.STORE, event.getChannel());
        } else if (event instanceof StoreChannelDeleteEvent) {
            handleChannelDeleteEvents(event.getChannel().getGuild(), ChannelType.STORE, event.getChannel());
        } else if (event instanceof StoreChannelUpdateNameEvent) {
            handleChannelUpdateNameEvents(event.getChannel().getGuild(), ChannelType.STORE, ((StoreChannelUpdateNameEvent) event).getOldName(), event.getChannel());
        } else if (event instanceof StoreChannelUpdatePermissionsEvent) {
            handleChannelUpdatePermissionsEvents(event.getChannel().getGuild(), ChannelType.STORE, ((StoreChannelUpdatePermissionsEvent) event).getChangedPermissionHolders(), event.getChannel());
        } else if (event instanceof StoreChannelUpdatePositionEvent) {
            handleChannelUpdatePositionEvents(event.getChannel().getGuild(), ChannelType.STORE, ((StoreChannelUpdatePositionEvent) event).getOldPosition(), event.getChannel());
        }
    }

    public void onGenericTextChannel(@NotNull GenericTextChannelEvent event) {
        if (event instanceof TextChannelCreateEvent) {
            handleChannelCreateEvents(event.getGuild(), ChannelType.TEXT, event.getChannel());
        } else if (event instanceof TextChannelDeleteEvent) {
            handleChannelDeleteEvents(event.getGuild(), ChannelType.TEXT, event.getChannel());
        } else if (event instanceof TextChannelUpdateNameEvent) {
            handleChannelUpdateNameEvents(event.getGuild(), ChannelType.TEXT, ((TextChannelUpdateNameEvent) event).getOldName(), event.getChannel());
        } else if (event instanceof TextChannelUpdatePermissionsEvent) {
            ((TextChannelUpdatePermissionsEvent) event).getChangedPermissionHolders();
            handleChannelUpdatePermissionsEvents(event.getGuild(), ChannelType.TEXT, ((TextChannelUpdatePermissionsEvent) event).getChangedPermissionHolders(), event.getChannel());
        } else if (event instanceof TextChannelUpdatePositionEvent) {
            handleChannelUpdatePositionEvents(event.getGuild(), ChannelType.TEXT, ((TextChannelUpdatePositionEvent) event).getOldPosition(), event.getChannel());
        }
    }

    public void onGenericVoiceChannel(@NotNull GenericVoiceChannelEvent event) {
        if (event instanceof VoiceChannelCreateEvent) {
            handleChannelCreateEvents(event.getGuild(), ChannelType.VOICE, event.getChannel());
        } else if (event instanceof VoiceChannelDeleteEvent) {
            handleChannelDeleteEvents(event.getGuild(), ChannelType.VOICE, event.getChannel());
        } else if (event instanceof VoiceChannelUpdateNameEvent) {
            handleChannelUpdateNameEvents(event.getGuild(), ChannelType.VOICE, ((VoiceChannelUpdateNameEvent) event).getOldName(), event.getChannel());
        } else if (event instanceof VoiceChannelUpdatePermissionsEvent) {
            handleChannelUpdatePermissionsEvents(event.getGuild(), ChannelType.VOICE, ((VoiceChannelUpdatePermissionsEvent) event).getChangedPermissionHolders(), event.getChannel());
        } else if (event instanceof VoiceChannelUpdatePositionEvent) {
            handleChannelUpdatePositionEvents(event.getGuild(), ChannelType.VOICE, ((VoiceChannelUpdatePositionEvent) event).getOldPosition(), event.getChannel());
        }
    }

    public void onGenericCategory(@NotNull GenericCategoryEvent event) {
        if (event instanceof CategoryCreateEvent) {
            handleChannelCreateEvents(event.getGuild(), ChannelType.CATEGORY, event.getCategory());
        } else if (event instanceof CategoryDeleteEvent) {
            handleChannelDeleteEvents(event.getGuild(), ChannelType.CATEGORY, event.getCategory());
        } else if (event instanceof CategoryUpdateNameEvent) {
            handleChannelUpdateNameEvents(event.getGuild(), ChannelType.CATEGORY, ((CategoryUpdateNameEvent) event).getOldName(), event.getCategory());
        } else if (event instanceof CategoryUpdatePermissionsEvent) {
            handleChannelUpdatePermissionsEvents(event.getGuild(), ChannelType.CATEGORY, ((CategoryUpdatePermissionsEvent) event).getChangedPermissionHolders(), event.getCategory());
        } else if (event instanceof CategoryUpdatePositionEvent) {
            handleChannelUpdatePositionEvents(event.getGuild(), ChannelType.CATEGORY, ((CategoryUpdatePositionEvent) event).getOldPosition(), event.getCategory());
        }
    }
    //endregion

    //region Channel handlers
    private void handleChannelCreateEvents(Guild guild, ChannelType type, GuildChannel channel) {
        ModlogEvent event = ModlogEvent.CHANNEL_CREATED;
        GuildData guildData = GuildDataManager.getGuildData(guild.getIdLong());
        guild.retrieveAuditLogs().limit(1).queue(auditLogEntries -> {
            AuditLogEntry entry = auditLogEntries.get(0);
            List<LanguageEmbedField> embedFieldList = new ArrayList<>();
            User responsible = null;
            if (entry.getType().equals(ActionType.CHANNEL_CREATE)) {
                responsible = entry.getUser();
            }
            embedFieldList.add(new WebhookEmbed.EmbedField(true, "Type", type.name()));
            ModlogEventStore modlogEventStore = new ModlogEventStore(event, responsible, channel, embedFieldList);
            guildData.getModeration().sendModlogEvent(modlogEventStore);
        });
    }

    private void handleChannelDeleteEvents(Guild guild, ChannelType type, GuildChannel channel) {
        ModlogEvent event = ModlogEvent.CHANNEL_DELETED;
        GuildData guildData = GuildDataManager.getGuildData(guild.getIdLong());
        guild.retrieveAuditLogs().limit(1).queue(auditLogEntries -> {
            AuditLogEntry entry = auditLogEntries.get(0);
            List<WebhookEmbed.EmbedField> embedFieldList = new ArrayList<>();
            User responsible = null;
            if (entry.getType().equals(ActionType.CHANNEL_DELETE)) {
                responsible = entry.getUser();
            }
            embedFieldList.add(new WebhookEmbed.EmbedField(true, "Type", type.name()));
            ModlogEventStore modlogEventStore = new ModlogEventStore(event, responsible, channel, embedFieldList);
            guildData.getModeration().sendModlogEvent(modlogEventStore);
        });
    }

    private void handleChannelUpdateNameEvents(Guild guild, ChannelType type, String oldName, GuildChannel channel) {
        ModlogEvent event = ModlogEvent.CHANNEL_NAME_UPDATED;
        GuildData guildData = GuildDataManager.getGuildData(guild.getIdLong());
        guild.retrieveAuditLogs().limit(1).queue(auditLogEntries -> {
            AuditLogEntry entry = auditLogEntries.get(0);
            List<LanguageEmbedField> embedFieldList = new ArrayList<>();
            User responsible = null;
            if (entry.getType().equals(ActionType.CHANNEL_UPDATE)) {
                responsible = entry.getUser();
            }
            embedFieldList.add(new WebhookEmbed.EmbedField(true, "Type", type.name()));
            embedFieldList.add(new WebhookEmbed.EmbedField(true, "Old Name", oldName));
            ModlogEventStore modlogEventStore = new ModlogEventStore(event, responsible, channel, embedFieldList);
            guildData.getModeration().sendModlogEvent(modlogEventStore);
        });
    }

    private void handleChannelUpdatePermissionsEvents(Guild guild, ChannelType type, List<IPermissionHolder> changedPermissionHolders, GuildChannel channel) {
        ModlogEvent event = ModlogEvent.CHANNEL_PERMISSIONS_UPDATED;
        GuildData guildData = GuildDataManager.getGuildData(guild.getIdLong());
        guild.retrieveAuditLogs().limit(1).queue(auditLogEntries -> {
            AuditLogEntry entry = auditLogEntries.get(0);
            List<LanguageEmbedField> embedFieldList = new ArrayList<>();
            User responsible = null;
            if (entry.getType().equals(ActionType.CHANNEL_UPDATE) || entry.getType().equals(ActionType.CHANNEL_OVERRIDE_UPDATE)) {
                responsible = entry.getUser();
                Map<String, AuditLogChange> auditLogChangeMap = entry.getChanges();
                for (Map.Entry<String, AuditLogChange> auditLogChangeEntry : auditLogChangeMap.entrySet()) {
                    AuditLogChange value = auditLogChangeEntry.getValue();
                    EnumSet<Permission> oldPermissions = Permission.getPermissions(((Integer) value.getOldValue()).longValue());
                    EnumSet<Permission> newPermissions = Permission.getPermissions(((Integer) value.getNewValue()).longValue());
                    ListChanges<Permission> permissionListChanges = new ListChanges<>(oldPermissions, newPermissions);
                    String change = "unknown";
                    switch (auditLogChangeEntry.getKey()) {
                        case "allow":
                            change = "Allowed";
                            break;
                        case "deny":
                            change = "Denied";
                            break;
                    }
                    String affectedType = entry.getOptionByName("type");
                    long affectedId = entry.getTargetIdLong();
                    String affected = "unknown";
                    switch (affectedType) {
                        case "role":
                            affected = CascadeBot.INS.getShardManager().getRoleById(affectedId).getName();
                            break;
                        case "user":
                            affected = CascadeBot.INS.getShardManager().getUserById(affectedId).getAsTag();
                            break;
                    }
                    embedFieldList.add(new WebhookEmbed.EmbedField(false, "Added " + change + " Permissions to " + affected, permissionListChanges.getAdded().stream().map(permission -> permission.getName()).collect(Collectors.joining("\n"))));
                    embedFieldList.add(new WebhookEmbed.EmbedField(false, "Removed " + change + " Permissions to " + affected, permissionListChanges.getRemoved().stream().map(permission -> permission.getName()).collect(Collectors.joining("\n"))));
                }
            }
            embedFieldList.add(new WebhookEmbed.EmbedField(true, "Type", type.name()));
            ModlogEventStore modlogEventStore = new ModlogEventStore(event, responsible, channel, embedFieldList);
            guildData.getModeration().sendModlogEvent(modlogEventStore);
        });
    }

    private void handleChannelUpdatePositionEvents(Guild guild, ChannelType type, int oldPos, GuildChannel channel) {
        ModlogEvent event = ModlogEvent.CHANNEL_POSITION_UPDATED;
        GuildData guildData = GuildDataManager.getGuildData(guild.getIdLong());
        guild.retrieveAuditLogs().limit(1).queue(auditLogEntries -> {
            AuditLogEntry entry = auditLogEntries.get(0);
            List<LanguageEmbedField> embedFieldList = new ArrayList<>();
            User responsible = null;
            if (entry.getType().equals(ActionType.CHANNEL_UPDATE)) {
                responsible = entry.getUser();
            }
            embedFieldList.add(new WebhookEmbed.EmbedField(true, "Type", type.name()));
            embedFieldList.add(new WebhookEmbed.EmbedField(true, "Old Position", String.valueOf(oldPos)));
            embedFieldList.add(new WebhookEmbed.EmbedField(true, "New Position", String.valueOf(channel.getPosition())));
            ModlogEventStore modlogEventStore = new ModlogEventStore(event, responsible, channel, embedFieldList);
            guildData.getModeration().sendModlogEvent(modlogEventStore);
        });
    }
    //endregion

    public void onGenericRole(GenericRoleEvent event) {
        GuildData guildData = GuildDataManager.getGuildData(event.getGuild().getIdLong());
        event.getGuild().retrieveAuditLogs().limit(1).queue(auditLogEntries -> {
            AuditLogEntry entry = auditLogEntries.get(0);
            List<LanguageEmbedField> embedFieldList = new ArrayList<>();
            User responsible = null;
            ModlogEvent modlogEvent;
            if (entry.getType().equals(ActionType.ROLE_CREATE) || entry.getType().equals(ActionType.ROLE_DELETE) || entry.getType().equals(ActionType.ROLE_UPDATE)) {
                responsible = entry.getUser();
            }
            Role affected = event.getRole();
            if (event instanceof RoleCreateEvent) {
                modlogEvent = ModlogEvent.ROLE_CREATED;
            } else if (event instanceof RoleDeleteEvent) {
                modlogEvent = ModlogEvent.ROLE_DELETED;
            } else if (event instanceof RoleUpdateColorEvent) {
                modlogEvent = ModlogEvent.ROLE_COLOR_UPDATED;
                embedFieldList.add(new WebhookEmbed.EmbedField(true, "Old Color", ((RoleUpdateColorEvent) event).getOldColor().toString())); // TODO properly show color
                embedFieldList.add(new WebhookEmbed.EmbedField(true, "New Color", ((RoleUpdateColorEvent) event).getNewColor().toString()));
            } else if (event instanceof RoleUpdateHoistedEvent) {
                modlogEvent = ModlogEvent.ROLE_HOIST_UPDATED;
                embedFieldList.add(new WebhookEmbed.EmbedField(true, "Hoisted", String.valueOf(!((RoleUpdateHoistedEvent) event).wasHoisted())));
            } else if (event instanceof RoleUpdateMentionableEvent) {
                modlogEvent = ModlogEvent.ROLE_MENTIONABLE_UPDATED;
                embedFieldList.add(new WebhookEmbed.EmbedField(true, "Mentionable", String.valueOf(!((RoleUpdateMentionableEvent) event).wasMentionable())));
            } else if (event instanceof RoleUpdateNameEvent) {
                modlogEvent = ModlogEvent.ROLE_NAME_UPDATED;
                embedFieldList.add(new WebhookEmbed.EmbedField(true, "Old Name", ((RoleUpdateNameEvent) event).getOldName()));
                embedFieldList.add(new WebhookEmbed.EmbedField(true, "New Name", ((RoleUpdateNameEvent) event).getNewName()));
            } else if (event instanceof RoleUpdatePermissionsEvent) {
                modlogEvent = ModlogEvent.ROLE_PERMISSIONS_UPDATED;
                EnumSet<Permission> oldPermissions = ((RoleUpdatePermissionsEvent) event).getOldPermissions();
                EnumSet<Permission> newPermissions = ((RoleUpdatePermissionsEvent) event).getNewPermissions();
                ListChanges<Permission> permissionListChanges = new ListChanges<>(oldPermissions, newPermissions);
                embedFieldList.add(new WebhookEmbed.EmbedField(false, "Added Permissions", permissionListChanges.getAdded().stream().map(permission -> permission.getName()).collect(Collectors.joining("\n"))));
                embedFieldList.add(new WebhookEmbed.EmbedField(false, "Removed Permissions", permissionListChanges.getRemoved().stream().map(permission -> permission.getName()).collect(Collectors.joining("\n"))));
            } else if (event instanceof RoleUpdatePositionEvent) {
                modlogEvent = ModlogEvent.ROLE_POSITION_UPDATED;
                embedFieldList.add(new WebhookEmbed.EmbedField(true, "Old Position", String.valueOf(((RoleUpdatePositionEvent) event).getOldPosition())));
                embedFieldList.add(new WebhookEmbed.EmbedField(true, "New Position", String.valueOf(((RoleUpdatePositionEvent) event).getNewPosition())));
            } else {
                return;
            }
            ModlogEventStore modlogEventStore = new ModlogEventStore(modlogEvent, responsible, affected, embedFieldList);
            guildData.getModeration().sendModlogEvent(modlogEventStore);
        });
    }

    //region Username updates
    public void onUserUpdateName(UserUpdateNameEvent event) {
        // TODO propagate to guilds
        List<LanguageEmbedField> embedFieldList = new ArrayList<>();
        embedFieldList.add(new WebhookEmbed.EmbedField(true, "Old Name", event.getOldName()));
        embedFieldList.add(new WebhookEmbed.EmbedField(true, "New Name", event.getNewName()));
        ModlogEventStore modlogEventStore = new ModlogEventStore(ModlogEvent.USER_NAME_UPDATED, event.getUser(), event.getUser(), embedFieldList);
    }

    public void onUserUpdateDiscriminator(UserUpdateDiscriminatorEvent event) {
        List<LanguageEmbedField> embedFieldList = new ArrayList<>();
        embedFieldList.add(new WebhookEmbed.EmbedField(true, "Old Discriminator", event.getOldDiscriminator()));
        embedFieldList.add(new WebhookEmbed.EmbedField(true, "New Discriminator", event.getNewDiscriminator()));
        ModlogEventStore modlogEventStore = new ModlogEventStore(ModlogEvent.USER_DISCRIMINATOR_UPDATED, event.getUser(), event.getUser(), embedFieldList);
    }
    //endregion

    //TODO move this to a util class
    public static class ListChanges<T> {
        private final List<T> added = new ArrayList<>();
        private final List<T> removed = new ArrayList<>();

        public ListChanges(Collection<? extends T> originalList, Collection<? extends T> newList) {
            for (T object : originalList) {
                if (!newList.contains(object)) {
                    removed.add(object);
                }
            }
            for (T object : newList) {
                if (!originalList.contains(object)) {
                    added.add(object);
                }
            }
        }

        public List<T> getAdded() {
            return added;
        }

        public List<T> getRemoved() {
            return removed;
        }
    }

}
