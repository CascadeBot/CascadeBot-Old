package org.cascadebot.cascadebot.events;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.channel.category.CategoryCreateEvent;
import net.dv8tion.jda.api.events.channel.category.CategoryDeleteEvent;
import net.dv8tion.jda.api.events.channel.category.GenericCategoryEvent;
import net.dv8tion.jda.api.events.channel.category.update.CategoryUpdateNameEvent;
import net.dv8tion.jda.api.events.channel.category.update.CategoryUpdatePositionEvent;
import net.dv8tion.jda.api.events.channel.store.GenericStoreChannelEvent;
import net.dv8tion.jda.api.events.channel.store.StoreChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.store.StoreChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.store.update.StoreChannelUpdateNameEvent;
import net.dv8tion.jda.api.events.channel.store.update.StoreChannelUpdatePositionEvent;
import net.dv8tion.jda.api.events.channel.text.GenericTextChannelEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.text.update.TextChannelUpdateNameEvent;
import net.dv8tion.jda.api.events.channel.text.update.TextChannelUpdatePositionEvent;
import net.dv8tion.jda.api.events.channel.voice.GenericVoiceChannelEvent;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.voice.update.VoiceChannelUpdateNameEvent;
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
import net.dv8tion.jda.api.events.guild.override.GenericPermissionOverrideEvent;
import net.dv8tion.jda.api.events.guild.override.PermissionOverrideCreateEvent;
import net.dv8tion.jda.api.events.guild.override.PermissionOverrideDeleteEvent;
import net.dv8tion.jda.api.events.guild.override.PermissionOverrideUpdateEvent;
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
import org.cascadebot.cascadebot.utils.ColorUtils;
import org.cascadebot.cascadebot.utils.CryptUtils;
import org.cascadebot.cascadebot.utils.LanguageEmbedField;
import org.cascadebot.cascadebot.utils.SerializableMessage;
import org.jetbrains.annotations.NotNull;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import java.awt.Color;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ModlogEventListener extends ListenerAdapter {

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
                embedFieldList.add(languageEmbedField);
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
        if (CascadeBot.INS.getRedisClient() == null) {
            return;
        }
        GuildData guildData = GuildDataManager.getGuildData(event.getGuild().getIdLong());
        String messageID = event.getMessageId();
        String messageString = CascadeBot.INS.getRedisClient().get("message:" + messageID);
        if (messageString == null) {
            return;
        }
        CascadeBot.INS.getRedisClient().del("message:" + messageID);
        SerializableMessage message = getMessageFromString(event.getMessageIdLong(), messageString);
        if (message == null) {
            return;
        }
        User affected = CascadeBot.INS.getClient().getUserById(message.getAuthorId());
        List<LanguageEmbedField> embedFieldList = new ArrayList<>();
        LanguageEmbedField messageEmbedField = new LanguageEmbedField(false, "modlog.message.message", "modlog.general.variable");
        messageEmbedField.addValueObjects(message.getContent());
        embedFieldList.add(messageEmbedField);
        if (affected == null) {
            return;
        }
        //TODO handle embeds/ect...
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
        if (CascadeBot.INS.getRedisClient() == null) {
            return;
        }
        if (event.getAuthor().isBot()) {
            return;
        }
        GuildData guildData = GuildDataManager.getGuildData(event.getGuild().getIdLong());
        Message message = event.getMessage();
        String messageString = CascadeBot.INS.getRedisClient().get("message:" + message.getId());
        if (messageString == null) {
            return;
        }
        CascadeBot.INS.getRedisClient().del("message:" + message.getId());
        SerializableMessage oldMessage = getMessageFromString(message.getIdLong(), messageString);
        if (oldMessage == null) {
            return;
        }
        User affected = message.getAuthor();
        List<LanguageEmbedField> embedFieldList = new ArrayList<>();
        LanguageEmbedField oldEmbedField = new LanguageEmbedField(false, "modlog.message.old_message", "modlog.general.variable");
        oldEmbedField.addValueObjects(oldMessage.getContent());
        LanguageEmbedField newEmbedField = new LanguageEmbedField(false, "modlog.message.new_message", "modlog.general.variable");
        newEmbedField.addValueObjects(message.getContentRaw());
        // TODO handle embeds/ect...
        embedFieldList.add(newEmbedField);
        ModlogEvent modlogEvent = ModlogEvent.GUILD_MESSAGE_UPDATED;
        ModlogEventStore eventStore = new ModlogEventStore(modlogEvent, null, affected, embedFieldList);
        guildData.getModeration().sendModlogEvent(eventStore);
    }

    private SerializableMessage getMessageFromString(long id, String messageString) {
        String message;
        if (Config.INS.getEncryptKey() != null) {
            byte[] messageId = ByteBuffer.allocate(Long.BYTES).putLong(id).array();
            byte[] iv = new byte[messageId.length * 2];
            System.arraycopy(messageId, 0, iv, 0, messageId.length);
            System.arraycopy(messageId, 0, iv, messageId.length, messageId.length);
            JsonArray bytesJsonArray = new JsonParser().parse(messageString).getAsJsonArray();
            byte[] messageBytes = new byte[bytesJsonArray.size()];
            for (int i = 0; i < bytesJsonArray.size(); i++) {
                messageBytes[i] = bytesJsonArray.get(i).getAsByte();
            }
            try {
                message = CryptUtils.decryptString(Config.INS.getEncryptKey(), iv, messageBytes);
            } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | IllegalBlockSizeException | ShortBufferException e) {
                e.printStackTrace();
                // TODO log these
                return null;
            } catch (BadPaddingException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            message = messageString;
        }
        return CascadeBot.getGSON().fromJson(message, SerializableMessage.class);
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
                    embedFieldList.add(new LanguageEmbedField(true, "modlog.guild.new_channel", "modlog.general.variable", newChannel.getName()));
                }
                modlogEvent = ModlogEvent.GUILD_UPDATE_AFK_CHANNEL;
            } else if (event instanceof GuildUpdateAfkTimeoutEvent) {
                embedFieldList.add(new LanguageEmbedField(true, "modlog.guild.old_timeout", "modlog.guild.timeout", String.valueOf(((GuildUpdateAfkTimeoutEvent) event).getOldAfkTimeout().getSeconds())));
                embedFieldList.add(new LanguageEmbedField(true, "modlog.guild.new_timeout", "modlog.guild.timeout", String.valueOf(((GuildUpdateAfkTimeoutEvent) event).getNewAfkTimeout().getSeconds())));
                modlogEvent = ModlogEvent.GUILD_UPDATE_AFK_TIMEOUT;
            } else if (event instanceof GuildUpdateBannerEvent) {
                if (((GuildUpdateBannerEvent) event).getOldBannerUrl() != null) {
                    embedFieldList.add(new LanguageEmbedField(false, "modlog.guild.old_image", "modlog.general.variable", ((GuildUpdateBannerEvent) event).getOldBannerUrl()));
                }
                if (((GuildUpdateBannerEvent) event).getNewBannerUrl() != null) {
                    embedFieldList.add(new LanguageEmbedField(false, "modlog.guild.new_image", "modlog.general.variable", ((GuildUpdateBannerEvent) event).getNewBannerUrl()));
                }
                modlogEvent = ModlogEvent.GUILD_UPDATE_BANNER;
            } else if (event instanceof GuildUpdateDescriptionEvent) {
                if (((GuildUpdateDescriptionEvent) event).getOldDescription() != null) {
                    embedFieldList.add(new LanguageEmbedField(false, "modlog.guild.old_description", "modlog.general.variable", ((GuildUpdateDescriptionEvent) event).getOldDescription()));
                }
                if (((GuildUpdateDescriptionEvent) event).getNewDescription() != null) {
                    embedFieldList.add(new LanguageEmbedField(false, "modlog.guild.new_description", "modlog.general.variable", ((GuildUpdateDescriptionEvent) event).getNewDescription()));
                }
                modlogEvent = ModlogEvent.GUILD_UPDATE_DESCRIPTION;
            } else if (event instanceof GuildUpdateExplicitContentLevelEvent) {
                embedFieldList.add(new LanguageEmbedField(true, "modlog.guild.content_filter.old", "modlog.guild.content_filter." + ((GuildUpdateExplicitContentLevelEvent) event).getOldLevel().name().toLowerCase()));
                embedFieldList.add(new LanguageEmbedField(true, "modlog.guild.content_filter.new", "modlog.guild.content_filter." + ((GuildUpdateExplicitContentLevelEvent) event).getNewLevel().name().toLowerCase()));
                modlogEvent = ModlogEvent.GUILD_UPDATE_EXPLICIT_FILTER;
            } else if (event instanceof GuildUpdateFeaturesEvent) {
                ListChanges<String> featuresChanged = new ListChanges<>(((GuildUpdateFeaturesEvent) event).getOldFeatures(), ((GuildUpdateFeaturesEvent) event).getNewFeatures());
                embedFieldList.add(new LanguageEmbedField(false, "modlog.guild.add_feature", "modlog.general.variable", String.join("\n", featuresChanged.getAdded())));
                embedFieldList.add(new LanguageEmbedField(false, "modlog.guild.removed_feature", "modlog.general.variable", String.join("\n", featuresChanged.getRemoved())));
                modlogEvent = ModlogEvent.GUILD_UPDATE_FEATURES;
            } else if (event instanceof GuildUpdateIconEvent) {
                if (((GuildUpdateIconEvent) event).getOldIconUrl() != null) {
                    embedFieldList.add(new LanguageEmbedField(false, "modlog.guild.old_image", "modlog.general.variable", ((GuildUpdateIconEvent) event).getOldIconUrl()));
                }
                if (((GuildUpdateIconEvent) event).getNewIconUrl() != null) {
                    embedFieldList.add(new LanguageEmbedField(false, "modlog.guild.new_image", "modlog.general.variable", ((GuildUpdateIconEvent) event).getNewIconUrl()));
                }
                modlogEvent = ModlogEvent.GUILD_UPDATE_ICON;
            } else if (event instanceof GuildUpdateMaxMembersEvent) {
                embedFieldList.add(new LanguageEmbedField(true, "modlog.guild.old_max_members", "modlog.guild.members", String.valueOf(((GuildUpdateMaxMembersEvent) event).getOldMaxMembers())));
                embedFieldList.add(new LanguageEmbedField(true, "modlog.guild.new_max_members", "modlog.guild.members", String.valueOf(((GuildUpdateMaxMembersEvent) event).getNewMaxMembers())));
                modlogEvent = ModlogEvent.GUILD_UPDATE_MAX_MEMBERS;
            } else if (event instanceof GuildUpdateMaxPresencesEvent) {
                embedFieldList.add(new LanguageEmbedField(true, "modlog.guild.old_presences", "modlog.guild.presences", String.valueOf(((GuildUpdateMaxPresencesEvent) event).getOldMaxPresences())));
                embedFieldList.add(new LanguageEmbedField(true, "modlog.guild.new_presences", "modlog.guild.presences", String.valueOf(((GuildUpdateMaxPresencesEvent) event).getNewMaxPresences())));
                modlogEvent = ModlogEvent.GUILD_UPDATE_MAX_PRESENCES;
            } else if (event instanceof GuildUpdateMFALevelEvent) {
                embedFieldList.add(new LanguageEmbedField(true, "modlog.guild.mfa.old", "modlog.guild.mfa." + ((GuildUpdateMFALevelEvent) event).getOldMFALevel().name().toLowerCase()));
                embedFieldList.add(new LanguageEmbedField(true, "modlog.guild.mfa.new", "modlog.guild.mfa." + ((GuildUpdateMFALevelEvent) event).getNewMFALevel().name().toLowerCase()));
                modlogEvent = ModlogEvent.GUILD_UPDATE_MFA_LEVEL;
            } else if (event instanceof GuildUpdateNameEvent) {
                embedFieldList.add(new LanguageEmbedField(true, "modlog.general.old_name", "modlog.general.variable", ((GuildUpdateNameEvent) event).getOldName()));
                modlogEvent = ModlogEvent.GUILD_UPDATE_NAME;
            } else if (event instanceof GuildUpdateNotificationLevelEvent) {
                embedFieldList.add(new LanguageEmbedField(true, "modlog.guild.notification.old", "modlog.guild.notification." + ((GuildUpdateNotificationLevelEvent) event).getOldNotificationLevel().name().toLowerCase()));
                embedFieldList.add(new LanguageEmbedField(true, "modlog.guild.notification.new", "modlog.guild.notification." + ((GuildUpdateNotificationLevelEvent) event).getNewNotificationLevel().name().toLowerCase()));
                modlogEvent = ModlogEvent.GUILD_UPDATE_NOTIFICATION_LEVEL;
            } else if (event instanceof GuildUpdateRegionEvent) {
                embedFieldList.add(new LanguageEmbedField(true, "modlog.guild.old_region", "modlog.general.variable", ((GuildUpdateRegionEvent) event).getOldRegion().getName()));
                embedFieldList.add(new LanguageEmbedField(true, "modlog.guild.new_region", "modlog.general.variable", ((GuildUpdateRegionEvent) event).getNewRegion().getName()));
                modlogEvent = ModlogEvent.GUILD_UPDATE_REGION;
            } else if (event instanceof GuildUpdateSplashEvent) {
                if (((GuildUpdateSplashEvent) event).getOldSplashUrl() != null) {
                    embedFieldList.add(new LanguageEmbedField(false, "modlog.guild.old_splash", "modlog.general.variable", ((GuildUpdateSplashEvent) event).getOldSplashUrl()));
                }
                if (((GuildUpdateSplashEvent) event).getNewSplashUrl() != null) {
                    embedFieldList.add(new LanguageEmbedField(false, "modlog.guild.new_splash", "modlog.general.variable", ((GuildUpdateSplashEvent) event).getNewSplashUrl()));
                }
                modlogEvent = ModlogEvent.GUILD_UPDATE_SPLASH;
            } else if (event instanceof GuildUpdateSystemChannelEvent) {
                TextChannel oldSystemChannel = ((GuildUpdateSystemChannelEvent) event).getOldSystemChannel();
                if (oldSystemChannel != null) {
                    embedFieldList.add(new LanguageEmbedField(true, "modlog.guild.old_sys", "modlog.general.variable", oldSystemChannel.getName()));
                }
                TextChannel newSystemChannel = ((GuildUpdateSystemChannelEvent) event).getNewSystemChannel();
                if (newSystemChannel != null) {
                    embedFieldList.add(new LanguageEmbedField(true, "modlog.guild.new_sys", "modlog.general.variable", newSystemChannel.getName()));
                }
                modlogEvent = ModlogEvent.GUILD_UPDATE_SYSTEM_CHANNEL;
            } else if (event instanceof GuildUpdateVanityCodeEvent) {
                if (((GuildUpdateVanityCodeEvent) event).getOldVanityCode() != null) {
                    embedFieldList.add(new LanguageEmbedField(true, "modlog.guild.vanity_code.old", "modlog.general.variable", ((GuildUpdateVanityCodeEvent) event).getOldVanityCode()));
                }
                if (((GuildUpdateVanityCodeEvent) event).getOldVanityUrl() != null) {
                    embedFieldList.add(new LanguageEmbedField(true, "modlog.guild.vanity_url.old", "modlog.general.variable", ((GuildUpdateVanityCodeEvent) event).getOldVanityUrl()));
                }
                if (((GuildUpdateVanityCodeEvent) event).getNewVanityCode() != null) {
                    embedFieldList.add(new LanguageEmbedField(true, "modlog.guild.vanity_code.new", "modlog.general.variable", ((GuildUpdateVanityCodeEvent) event).getNewVanityCode()));
                }
                if (((GuildUpdateVanityCodeEvent) event).getNewVanityUrl() != null) {
                    embedFieldList.add(new LanguageEmbedField(true, "modlog.guild.vanity_url.new", "modlog.general.variable", ((GuildUpdateVanityCodeEvent) event).getNewVanityUrl()));
                }
                modlogEvent = ModlogEvent.GUILD_UPDATE_VANITY_CODE;
            } else if (event instanceof GuildUpdateVerificationLevelEvent) {
                embedFieldList.add(new LanguageEmbedField(true, "modlog.guild.old_verification", "utils.verification_level." + ((GuildUpdateVerificationLevelEvent) event).getOldVerificationLevel().name().toLowerCase()));
                embedFieldList.add(new LanguageEmbedField(true, "modlog.guild.new_verification", "utils.verification_level." + ((GuildUpdateVerificationLevelEvent) event).getNewVerificationLevel().name().toLowerCase()));
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
        } else if (event instanceof CategoryUpdatePositionEvent) {
            handleChannelUpdatePositionEvents(event.getGuild(), ChannelType.CATEGORY, ((CategoryUpdatePositionEvent) event).getOldPosition(), event.getCategory());
        }
    }

    public void onGenericPermissionOverride(GenericPermissionOverrideEvent event) {
        ModlogEvent modlogEvent = ModlogEvent.CHANNEL_PERMISSIONS_UPDATED;
        Guild guild = event.getGuild();
        GuildData guildData = GuildDataManager.getGuildData(guild.getIdLong());
        guild.retrieveAuditLogs().limit(1).queue(auditLogEntries -> {
            AuditLogEntry entry = auditLogEntries.get(0);
            List<LanguageEmbedField> embedFieldList = new ArrayList<>();
            User responsible = null;
            if (entry.getType().equals(ActionType.CHANNEL_UPDATE) || entry.getType().equals(ActionType.CHANNEL_OVERRIDE_UPDATE)) {
                responsible = entry.getUser();
            }
            String permissionsHolderName;
            if (event.getPermissionHolder() instanceof User) {
                permissionsHolderName = ((User) event.getPermissionHolder()).getName();
            } else if (event.getPermissionHolder() instanceof Role) {
                permissionsHolderName = ((Role) event.getPermissionHolder()).getName();
            } else {
                permissionsHolderName = "";
            }

            String allowedPath;
            String deniedPath;
            if (event instanceof PermissionOverrideCreateEvent) {
                allowedPath = "modlog.channel.perm.added_allow";
                deniedPath = "modlog.channel.perm.added_deny";
            } else if (event instanceof PermissionOverrideDeleteEvent) {
                allowedPath = "modlog.channel.perm.removed_allow";
                deniedPath = "modlog.channel.perm.removed_deny";
            } else if (event instanceof PermissionOverrideUpdateEvent) {
                allowedPath = "modlog.channel.perm.update_allow";
                deniedPath = "modlog.channel.perm.update_deny";
                if (((PermissionOverrideUpdateEvent) event).getOldAllow().size() > 0) {
                    LanguageEmbedField oldAllowed = new LanguageEmbedField(false, "modlog.channel.perm.old_allow", "modlog.general.variable",
                            ((PermissionOverrideUpdateEvent) event).getOldAllow().stream().map(Permission::getName).collect(Collectors.joining("\n")));
                    oldAllowed.addTitleObjects(permissionsHolderName);
                    embedFieldList.add(oldAllowed);
                }
                if (((PermissionOverrideUpdateEvent) event).getOldDeny().size() > 0) {
                    LanguageEmbedField oldDenied = new LanguageEmbedField(false, "modlog.channel.perm.old_allow", "modlog.general.variable",
                            ((PermissionOverrideUpdateEvent) event).getOldDeny().stream().map(Permission::getName).collect(Collectors.joining("\n")));
                    oldDenied.addTitleObjects(permissionsHolderName);
                    embedFieldList.add(oldDenied);
                }
            } else {
                return;
            }
            if (event.getPermissionOverride().getAllowed().size() > 0) {
                LanguageEmbedField allowed = new LanguageEmbedField(false, allowedPath, "modlog.general.variable", event.getPermissionOverride().getAllowed().stream().map(Permission::getName).collect(Collectors.joining("\n")));
                allowed.addTitleObjects(permissionsHolderName);
                embedFieldList.add(allowed);
            }
            if (event.getPermissionOverride().getDenied().size() > 0) {
                LanguageEmbedField denied = new LanguageEmbedField(false, deniedPath, "modlog.general.variable", event.getPermissionOverride().getDenied().stream().map(Permission::getName).collect(Collectors.joining("\n")));
                denied.addTitleObjects(permissionsHolderName);
                embedFieldList.add(denied);
            }
            embedFieldList.add(new LanguageEmbedField(true, "modlog.channel.type.name", "modlog.channel.type." + event.getChannelType().name().toLowerCase()));
            ModlogEventStore modlogEventStore = new ModlogEventStore(modlogEvent, responsible, event.getChannel(), embedFieldList);
            guildData.getModeration().sendModlogEvent(modlogEventStore);
        });
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
            embedFieldList.add(new LanguageEmbedField(true, "modlog.channel.type.name", "modlog.channel.type." + type.name().toLowerCase()));
            ModlogEventStore modlogEventStore = new ModlogEventStore(event, responsible, channel, embedFieldList);
            guildData.getModeration().sendModlogEvent(modlogEventStore);
        });
    }

    private void handleChannelDeleteEvents(Guild guild, ChannelType type, GuildChannel channel) {
        ModlogEvent event = ModlogEvent.CHANNEL_DELETED;
        GuildData guildData = GuildDataManager.getGuildData(guild.getIdLong());
        guild.retrieveAuditLogs().limit(1).queue(auditLogEntries -> {
            AuditLogEntry entry = auditLogEntries.get(0);
            List<LanguageEmbedField> embedFieldList = new ArrayList<>();
            User responsible = null;
            if (entry.getType().equals(ActionType.CHANNEL_DELETE)) {
                responsible = entry.getUser();
            }
            embedFieldList.add(new LanguageEmbedField(true, "modlog.channel.type.name", "modlog.channel.type." + type.name().toLowerCase()));
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
            embedFieldList.add(new LanguageEmbedField(true, "modlog.channel.type.name", "modlog.channel.type." + type.name().toLowerCase()));
            embedFieldList.add(new LanguageEmbedField(true, "modlog.general.old_name", "modlog.general.variable", oldName));
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
            embedFieldList.add(new LanguageEmbedField(true, "modlog.channel.type.name", "modlog.channel.type." + type.name().toLowerCase()));
            embedFieldList.add(new LanguageEmbedField(true, "modlog.channel.old_pos", "modlog.general.variable", String.valueOf(oldPos)));
            embedFieldList.add(new LanguageEmbedField(true, "modlog.channel.new_pos", "modlog.general.variable", String.valueOf(channel.getPosition())));
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
                Color oldColor = ((RoleUpdateColorEvent) event).getOldColor();
                if (oldColor != null) {
                    embedFieldList.add(new LanguageEmbedField(true, "modlog.role.old_color", "modlog.general.variable", ColorUtils.getHex(oldColor.getRed(), oldColor.getGreen(), oldColor.getBlue())));
                }
                Color newColor = ((RoleUpdateColorEvent) event).getNewColor();
                if (newColor != null) {
                    embedFieldList.add(new LanguageEmbedField(true, "modlog.role.new_color", "modlog.general.variable", ColorUtils.getHex(newColor.getRed(), newColor.getGreen(), newColor.getBlue())));
                }
            } else if (event instanceof RoleUpdateHoistedEvent) {
                modlogEvent = ModlogEvent.ROLE_HOIST_UPDATED;
                embedFieldList.add(new LanguageEmbedField(true, "modlog.role.hoisted", "modlog.general.variable", String.valueOf(!((RoleUpdateHoistedEvent) event).wasHoisted())));
            } else if (event instanceof RoleUpdateMentionableEvent) {
                modlogEvent = ModlogEvent.ROLE_MENTIONABLE_UPDATED;
                embedFieldList.add(new LanguageEmbedField(true, "modlog.role.mention", "modlog.general.variable", String.valueOf(!((RoleUpdateMentionableEvent) event).wasMentionable())));
            } else if (event instanceof RoleUpdateNameEvent) {
                modlogEvent = ModlogEvent.ROLE_NAME_UPDATED;
                embedFieldList.add(new LanguageEmbedField(true, "modlog.general.old_name", "modlog.general.variable", ((RoleUpdateNameEvent) event).getOldName()));
            } else if (event instanceof RoleUpdatePermissionsEvent) {
                modlogEvent = ModlogEvent.ROLE_PERMISSIONS_UPDATED;
                EnumSet<Permission> oldPermissions = ((RoleUpdatePermissionsEvent) event).getOldPermissions();
                EnumSet<Permission> newPermissions = ((RoleUpdatePermissionsEvent) event).getNewPermissions();
                ListChanges<Permission> permissionListChanges = new ListChanges<>(oldPermissions, newPermissions);
                embedFieldList.add(new LanguageEmbedField(false, "modlog.role.added_perm", "modlog.general.variable", permissionListChanges.getAdded().stream().map(Permission::getName).collect(Collectors.joining("\n"))));
                embedFieldList.add(new LanguageEmbedField(false, "modlog.role.removed_perm", "modlog.general.variable", permissionListChanges.getRemoved().stream().map(Permission::getName).collect(Collectors.joining("\n"))));
            } else if (event instanceof RoleUpdatePositionEvent) {
                modlogEvent = ModlogEvent.ROLE_POSITION_UPDATED;
                embedFieldList.add(new LanguageEmbedField(true, "modlog.general.old_pos", "modlog.general.variable", String.valueOf(((RoleUpdatePositionEvent) event).getOldPosition())));
                embedFieldList.add(new LanguageEmbedField(true, "modlog.general.new_pos", "modlog.general.variable", String.valueOf(((RoleUpdatePositionEvent) event).getNewPosition())));
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
        embedFieldList.add(new LanguageEmbedField(true, "modlog.general.old_name", "modlog.general.variable", event.getOldName()));
        ModlogEventStore modlogEventStore = new ModlogEventStore(ModlogEvent.USER_NAME_UPDATED, event.getUser(), event.getUser(), embedFieldList);
        for (Guild guild : CascadeBot.INS.getClient().getMutualGuilds(event.getUser())) {
            GuildData guildData = GuildDataManager.getGuildData(guild.getIdLong());
            guildData.getModeration().sendModlogEvent(modlogEventStore);
        }
    }

    public void onUserUpdateDiscriminator(UserUpdateDiscriminatorEvent event) {
        List<LanguageEmbedField> embedFieldList = new ArrayList<>();
        embedFieldList.add(new LanguageEmbedField(true, "modlog.user.old_discrim", "modlog.general.variable", event.getOldDiscriminator()));
        ModlogEventStore modlogEventStore = new ModlogEventStore(ModlogEvent.USER_DISCRIMINATOR_UPDATED, event.getUser(), event.getUser(), embedFieldList);
        for (Guild guild : CascadeBot.INS.getClient().getMutualGuilds(event.getUser())) {
            GuildData guildData = GuildDataManager.getGuildData(guild.getIdLong());
            guildData.getModeration().sendModlogEvent(modlogEventStore);
        }
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
