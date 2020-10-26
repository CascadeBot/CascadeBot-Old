package org.cascadebot.cascadebot.events;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Category;
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
import net.dv8tion.jda.api.events.channel.text.update.TextChannelUpdateNSFWEvent;
import net.dv8tion.jda.api.events.channel.text.update.TextChannelUpdateNameEvent;
import net.dv8tion.jda.api.events.channel.text.update.TextChannelUpdateParentEvent;
import net.dv8tion.jda.api.events.channel.text.update.TextChannelUpdatePositionEvent;
import net.dv8tion.jda.api.events.channel.text.update.TextChannelUpdateSlowmodeEvent;
import net.dv8tion.jda.api.events.channel.text.update.TextChannelUpdateTopicEvent;
import net.dv8tion.jda.api.events.channel.voice.GenericVoiceChannelEvent;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.voice.update.VoiceChannelUpdateBitrateEvent;
import net.dv8tion.jda.api.events.channel.voice.update.VoiceChannelUpdateNameEvent;
import net.dv8tion.jda.api.events.channel.voice.update.VoiceChannelUpdateParentEvent;
import net.dv8tion.jda.api.events.channel.voice.update.VoiceChannelUpdatePositionEvent;
import net.dv8tion.jda.api.events.channel.voice.update.VoiceChannelUpdateUserLimitEvent;
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
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostCountEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostTierEvent;
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
import net.dv8tion.jda.api.events.guild.voice.GenericGuildVoiceEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceDeafenEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildDeafenEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildMuteEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMuteEvent;
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
import org.cascadebot.cascadebot.moderation.ModlogEmbedDescription;
import org.cascadebot.cascadebot.moderation.ModlogEmbedField;
import org.cascadebot.cascadebot.moderation.ModlogEmbedPart;
import org.cascadebot.cascadebot.moderation.ModlogEvent;
import org.cascadebot.cascadebot.runnables.ModlogChannelMoveCollectorRunnable;
import org.cascadebot.cascadebot.runnables.ModlogMemberRoleCollectorRunnable;
import org.cascadebot.cascadebot.utils.Attachment;
import org.cascadebot.cascadebot.utils.ColorUtils;
import org.cascadebot.cascadebot.utils.CryptUtils;
import org.cascadebot.cascadebot.utils.ModlogUtils;
import org.cascadebot.cascadebot.utils.SerializableMessage;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import java.awt.Color;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ModlogEventListener extends ListenerAdapter {

    private Map<Long, ModlogChannelMoveCollectorRunnable> moveRunnableMap = new HashMap<>();
    private Map<Long, ModlogMemberRoleCollectorRunnable> roleRunnableMap = new HashMap<>();

    public void onGenericEmote(GenericEmoteEvent event) {
        GuildData guildData = GuildDataManager.getGuildData(event.getGuild().getIdLong());
        Emote emote = event.getEmote();
        ModlogUtils.getAuditLogFromType(event.getGuild(), event.getEmote().getIdLong(), auditLogEntry -> {
            User user = null;
            if (auditLogEntry != null) {
                user = auditLogEntry.getUser();
            } else {
                CascadeBot.LOGGER.warn("Modlog: Failed to find emote audit log entry");
            }
            List<ModlogEmbedPart> embedFieldList = new ArrayList<>();
            ModlogEvent modlogEvent;
            if (event instanceof EmoteAddedEvent) {
                modlogEvent = ModlogEvent.EMOTE_CREATED;
            } else if (event instanceof EmoteRemovedEvent) {
                modlogEvent = ModlogEvent.EMOTE_DELETED;
            } else if (event instanceof EmoteUpdateNameEvent) {
                modlogEvent = ModlogEvent.EMOTE_UPDATED_NAME;

                var oldName = new ModlogEmbedField(false, "modlog.general.old_name", null);
                var newName = new ModlogEmbedField(false, "modlog.general.new_name", null);

                oldName.addValueObjects(((EmoteUpdateNameEvent) event).getOldName());
                newName.addValueObjects(((EmoteUpdateNameEvent) event).getNewName());

                embedFieldList.add(oldName);
                embedFieldList.add(newName);
            } else if (event instanceof EmoteUpdateRolesEvent) {
                modlogEvent = ModlogEvent.EMOTE_UPDATED_ROLES;

                List<Role> oldRoles = ((EmoteUpdateRolesEvent) event).getOldRoles();
                List<Role> newRoles = ((EmoteUpdateRolesEvent) event).getNewRoles();
                ListChanges<Role> roleListChanges = new ListChanges<>(oldRoles, newRoles);

                if (!roleListChanges.getAdded().isEmpty()) {
                    ModlogEmbedField addedRolesEmbed = new ModlogEmbedField(false, "modlog.general.added_roles", null);
                    addedRolesEmbed.addValueObjects(roleListChanges.getAdded().stream().map(role -> role.getName() + " (" + role.getId() + ")")
                            .collect(Collectors.joining("\n")));
                    embedFieldList.add(addedRolesEmbed);
                }
                if (!roleListChanges.getRemoved().isEmpty()) {
                    ModlogEmbedField removedRolesEmbed = new ModlogEmbedField(false, "modlog.general.removed_roles", null);
                    removedRolesEmbed.addValueObjects(roleListChanges.getRemoved().stream().map(role -> role.getName() + " (" + role.getId() + ")")
                            .collect(Collectors.joining("\n")));
                    embedFieldList.add(removedRolesEmbed);
                }
            } else {
                return;
            }
            ModlogEventStore eventStore = new ModlogEventStore(modlogEvent, user, emote, embedFieldList);
            guildData.getModeration().sendModlogEvent(event.getGuild().getIdLong(), eventStore);
        }, ActionType.EMOTE_CREATE, ActionType.EMOTE_DELETE, ActionType.EMOTE_UPDATE);
    }

    public void onGenericGuildMember(GenericGuildMemberEvent event) {
        GuildData guildData = GuildDataManager.getGuildData(event.getGuild().getIdLong());
        User user = event.getMember().getUser();
        ModlogUtils.getAuditLogFromType(event.getGuild(), event.getUser().getIdLong(), auditLogEntry -> {
            List<ModlogEmbedPart> embedFieldList = new ArrayList<>();
            ModlogEvent modlogEvent;
            User responsible = null;
            if (event instanceof GuildMemberJoinEvent) {
                modlogEvent = ModlogEvent.GUILD_MEMBER_JOINED;
                embedFieldList.add(new ModlogEmbedDescription("modlog.member.joined", user.getAsMention()));
            } else if (event instanceof GuildMemberLeaveEvent) {
                if (auditLogEntry != null && auditLogEntry.getType().equals(ActionType.KICK)) {
                    responsible = auditLogEntry.getUser();
                    embedFieldList.add(new ModlogEmbedDescription("modlog.member.kicked", user.getAsMention()));
                    if (auditLogEntry.getReason() != null) {
                        ModlogEmbedField reasonEmbedField = new ModlogEmbedField(false, "modlog.general.reason", null);
                        reasonEmbedField.addValueObjects(auditLogEntry.getReason());
                        embedFieldList.add(reasonEmbedField);
                    }
                    modlogEvent = ModlogEvent.GUILD_MEMBER_KICKED;
                } else {
                    CascadeBot.LOGGER.warn("Modlog: Failed to find kick entry");
                    modlogEvent = ModlogEvent.GUILD_MEMBER_LEFT;
                }
            } else if (event instanceof GuildMemberRoleAddEvent) {
                if (auditLogEntry != null && auditLogEntry.getTargetIdLong() == event.getMember().getIdLong()) {
                    responsible = auditLogEntry.getUser();
                }
                modlogEvent = ModlogEvent.GUILD_MEMBER_ROLE_ADDED;
                ModlogEmbedField addedRolesEmbedField = new ModlogEmbedField(false, "modlog.general.added_roles", null);
                addedRolesEmbedField.addValueObjects(((GuildMemberRoleAddEvent) event).getRoles().stream().map(role -> role.getName() + " (" + role.getId() + ")").collect(Collectors.joining("\n")));
                embedFieldList.add(addedRolesEmbedField);
            } else if (event instanceof GuildMemberRoleRemoveEvent) {
                if (auditLogEntry != null && auditLogEntry.getTargetIdLong() == event.getMember().getIdLong()) {
                    responsible = auditLogEntry.getUser();
                }
                modlogEvent = ModlogEvent.GUILD_MEMBER_ROLE_REMOVED;
                ModlogEmbedField removedRolesEmbedField = new ModlogEmbedField(false, "modlog.general.removed_roles", null);
                removedRolesEmbedField.addValueObjects(((GuildMemberRoleRemoveEvent) event).getRoles().stream().map(role -> role.getName() + " (" + role.getId() + ")").collect(Collectors.joining("\n")));
                embedFieldList.add(removedRolesEmbedField);
            } else if (event instanceof GuildMemberUpdateNicknameEvent) {
                if (auditLogEntry != null && auditLogEntry.getType() == ActionType.MEMBER_UPDATE) {
                    responsible = auditLogEntry.getUser();
                }
                if (((GuildMemberUpdateNicknameEvent) event).getOldValue() != null) {
                    ModlogEmbedField oldNickEmbedField = new ModlogEmbedField(false, "modlog.member.old_nick", null);
                    oldNickEmbedField.addValueObjects(((GuildMemberUpdateNicknameEvent) event).getOldValue());
                    embedFieldList.add(oldNickEmbedField);
                }
                if (((GuildMemberUpdateNicknameEvent) event).getNewValue() != null) {
                    ModlogEmbedField newNickEmbedField = new ModlogEmbedField(false, "modlog.member.new_nick", null);
                    newNickEmbedField.addValueObjects(((GuildMemberUpdateNicknameEvent) event).getNewValue());
                    embedFieldList.add(newNickEmbedField);
                }
                modlogEvent = ModlogEvent.GUILD_MEMBER_NICKNAME_UPDATED;
            } else {
                return;
            }
            ModlogEventStore eventStore = new ModlogEventStore(modlogEvent, responsible, user, embedFieldList);
            guildData.getModeration().sendModlogEvent(event.getGuild().getIdLong(), eventStore);
        }, ActionType.KICK, ActionType.MEMBER_ROLE_UPDATE);
    }

    //region Ban events
    public void onGuildBan(GuildBanEvent event) {
        GuildData guildData = GuildDataManager.getGuildData(event.getGuild().getIdLong());
        User user = event.getUser();
        ModlogUtils.getAuditLogFromType(event.getGuild(), event.getUser().getIdLong(), auditLogEntry -> {
            List<ModlogEmbedPart> embedFieldList = new ArrayList<>();
            ModlogEvent modlogEvent = ModlogEvent.GUILD_USER_BANNED;
            User responsible = null;
            if (auditLogEntry != null) {
                responsible = auditLogEntry.getUser();
                embedFieldList.add(new ModlogEmbedDescription("modlog.member.banned", user.getAsMention()));
                if (auditLogEntry.getReason() != null) {
                    ModlogEmbedField reasonEmbedField = new ModlogEmbedField(false, "modlog.general.reason", null);
                    reasonEmbedField.addValueObjects(auditLogEntry.getReason());
                    embedFieldList.add(reasonEmbedField);
                }
            } else {
                CascadeBot.LOGGER.warn("Modlog: Failed to find ban entry");
            }
            ModlogEventStore eventStore = new ModlogEventStore(modlogEvent, responsible, user, embedFieldList);
            guildData.getModeration().sendModlogEvent(event.getGuild().getIdLong(), eventStore);
        }, ActionType.BAN);
    }

    public void onGuildUnban(GuildUnbanEvent event) {
        GuildData guildData = GuildDataManager.getGuildData(event.getGuild().getIdLong());
        User user = event.getUser();
        ModlogUtils.getAuditLogFromType(event.getGuild(), event.getUser().getIdLong(), auditLogEntry -> {
            List<ModlogEmbedPart> embedFieldList = new ArrayList<>();
            ModlogEvent modlogEvent = ModlogEvent.GUILD_USER_UNBANNED;
            User responsible = null;
            if (auditLogEntry != null) {
                responsible = auditLogEntry.getUser();
            }  else {
                CascadeBot.LOGGER.warn("Modlog: Failed to find unban entry");
            }
            ModlogEventStore eventStore = new ModlogEventStore(modlogEvent, responsible, user, embedFieldList);
            guildData.getModeration().sendModlogEvent(event.getGuild().getIdLong(), eventStore);
        }, ActionType.UNBAN);
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
        List<ModlogEmbedPart> embedFieldList = new ArrayList<>();
        ModlogEmbedField messageEmbedField = new ModlogEmbedField(false, "modlog.message.message", null);
        messageEmbedField.addValueObjects(message.getContent());
        embedFieldList.add(messageEmbedField);
        if (message.getAttachments().size() > 0) {
            StringBuilder attachmentsBuilder = new StringBuilder();
            for (Attachment attachment : message.getAttachments()) {
                attachmentsBuilder.append(attachment.getUrl()).append('\n');
            }
            embedFieldList.add(new ModlogEmbedField(false, "modlog.message.attachments", null, attachmentsBuilder.toString()));
        }
        if (affected == null) {
            return;
        }
        //TODO handle embeds/ect...
        ModlogUtils.getAuditLogFromType(event.getGuild(), message.getAuthorId(), auditLogEntry -> {
            User responsible;
            if (auditLogEntry != null) {
                responsible = auditLogEntry.getUser();
            } else {
                CascadeBot.LOGGER.warn("Modlog: Failed to find message delete entry");
                responsible = null;
            }
            if (message.getUserMentions().size() > 0 || message.getRoleMentions().size() > 0) {
                ModlogEventStore eventStore = new ModlogEventStore(ModlogEvent.GUILD_MESSAGE_DELETED_MENTION, responsible, affected, embedFieldList);
                guildData.getModeration().sendModlogEvent(event.getGuild().getIdLong(), eventStore);
            }
            ModlogEventStore eventStore = new ModlogEventStore(ModlogEvent.GUILD_MESSAGE_DELETED, responsible, affected, embedFieldList);
            guildData.getModeration().sendModlogEvent(event.getGuild().getIdLong(), eventStore);
        }, ActionType.MESSAGE_DELETE);
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
        List<ModlogEmbedPart> embedFieldList = new ArrayList<>();
        ModlogEmbedField oldEmbedField = new ModlogEmbedField(false, "modlog.message.old_message", null);
        oldEmbedField.addValueObjects(oldMessage.getContent());
        ModlogEmbedField newEmbedField = new ModlogEmbedField(false, "modlog.message.new_message", null);
        newEmbedField.addValueObjects(message.getContentRaw());
        // TODO handle embeds/ect...
        embedFieldList.add(oldEmbedField);
        embedFieldList.add(newEmbedField);
        ModlogEvent modlogEvent = ModlogEvent.GUILD_MESSAGE_UPDATED;
        ModlogEventStore eventStore = new ModlogEventStore(modlogEvent, null, affected, embedFieldList);
        guildData.getModeration().sendModlogEvent(event.getGuild().getIdLong(), eventStore);
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
        ModlogUtils.getAuditLogFromType(event.getGuild(), auditLogEntry -> {
            List<ModlogEmbedPart> embedFieldList = new ArrayList<>();
            User responsible = null;
            ModlogEvent modlogEvent;
            if (auditLogEntry != null) {
                responsible = auditLogEntry.getUser();
            } else {
                CascadeBot.LOGGER.warn("Modlog: Failed to find guild update entry");
            }
            if (event instanceof GuildUpdateAfkChannelEvent) {
                VoiceChannel oldChannel = ((GuildUpdateAfkChannelEvent) event).getOldAfkChannel();
                if (oldChannel != null) {
                    embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.old_channel", null, oldChannel.getName()));
                }
                VoiceChannel newChannel = ((GuildUpdateAfkChannelEvent) event).getNewAfkChannel();
                if (newChannel != null) {
                    embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.new_channel", null, newChannel.getName()));
                }
                modlogEvent = ModlogEvent.GUILD_UPDATE_AFK_CHANNEL;
            } else if (event instanceof GuildUpdateAfkTimeoutEvent) {
                embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.old_timeout", "modlog.guild.timeout", String.valueOf(((GuildUpdateAfkTimeoutEvent) event).getOldAfkTimeout().getSeconds())));
                embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.new_timeout", "modlog.guild.timeout", String.valueOf(((GuildUpdateAfkTimeoutEvent) event).getNewAfkTimeout().getSeconds())));
                modlogEvent = ModlogEvent.GUILD_UPDATE_AFK_TIMEOUT;
            } else if (event instanceof GuildUpdateBannerEvent) {
                if (((GuildUpdateBannerEvent) event).getOldBannerUrl() != null) {
                    embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.old_image", null, ((GuildUpdateBannerEvent) event).getOldBannerUrl()));
                }
                if (((GuildUpdateBannerEvent) event).getNewBannerUrl() != null) {
                    embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.new_image", null, ((GuildUpdateBannerEvent) event).getNewBannerUrl()));
                }
                modlogEvent = ModlogEvent.GUILD_UPDATE_BANNER;
            } else if (event instanceof GuildUpdateDescriptionEvent) {
                if (((GuildUpdateDescriptionEvent) event).getOldDescription() != null) {
                    embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.old_description", null, ((GuildUpdateDescriptionEvent) event).getOldDescription()));
                }
                if (((GuildUpdateDescriptionEvent) event).getNewDescription() != null) {
                    embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.new_description", null, ((GuildUpdateDescriptionEvent) event).getNewDescription()));
                }
                modlogEvent = ModlogEvent.GUILD_UPDATE_DESCRIPTION;
            } else if (event instanceof GuildUpdateExplicitContentLevelEvent) {
                embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.content_filter.old", "modlog.guild.content_filter." + ((GuildUpdateExplicitContentLevelEvent) event).getOldLevel().name().toLowerCase()));
                embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.content_filter.new", "modlog.guild.content_filter." + ((GuildUpdateExplicitContentLevelEvent) event).getNewLevel().name().toLowerCase()));
                modlogEvent = ModlogEvent.GUILD_UPDATE_EXPLICIT_FILTER;
            } else if (event instanceof GuildUpdateFeaturesEvent) {
                ListChanges<String> featuresChanged = new ListChanges<>(((GuildUpdateFeaturesEvent) event).getOldFeatures(), ((GuildUpdateFeaturesEvent) event).getNewFeatures());
                embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.add_feature", null, String.join("\n", featuresChanged.getAdded())));
                embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.removed_feature", null, String.join("\n", featuresChanged.getRemoved())));
                modlogEvent = ModlogEvent.GUILD_UPDATE_FEATURES;
            } else if (event instanceof GuildUpdateIconEvent) {
                if (((GuildUpdateIconEvent) event).getOldIconUrl() != null) {
                    embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.old_image", null, ((GuildUpdateIconEvent) event).getOldIconUrl()));
                }
                if (((GuildUpdateIconEvent) event).getNewIconUrl() != null) {
                    embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.new_image", null, ((GuildUpdateIconEvent) event).getNewIconUrl()));
                }
                modlogEvent = ModlogEvent.GUILD_UPDATE_ICON;
            } else if (event instanceof GuildUpdateMaxMembersEvent) {
                embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.old_max_members", "modlog.guild.members", String.valueOf(((GuildUpdateMaxMembersEvent) event).getOldMaxMembers())));
                embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.new_max_members", "modlog.guild.members", String.valueOf(((GuildUpdateMaxMembersEvent) event).getNewMaxMembers())));
                modlogEvent = ModlogEvent.GUILD_UPDATE_MAX_MEMBERS;
            } else if (event instanceof GuildUpdateMaxPresencesEvent) {
                embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.old_presences", "modlog.guild.presences", String.valueOf(((GuildUpdateMaxPresencesEvent) event).getOldMaxPresences())));
                embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.new_presences", "modlog.guild.presences", String.valueOf(((GuildUpdateMaxPresencesEvent) event).getNewMaxPresences())));
                modlogEvent = ModlogEvent.GUILD_UPDATE_MAX_PRESENCES;
            } else if (event instanceof GuildUpdateMFALevelEvent) {
                embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.mfa.old", "modlog.guild.mfa." + ((GuildUpdateMFALevelEvent) event).getOldMFALevel().name().toLowerCase()));
                embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.mfa.new", "modlog.guild.mfa." + ((GuildUpdateMFALevelEvent) event).getNewMFALevel().name().toLowerCase()));
                modlogEvent = ModlogEvent.GUILD_UPDATE_MFA_LEVEL;
            } else if (event instanceof GuildUpdateNameEvent) {
                embedFieldList.add(new ModlogEmbedField(false, "modlog.general.old_name", null, ((GuildUpdateNameEvent) event).getOldName()));
                modlogEvent = ModlogEvent.GUILD_UPDATE_NAME;
            } else if (event instanceof GuildUpdateNotificationLevelEvent) {
                embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.notification.old", "modlog.guild.notification." + ((GuildUpdateNotificationLevelEvent) event).getOldNotificationLevel().name().toLowerCase()));
                embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.notification.new", "modlog.guild.notification." + ((GuildUpdateNotificationLevelEvent) event).getNewNotificationLevel().name().toLowerCase()));
                modlogEvent = ModlogEvent.GUILD_UPDATE_NOTIFICATION_LEVEL;
            } else if (event instanceof GuildUpdateRegionEvent) {
                embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.old_region", null, ((GuildUpdateRegionEvent) event).getOldRegion().getName()));
                embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.new_region", null, ((GuildUpdateRegionEvent) event).getNewRegion().getName()));
                modlogEvent = ModlogEvent.GUILD_UPDATE_REGION;
            } else if (event instanceof GuildUpdateSplashEvent) {
                if (((GuildUpdateSplashEvent) event).getOldSplashUrl() != null) {
                    embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.old_splash", null, ((GuildUpdateSplashEvent) event).getOldSplashUrl()));
                }
                if (((GuildUpdateSplashEvent) event).getNewSplashUrl() != null) {
                    embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.new_splash", null, ((GuildUpdateSplashEvent) event).getNewSplashUrl()));
                }
                modlogEvent = ModlogEvent.GUILD_UPDATE_SPLASH;
            } else if (event instanceof GuildUpdateSystemChannelEvent) {
                TextChannel oldSystemChannel = ((GuildUpdateSystemChannelEvent) event).getOldSystemChannel();
                if (oldSystemChannel != null) {
                    embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.old_sys", null, oldSystemChannel.getName()));
                }
                TextChannel newSystemChannel = ((GuildUpdateSystemChannelEvent) event).getNewSystemChannel();
                if (newSystemChannel != null) {
                    embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.new_sys", null, newSystemChannel.getName()));
                }
                modlogEvent = ModlogEvent.GUILD_UPDATE_SYSTEM_CHANNEL;
            } else if (event instanceof GuildUpdateVanityCodeEvent) {
                if (((GuildUpdateVanityCodeEvent) event).getOldVanityCode() != null) {
                    embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.vanity_code.old", null, ((GuildUpdateVanityCodeEvent) event).getOldVanityCode()));
                }
                if (((GuildUpdateVanityCodeEvent) event).getOldVanityUrl() != null) {
                    embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.vanity_url.old", null, ((GuildUpdateVanityCodeEvent) event).getOldVanityUrl()));
                }
                if (((GuildUpdateVanityCodeEvent) event).getNewVanityCode() != null) {
                    embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.vanity_code.new", null, ((GuildUpdateVanityCodeEvent) event).getNewVanityCode()));
                }
                if (((GuildUpdateVanityCodeEvent) event).getNewVanityUrl() != null) {
                    embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.vanity_url.new", null, ((GuildUpdateVanityCodeEvent) event).getNewVanityUrl()));
                }
                modlogEvent = ModlogEvent.GUILD_UPDATE_VANITY_CODE;
            } else if (event instanceof GuildUpdateVerificationLevelEvent) {
                embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.old_verification", "utils.verification_level." + ((GuildUpdateVerificationLevelEvent) event).getOldVerificationLevel().name().toLowerCase()));
                embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.new_verification", "utils.verification_level." + ((GuildUpdateVerificationLevelEvent) event).getNewVerificationLevel().name().toLowerCase()));
                modlogEvent = ModlogEvent.GUILD_UPDATE_VERIFICATION_LEVEL;
            } else if (event instanceof GuildUpdateBoostCountEvent) {
                embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.old_boost_count", null, String.valueOf(event.getOldValue())));
                embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.new_boost_count", null, String.valueOf(event.getNewValue())));
                modlogEvent = ModlogEvent.GUILD_BOOST_COUNT_UPDATED;
            } else if (event instanceof GuildUpdateBoostTierEvent) {
                embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.old_boost_tier", "modlog.guild.boost_tier." + ((GuildUpdateBoostTierEvent) event).getOldBoostTier().name().toLowerCase()));
                embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.new_boost_tier", "modlog.guild.boost_tier." + ((GuildUpdateBoostTierEvent) event).getNewBoostTier().name().toLowerCase()));
                modlogEvent = ModlogEvent.GUILD_BOOST_TIER_UPDATED;
            } else {
                return;
            }
            ModlogEventStore eventStore = new ModlogEventStore(modlogEvent, responsible, affected, embedFieldList);
            guildData.getModeration().sendModlogEvent(event.getGuild().getIdLong(), eventStore);
        }, ActionType.GUILD_UPDATE);
    }

    public void onGenericGuildVoice(GenericGuildVoiceEvent event) {
        User affected = event.getMember().getUser();
        Guild guild = event.getGuild();
        GuildData guildData = GuildDataManager.getGuildData(guild.getIdLong());
        ModlogUtils.getAuditLogFromType(event.getGuild(), event.getMember().getIdLong(), auditLogEntry -> {
            List<ModlogEmbedPart> embedFieldList = new ArrayList<>();
            ModlogEvent action;
            if (event instanceof GuildVoiceDeafenEvent) {
                embedFieldList.add(new ModlogEmbedField(false, "modlog.voice.deafen", null, String.valueOf(((GuildVoiceDeafenEvent) event).isDeafened())));
                action = ModlogEvent.VOICE_DEAFEN;
            } else if (event instanceof GuildVoiceMuteEvent) {
                embedFieldList.add(new ModlogEmbedField(false, "modlog.voice.mute", null, String.valueOf(((GuildVoiceMuteEvent) event).isMuted())));
                action = ModlogEvent.VOICE_MUTE;
            } else if (event instanceof GuildVoiceGuildDeafenEvent) {
                embedFieldList.add(new ModlogEmbedField(false, "modlog.voice.deafen", null, String.valueOf(((GuildVoiceGuildDeafenEvent) event).isGuildDeafened())));
                action = ModlogEvent.VOICE_SERVER_DEAFEN;
            } else if (event instanceof GuildVoiceGuildMuteEvent) {
                embedFieldList.add(new ModlogEmbedField(false, "modlog.voice.mute", null, String.valueOf(((GuildVoiceGuildMuteEvent) event).isGuildMuted())));
                action = ModlogEvent.VOICE_SERVER_MUTE;
            } else if (event instanceof GuildVoiceJoinEvent) {
                embedFieldList.add(new ModlogEmbedField(false, "modlog.voice.join", null, ((GuildVoiceJoinEvent) event).getChannelJoined().getName()));
                action = ModlogEvent.VOICE_JOIN;
            } else if (event instanceof GuildVoiceLeaveEvent) {
                embedFieldList.add(new ModlogEmbedField(false, "modlog.voice.left", null, ((GuildVoiceLeaveEvent) event).getChannelLeft().getName()));
                if (auditLogEntry != null) {
                    action = ModlogEvent.VOICE_DISCONNECT;
                } else {
                    action = ModlogEvent.VOICE_LEAVE;
                }
            } else if (event instanceof GuildVoiceMoveEvent) {
                embedFieldList.add(new ModlogEmbedField(false, "modlog.voice.left", null, ((GuildVoiceMoveEvent) event).getChannelLeft().getName()));
                embedFieldList.add(new ModlogEmbedField(false, "modlog.voice.join", null, ((GuildVoiceMoveEvent) event).getChannelJoined().getName()));
                if (auditLogEntry != null) {
                    action = ModlogEvent.VOICE_FORCE_MOVE;
                } else {
                    action = ModlogEvent.VOICE_MOVE;
                }
            } else {
                return;
            }
            ModlogEventStore eventStore = new ModlogEventStore(action, null, affected, embedFieldList);
            guildData.getModeration().sendModlogEvent(event.getGuild().getIdLong(), eventStore);
        }, ActionType.MEMBER_VOICE_MOVE, ActionType.MEMBER_VOICE_KICK);
    }

    //region Channels
    public void onGenericStoreChannel(GenericStoreChannelEvent event) {
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

    public void onGenericTextChannel(GenericTextChannelEvent event) {
        if (event instanceof TextChannelCreateEvent) {
            handleChannelCreateEvents(event.getGuild(), ChannelType.TEXT, event.getChannel());
            return;
        } else if (event instanceof TextChannelDeleteEvent) {
            handleChannelDeleteEvents(event.getGuild(), ChannelType.TEXT, event.getChannel());
            return;
        } else if (event instanceof TextChannelUpdateNameEvent) {
            handleChannelUpdateNameEvents(event.getGuild(), ChannelType.TEXT, ((TextChannelUpdateNameEvent) event).getOldName(), event.getChannel());
            return;
        } else if (event instanceof TextChannelUpdatePositionEvent) {
            handleChannelUpdatePositionEvents(event.getGuild(), ChannelType.TEXT, ((TextChannelUpdatePositionEvent) event).getOldPosition(), event.getChannel());
            return;
        } else if (event instanceof TextChannelUpdateParentEvent) {
            handleChannelUpdateParentEvents(event.getGuild(), ChannelType.TEXT, ((TextChannelUpdateParentEvent) event).getOldParent(), ((TextChannelUpdateParentEvent) event).getNewParent(), event.getChannel());
            return;
        }
        GuildData guildData = GuildDataManager.getGuildData(event.getGuild().getIdLong());
        ModlogUtils.getAuditLogFromType(event.getGuild(), event.getChannel().getIdLong(), auditLogEntry -> {
            ModlogEvent trigger;
            List<ModlogEmbedPart> embedFieldList = new ArrayList<>();
            User responsible = null;
            if (auditLogEntry != null) {
                responsible = auditLogEntry.getUser();
            } else {
                CascadeBot.LOGGER.warn("Modlog: Failed to find channel update entry");
            }
            if (event instanceof TextChannelUpdateNSFWEvent) {
                embedFieldList.add(new ModlogEmbedField(false, "modlog.channel.nsfw", null, String.valueOf(!((TextChannelUpdateNSFWEvent) event).getOldNSFW())));
                trigger = ModlogEvent.TEXT_CHANNEL_NSFW_UPDATED;
            } else if (event instanceof TextChannelUpdateSlowmodeEvent) {
                embedFieldList.add(new ModlogEmbedField(false, "modlog.channel.old_slowmode", null, String.valueOf(((TextChannelUpdateSlowmodeEvent) event).getOldSlowmode())));
                embedFieldList.add(new ModlogEmbedField(false, "modlog.channel.new_slowmode", null, String.valueOf(((TextChannelUpdateSlowmodeEvent) event).getNewSlowmode())));
                trigger = ModlogEvent.TEXT_CHANNEL_SLOWMODE_UPDATED;
            } else if (event instanceof TextChannelUpdateTopicEvent) {
                embedFieldList.add(new ModlogEmbedField(false, "modlog.channel.old_topic", null, ((TextChannelUpdateTopicEvent) event).getOldTopic()));
                embedFieldList.add(new ModlogEmbedField(false, "modlog.channel.new_topic", null, ((TextChannelUpdateTopicEvent) event).getNewTopic()));
                trigger = ModlogEvent.TEXT_CHANNEL_TOPIC_UPDATED;
            } else {
                return;
            }
            ModlogEventStore eventStore = new ModlogEventStore(trigger, responsible, event.getChannel(), embedFieldList);
            guildData.getModeration().sendModlogEvent(event.getGuild().getIdLong(), eventStore);
        }, ActionType.CHANNEL_UPDATE);
    }

    public void onGenericVoiceChannel(GenericVoiceChannelEvent event) {
        if (event instanceof VoiceChannelCreateEvent) {
            handleChannelCreateEvents(event.getGuild(), ChannelType.VOICE, event.getChannel());
        } else if (event instanceof VoiceChannelDeleteEvent) {
            handleChannelDeleteEvents(event.getGuild(), ChannelType.VOICE, event.getChannel());
        } else if (event instanceof VoiceChannelUpdateNameEvent) {
            handleChannelUpdateNameEvents(event.getGuild(), ChannelType.VOICE, ((VoiceChannelUpdateNameEvent) event).getOldName(), event.getChannel());
        } else if (event instanceof VoiceChannelUpdatePositionEvent) {
            handleChannelUpdatePositionEvents(event.getGuild(), ChannelType.VOICE, ((VoiceChannelUpdatePositionEvent) event).getOldPosition(), event.getChannel());
        } else if (event instanceof VoiceChannelUpdateParentEvent) {
            handleChannelUpdateParentEvents(event.getGuild(), ChannelType.VOICE, ((VoiceChannelUpdateParentEvent) event).getOldParent(), ((VoiceChannelUpdateParentEvent) event).getNewParent(), event.getChannel());
        }
        GuildData guildData = GuildDataManager.getGuildData(event.getGuild().getIdLong());
        ModlogUtils.getAuditLogFromType(event.getGuild(), event.getChannel().getIdLong(), auditLogEntry -> {
            ModlogEvent trigger;
            List<ModlogEmbedPart> embedFieldList = new ArrayList<>();
            User responsible = null;
            if (auditLogEntry != null) {
                responsible = auditLogEntry.getUser();
            } else {
                CascadeBot.LOGGER.warn("Modlog: Failed to find channel update entry");
            }
            if (event instanceof VoiceChannelUpdateBitrateEvent) {
                embedFieldList.add(new ModlogEmbedField(false, "modlog.channel.old_bitrate", "modlog.channel.kpbs", String.valueOf(((VoiceChannelUpdateBitrateEvent) event).getOldBitrate())));
                embedFieldList.add(new ModlogEmbedField(false, "modlog.channel.new_bitrate", "modlog.channel.kpbs", String.valueOf(((VoiceChannelUpdateBitrateEvent) event).getNewBitrate())));
                trigger = ModlogEvent.VOICE_CHANNEL_BITRATE_UPDATED;
            } else if (event instanceof VoiceChannelUpdateUserLimitEvent) {
                embedFieldList.add(new ModlogEmbedField(false, "modlog.channel.old_users", "modlog.channel.user", String.valueOf(((VoiceChannelUpdateUserLimitEvent) event).getOldUserLimit())));
                embedFieldList.add(new ModlogEmbedField(false, "modlog.channel.new_users", "modlog.channel.user", String.valueOf(((VoiceChannelUpdateUserLimitEvent) event).getNewUserLimit())));
                trigger = ModlogEvent.VOICE_CHANNEL_USER_LIMIT_UPDATED;
            } else {
                return;
            }
            ModlogEventStore eventStore = new ModlogEventStore(trigger, responsible, event.getChannel(), embedFieldList);
            guildData.getModeration().sendModlogEvent(event.getGuild().getIdLong(), eventStore);
        }, ActionType.CHANNEL_UPDATE);
    }

    public void onGenericCategory(GenericCategoryEvent event) {
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
        ModlogUtils.getAuditLogFromType(event.getGuild(), event.getChannel().getIdLong(), auditLogEntry -> {
            List<ModlogEmbedPart> embedFieldList = new ArrayList<>();
            User responsible = null;
            if (auditLogEntry != null) {
                responsible = auditLogEntry.getUser();
            } else {
                CascadeBot.LOGGER.warn("Modlog: Failed to find channel update entry");
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
                    ModlogEmbedField oldAllowed = new ModlogEmbedField(false, "modlog.channel.perm.old_allow", null,
                            ((PermissionOverrideUpdateEvent) event).getOldAllow().stream().map(Permission::getName).collect(Collectors.joining("\n")));
                    oldAllowed.addTitleObjects(permissionsHolderName);
                    embedFieldList.add(oldAllowed);
                }
                if (((PermissionOverrideUpdateEvent) event).getOldDeny().size() > 0) {
                    ModlogEmbedField oldDenied = new ModlogEmbedField(false, "modlog.channel.perm.old_allow", null,
                            ((PermissionOverrideUpdateEvent) event).getOldDeny().stream().map(Permission::getName).collect(Collectors.joining("\n")));
                    oldDenied.addTitleObjects(permissionsHolderName);
                    embedFieldList.add(oldDenied);
                }
            } else {
                return;
            }
            if (event.getPermissionOverride().getAllowed().size() > 0) {
                ModlogEmbedField allowed = new ModlogEmbedField(false, allowedPath, null, event.getPermissionOverride().getAllowed().stream().map(Permission::getName).collect(Collectors.joining("\n")));
                allowed.addTitleObjects(permissionsHolderName);
                embedFieldList.add(allowed);
            }
            if (event.getPermissionOverride().getDenied().size() > 0) {
                ModlogEmbedField denied = new ModlogEmbedField(false, deniedPath, null, event.getPermissionOverride().getDenied().stream().map(Permission::getName).collect(Collectors.joining("\n")));
                denied.addTitleObjects(permissionsHolderName);
                embedFieldList.add(denied);
            }
            embedFieldList.add(new ModlogEmbedField(false, "modlog.channel.type.name", "modlog.channel.type." + event.getChannelType().name().toLowerCase()));
            ModlogEventStore modlogEventStore = new ModlogEventStore(modlogEvent, responsible, event.getChannel(), embedFieldList);
            guildData.getModeration().sendModlogEvent(event.getGuild().getIdLong(), modlogEventStore);
        }, ActionType.CHANNEL_OVERRIDE_UPDATE, ActionType.CHANNEL_UPDATE);
    }

    //endregion

    //region Channel handlers
    private void handleChannelCreateEvents(Guild guild, ChannelType type, GuildChannel channel) {
        ModlogEvent event = ModlogEvent.CHANNEL_CREATED;
        GuildData guildData = GuildDataManager.getGuildData(guild.getIdLong());
        ModlogUtils.getAuditLogFromType(guild, channel.getIdLong(), auditLogEntry -> {
            List<ModlogEmbedPart> embedFieldList = new ArrayList<>();
            User responsible = null;
            if (auditLogEntry != null) {
                responsible = auditLogEntry.getUser();
            } else {
                CascadeBot.LOGGER.warn("Modlog: Failed to find channel create entry");
            }
            embedFieldList.add(new ModlogEmbedField(false, "modlog.channel.type.name", "modlog.channel.type." + type.name().toLowerCase()));
            ModlogEventStore modlogEventStore = new ModlogEventStore(event, responsible, channel, embedFieldList);
            guildData.getModeration().sendModlogEvent(guild.getIdLong(), modlogEventStore);
        }, ActionType.CHANNEL_CREATE);
    }

    private void handleChannelDeleteEvents(Guild guild, ChannelType type, GuildChannel channel) {
        ModlogEvent event = ModlogEvent.CHANNEL_DELETED;
        GuildData guildData = GuildDataManager.getGuildData(guild.getIdLong());
        ModlogUtils.getAuditLogFromType(guild, channel.getIdLong(), auditLogEntry -> {
            List<ModlogEmbedPart> embedFieldList = new ArrayList<>();
            User responsible = null;
            if (auditLogEntry != null) {
                responsible = auditLogEntry.getUser();
            } else {
                CascadeBot.LOGGER.warn("Modlog: Failed to find channel delete entry");
            }
            embedFieldList.add(new ModlogEmbedField(false, "modlog.channel.type.name", "modlog.channel.type." + type.name().toLowerCase()));
            ModlogEventStore modlogEventStore = new ModlogEventStore(event, responsible, channel, embedFieldList);
            guildData.getModeration().sendModlogEvent(guild.getIdLong(), modlogEventStore);
        }, ActionType.CHANNEL_DELETE);
    }

    private void handleChannelUpdateNameEvents(Guild guild, ChannelType type, String oldName, GuildChannel channel) {
        ModlogEvent event = ModlogEvent.CHANNEL_NAME_UPDATED;
        GuildData guildData = GuildDataManager.getGuildData(guild.getIdLong());
        ModlogUtils.getAuditLogFromType(guild, channel.getIdLong(), auditLogEntry -> {
            List<ModlogEmbedPart> embedFieldList = new ArrayList<>();
            User responsible = null;
            if (auditLogEntry != null) {
                responsible = auditLogEntry.getUser();
            } else {
                CascadeBot.LOGGER.warn("Modlog: Failed to find channel update entry");
            }
            embedFieldList.add(new ModlogEmbedField(false, "modlog.channel.type.name", "modlog.channel.type." + type.name().toLowerCase()));
            embedFieldList.add(new ModlogEmbedField(false, "modlog.general.old_name", null, oldName));
            ModlogEventStore modlogEventStore = new ModlogEventStore(event, responsible, channel, embedFieldList);
            guildData.getModeration().sendModlogEvent(guild.getIdLong(), modlogEventStore);
        }, ActionType.CHANNEL_UPDATE);
    }

    private void handleChannelUpdatePositionEvents(Guild guild, ChannelType type, int oldPos, GuildChannel channel) {
        ModlogChannelMoveCollectorRunnable.ChannelMoveData moveData =
                new ModlogChannelMoveCollectorRunnable.ChannelMoveData(type, oldPos, channel);
        if (moveRunnableMap.containsKey(guild.getIdLong())) {
            moveRunnableMap.get(guild.getIdLong()).getQueue().add(moveData);
        } else {
            ModlogChannelMoveCollectorRunnable runnable = new ModlogChannelMoveCollectorRunnable(guild, new Runnable() {
                @Override
                public void run() {
                    moveRunnableMap.remove(guild.getIdLong());
                }
            });
            moveRunnableMap.put(guild.getIdLong(), runnable);
            new Thread(runnable, "modlog-channel-move-" + guild.getId()).start();
        }
    }

    public void handleChannelUpdateParentEvents(Guild guild, ChannelType type, Category oldParent, Category newParent, GuildChannel channel) {
        ModlogEvent event = ModlogEvent.CHANNEL_PARENT_UPDATED;
        GuildData guildData = GuildDataManager.getGuildData(guild.getIdLong());
        ModlogUtils.getAuditLogFromType(guild, channel.getIdLong(), auditLogEntry -> {
            List<ModlogEmbedPart> embedFieldList = new ArrayList<>();
            User responsible = null;
            if (auditLogEntry != null) {
                responsible = auditLogEntry.getUser();
            } else {
                CascadeBot.LOGGER.warn("Modlog: Failed to find channel update entry");
            }
            embedFieldList.add(new ModlogEmbedField(false, "modlog.channel.type.name", "modlog.channel.type." + type.name().toLowerCase()));
            embedFieldList.add(new ModlogEmbedField(false, "modlog.channel.old_parent", null, oldParent.getName()));
            embedFieldList.add(new ModlogEmbedField(false, "modlog.channel.new_parent", null, newParent.getName()));
            ModlogEventStore modlogEventStore = new ModlogEventStore(event, responsible, channel, embedFieldList);
            guildData.getModeration().sendModlogEvent(guild.getIdLong(), modlogEventStore);
        }, ActionType.CHANNEL_UPDATE);
    }
    //endregion

    public void onGenericRole(GenericRoleEvent event) {
        GuildData guildData = GuildDataManager.getGuildData(event.getGuild().getIdLong());
        ModlogUtils.getAuditLogFromType(event.getGuild(), event.getRole().getIdLong(), auditLogEntry -> {
            List<ModlogEmbedPart> embedFieldList = new ArrayList<>();
            List<String> descriptionStuff = new ArrayList<>();
            User responsible = null;
            ModlogEvent modlogEvent;
            if (auditLogEntry != null) {
                responsible = auditLogEntry.getUser();
            } else {
                CascadeBot.LOGGER.warn("Modlog: Failed to find role audit log entry");
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
                    embedFieldList.add(new ModlogEmbedField(false, "modlog.role.old_color", null, ColorUtils.getHex(oldColor.getRed(), oldColor.getGreen(), oldColor.getBlue())));
                }
                Color newColor = ((RoleUpdateColorEvent) event).getNewColor();
                if (newColor != null) {
                    embedFieldList.add(new ModlogEmbedField(false, "modlog.role.new_color", null, ColorUtils.getHex(newColor.getRed(), newColor.getGreen(), newColor.getBlue())));
                }
            } else if (event instanceof RoleUpdateHoistedEvent) {
                modlogEvent = ModlogEvent.ROLE_HOIST_UPDATED;
                var wasHoisted = ((RoleUpdateHoistedEvent) event).wasHoisted();
                Emote emote = !wasHoisted ? CascadeBot.INS.getShardManager().getEmoteById(Config.INS.getGlobalEmotes().get("tick")) : CascadeBot.INS.getShardManager().getEmoteById(Config.INS.getGlobalEmotes().get("cross"));
                // TODO: Display role as new role (17367245237) instead of just new role in the description
                descriptionStuff = List.of(emote != null ? emote.getAsMention() : "", String.valueOf(!wasHoisted));
            } else if (event instanceof RoleUpdateMentionableEvent) {
                modlogEvent = ModlogEvent.ROLE_MENTIONABLE_UPDATED;
                Emote emote = ((RoleUpdateMentionableEvent) event).getNewValue() ? CascadeBot.INS.getShardManager().getEmoteById(Config.INS.getGlobalEmotes().get("tick")) : CascadeBot.INS.getShardManager().getEmoteById(Config.INS.getGlobalEmotes().get("cross"));
                descriptionStuff = List.of(emote != null ? emote.getAsMention() : "", String.valueOf(((RoleUpdateMentionableEvent) event).getNewValue()));
            } else if (event instanceof RoleUpdateNameEvent) {
                modlogEvent = ModlogEvent.ROLE_NAME_UPDATED;
                embedFieldList.add(new ModlogEmbedField(false, "modlog.general.old_name", null, ((RoleUpdateNameEvent) event).getOldName()));
                embedFieldList.add(new ModlogEmbedField(false, "modlog.general.new_name", null, ((RoleUpdateNameEvent) event).getNewValue()));
            } else if (event instanceof RoleUpdatePermissionsEvent) {
                modlogEvent = ModlogEvent.ROLE_PERMISSIONS_UPDATED;
                EnumSet<Permission> oldPermissions = ((RoleUpdatePermissionsEvent) event).getOldPermissions();
                EnumSet<Permission> newPermissions = ((RoleUpdatePermissionsEvent) event).getNewPermissions();

                ListChanges<Permission> permissionListChanges = new ListChanges<>(oldPermissions, newPermissions);
                if (!permissionListChanges.getAdded().isEmpty()) {
                    embedFieldList.add(new ModlogEmbedField(false, "modlog.role.added_perm", null, permissionListChanges.getAdded().stream().map(Permission::getName).collect(Collectors.joining("\n"))));
                }
                if (!permissionListChanges.getRemoved().isEmpty()) {
                    embedFieldList.add(new ModlogEmbedField(false, "modlog.role.removed_perm", null, permissionListChanges.getRemoved().stream().map(Permission::getName).collect(Collectors.joining("\n"))));
                }
            } else if (event instanceof RoleUpdatePositionEvent) {
                modlogEvent = ModlogEvent.ROLE_POSITION_UPDATED;
                if (((RoleUpdatePositionEvent) event).getNewPosition() == ((RoleUpdatePositionEvent) event).getOldPosition()) {
                    // If the position stays the same, we have no reason to log the event
                    return;
                }
                embedFieldList.add(new ModlogEmbedField(false, "modlog.general.position", "modlog.general.small_change", ((RoleUpdatePositionEvent) event).getOldPosition() + 1, ((RoleUpdatePositionEvent) event).getNewPosition() + 1));
            } else {
                return;
            }
            ModlogEventStore modlogEventStore = new ModlogEventStore(modlogEvent, responsible, affected, embedFieldList);
            modlogEventStore.setExtraDescriptionInfo(descriptionStuff);
            guildData.getModeration().sendModlogEvent(event.getGuild().getIdLong(), modlogEventStore);
        }, ActionType.ROLE_CREATE, ActionType.ROLE_DELETE, ActionType.ROLE_UPDATE);
    }

    //region Username updates
    public void onUserUpdateName(UserUpdateNameEvent event) {
        List<ModlogEmbedPart> embedFieldList = new ArrayList<>();
        embedFieldList.add(new ModlogEmbedField(false, "modlog.general.name", "modlog.general.small_change", event.getOldName(), event.getNewName()));
        ModlogEventStore modlogEventStore = new ModlogEventStore(ModlogEvent.USER_NAME_UPDATED, event.getUser(), event.getUser(), embedFieldList);
        for (Guild guild : CascadeBot.INS.getClient().getMutualGuilds(event.getUser())) {
            GuildData guildData = GuildDataManager.getGuildData(guild.getIdLong());
            guildData.getModeration().sendModlogEvent(guild.getIdLong(), modlogEventStore);
        }
    }

    public void onUserUpdateDiscriminator(UserUpdateDiscriminatorEvent event) {
        List<ModlogEmbedPart> embedFieldList = new ArrayList<>();
        embedFieldList.add(new ModlogEmbedField(false, "modlog.member.discrim", "modlog.general.small_change", event.getOldDiscriminator(), event.getNewDiscriminator()));
        ModlogEventStore modlogEventStore = new ModlogEventStore(ModlogEvent.USER_DISCRIMINATOR_UPDATED, event.getUser(), event.getUser(), embedFieldList);
        for (Guild guild : CascadeBot.INS.getClient().getMutualGuilds(event.getUser())) {
            GuildData guildData = GuildDataManager.getGuildData(guild.getIdLong());
            guildData.getModeration().sendModlogEvent(guild.getIdLong(), modlogEventStore);
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
