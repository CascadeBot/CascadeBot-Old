package org.cascadebot.cascadebot.events;

import club.minnced.discord.webhook.send.WebhookEmbed;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.channel.category.GenericCategoryEvent;
import net.dv8tion.jda.api.events.channel.store.GenericStoreChannelEvent;
import net.dv8tion.jda.api.events.channel.text.GenericTextChannelEvent;
import net.dv8tion.jda.api.events.channel.voice.GenericVoiceChannelEvent;
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
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.events.role.GenericRoleEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateDiscriminatorEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.data.Config;
import org.cascadebot.cascadebot.data.objects.ModlogEventStore;
import org.cascadebot.cascadebot.moderation.ModlogEvent;
import org.cascadebot.cascadebot.utils.CryptUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ModlogEventListener extends ListenerAdapter {

    //TODO move everything over to using language paths instead of hard coded string
    public void onGenericEmote(GenericEmoteEvent event) {
        Emote emote = event.getEmote();
        event.getGuild().retrieveAuditLogs().queue(auditLogEntries -> {
            AuditLogEntry entry = auditLogEntries.get(0);
            User user = null;
            if (entry.getType().equals(ActionType.EMOTE_UPDATE) || entry.getType().equals(ActionType.EMOTE_CREATE) || entry.getType().equals(ActionType.EMOTE_DELETE)) {
                user = auditLogEntries.get(0).getUser();
            }
            List<WebhookEmbed.EmbedField> embedFieldList = new ArrayList<>();
            ModlogEvent modlogEvent;
            if (event instanceof EmoteAddedEvent) {
                modlogEvent = ModlogEvent.EMOTE_CREATED;
            } else if (event instanceof EmoteRemovedEvent) {
                modlogEvent = ModlogEvent.EMOTE_DELETED;
            } else if (event instanceof EmoteUpdateNameEvent) {
                modlogEvent = ModlogEvent.EMOTE_UPDATED_NAME;
                embedFieldList.add(new WebhookEmbed.EmbedField(true, "Old Name", ((EmoteUpdateNameEvent) event).getOldName()));
            } else if (event instanceof EmoteUpdateRolesEvent) {
                modlogEvent = ModlogEvent.EMOTE_UPDATED_ROLES;
                List<Role> oldRoles = ((EmoteUpdateRolesEvent) event).getOldRoles();
                List<Role> newRoles = ((EmoteUpdateRolesEvent) event).getNewRoles();
                ListChanges<Role> roleListChanges = new ListChanges<>(oldRoles, newRoles);
                embedFieldList.add(new WebhookEmbed.EmbedField(false, "Added Roles",
                        roleListChanges.added.stream().map(role -> role.getName() + " (" + role.getId() + ")")
                                .collect(Collectors.joining("\n"))));
                embedFieldList.add(new WebhookEmbed.EmbedField(false, "Removed Roles",
                        roleListChanges.removed.stream().map(role -> role.getName() + " (" + role.getId() + ")")
                                .collect(Collectors.joining("\n"))));
            } else {
                return;
            }
            ModlogEventStore eventStore = new ModlogEventStore(modlogEvent, user, emote, embedFieldList);
        });
    }

    public void onGenericGuildMember(GenericGuildMemberEvent event) {
        User user = event.getMember().getUser();
        event.getGuild().retrieveAuditLogs().queue(auditLogEntries -> {
            AuditLogEntry entry = auditLogEntries.get(0);
            List<WebhookEmbed.EmbedField> embedFieldList = new ArrayList<>();
            ModlogEvent modlogEvent;
            User responsible = null;
            if (event instanceof GuildMemberJoinEvent) {
                modlogEvent = ModlogEvent.GUILD_MEMBER_JOINED;
            } else if (event instanceof GuildMemberLeaveEvent) {
                if (entry.getType().equals(ActionType.KICK)) {
                    embedFieldList.add(new WebhookEmbed.EmbedField(true, "Responsible", Objects.requireNonNull(entry.getUser()).getAsTag()));
                    if (entry.getReason() != null) {
                        embedFieldList.add(new WebhookEmbed.EmbedField(false, "Reason", entry.getReason()));
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
                embedFieldList.add(new WebhookEmbed.EmbedField(false, "Added Roles", ((GuildMemberRoleAddEvent) event).getRoles().stream().map(role -> role.getName() + " (" + role.getId() + ")").collect(Collectors.joining("\n"))));
            } else if (event instanceof GuildMemberRoleRemoveEvent) {
                if (entry.getType().equals(ActionType.MEMBER_ROLE_UPDATE)) {
                    responsible = entry.getUser();
                }
                modlogEvent = ModlogEvent.GUILD_MEMBER_ROLE_REMOVED;
                embedFieldList.add(new WebhookEmbed.EmbedField(false, "Removed Roles", ((GuildMemberRoleRemoveEvent) event).getRoles().stream().map(role -> role.getName() + " (" + role.getId() + ")").collect(Collectors.joining("\n"))));
            } else if (event instanceof GuildMemberUpdateNicknameEvent) {
                embedFieldList.add(new WebhookEmbed.EmbedField(true, "Old Nickname", Objects.requireNonNull(((GuildMemberUpdateNicknameEvent) event).getOldValue())));
                embedFieldList.add(new WebhookEmbed.EmbedField(true, "New Nickname", Objects.requireNonNull(((GuildMemberUpdateNicknameEvent) event).getNewValue())));
                modlogEvent = ModlogEvent.GUILD_MEMBER_NICKNAME_UPDATED;
            } else {
                return;
            }
            ModlogEventStore eventStore = new ModlogEventStore(modlogEvent, responsible, user, embedFieldList);
        });
    }

    //region Ban events
    public void onGuildBan(GuildBanEvent event) {
        User user = event.getUser();
        event.getGuild().retrieveAuditLogs().queue(auditLogEntries -> {
            AuditLogEntry entry = auditLogEntries.get(0);
            List<WebhookEmbed.EmbedField> embedFieldList = new ArrayList<>();
            ModlogEvent modlogEvent = ModlogEvent.GUILD_USER_BANNED;
            User responsible = null;
            if (entry.getType().equals(ActionType.BAN)) {
                responsible = entry.getUser();
                if (entry.getReason() != null) {
                    embedFieldList.add(new WebhookEmbed.EmbedField(false, "Reason", entry.getReason()));
                }
            }
            ModlogEventStore eventStore = new ModlogEventStore(modlogEvent, responsible, user, embedFieldList);
        });
    }

    public void onGuildUnban(GuildUnbanEvent event) {
        User user = event.getUser();
        event.getGuild().retrieveAuditLogs().queue(auditLogEntries -> {
            AuditLogEntry entry = auditLogEntries.get(0);
            List<WebhookEmbed.EmbedField> embedFieldList = new ArrayList<>();
            ModlogEvent modlogEvent = ModlogEvent.GUILD_USER_UNBANNED;
            User responsible = null;
            if (entry.getType().equals(ActionType.UNBAN)) {
                responsible = entry.getUser();
            }
            ModlogEventStore eventStore = new ModlogEventStore(modlogEvent, responsible, user, embedFieldList);
        });
    }
    //endregion

    //region Message
    public void onGuildMessageDelete(GuildMessageDeleteEvent event) {
        String messageID = event.getMessageId();
        String messageJson = CascadeBot.INS.getRedisClient().get("message:" + messageID);
        CascadeBot.INS.getRedisClient().del("message:" + messageID);
        JsonObject jsonObject = new JsonParser().parse(messageJson).getAsJsonObject();
        long messageSender = jsonObject.get("sender").getAsLong();
        User affected = CascadeBot.INS.getClient().getUserById(messageSender);
        List<WebhookEmbed.EmbedField> embedFieldList = new ArrayList<>();
        embedFieldList.add(new WebhookEmbed.EmbedField(false, "Message", getMessageFromJson(jsonObject)));
        if (affected == null) {
            return;
        }
        event.getGuild().retrieveAuditLogs().queue(auditLogEntries -> {
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
        });
    }

    public void onGuildMessageUpdate(GuildMessageUpdateEvent event) {
        Message message = event.getMessage();
        String messageJson = CascadeBot.INS.getRedisClient().get("message:" + message.getId());
        CascadeBot.INS.getRedisClient().del("message:" + message.getId());
        JsonObject jsonObject = new JsonParser().parse(messageJson).getAsJsonObject();
        long messageSender = jsonObject.get("sender").getAsLong();
        User affected = CascadeBot.INS.getClient().getUserById(messageSender);
        List<WebhookEmbed.EmbedField> embedFieldList = new ArrayList<>();
        embedFieldList.add(new WebhookEmbed.EmbedField(false, "Old Message", getMessageFromJson(jsonObject)));
        embedFieldList.add(new WebhookEmbed.EmbedField(false, "New Message", message.getContentRaw()));
        if (affected == null) {
            return;
        }
        ModlogEvent modlogEvent = ModlogEvent.GUILD_MESSAGE_UPDATED;
        ModlogEventStore eventStore = new ModlogEventStore(modlogEvent, null, affected, embedFieldList);
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

    }

    public void onGenericStoreChannel(GenericStoreChannelEvent event) {

    }

    public void onGenericTextChannel(GenericTextChannelEvent event) {

    }

    public void onGenericVoiceChannel(GenericVoiceChannelEvent event) {

    }

    public void onGenericCategory(GenericCategoryEvent event) {

    }

    public void onGenericRole(GenericRoleEvent event) {

    }

    //region Username updates
    public void onUserUpdateName(UserUpdateNameEvent event) {

    }

    public void onUserUpdateDiscriminator(UserUpdateDiscriminatorEvent event) {

    }
    //endregion

    //TODO move this to a util class
    public static class ListChanges<T> {
        private final List<T> added = new ArrayList<>();
        private final List<T> removed = new ArrayList<>();

        public ListChanges(List<T> originalList, List<T> newList) {
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
