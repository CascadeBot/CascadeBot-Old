/*
 * Copyright (c) 2021 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.events.modlog

import com.google.gson.JsonParser
import net.dv8tion.jda.api.audit.ActionType
import net.dv8tion.jda.api.audit.AuditLogEntry
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.emote.EmoteAddedEvent
import net.dv8tion.jda.api.events.emote.EmoteRemovedEvent
import net.dv8tion.jda.api.events.emote.GenericEmoteEvent
import net.dv8tion.jda.api.events.emote.update.EmoteUpdateNameEvent
import net.dv8tion.jda.api.events.emote.update.EmoteUpdateRolesEvent
import net.dv8tion.jda.api.events.guild.GuildBanEvent
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent
import net.dv8tion.jda.api.events.guild.member.GenericGuildMemberEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent
import net.dv8tion.jda.api.events.guild.update.GenericGuildUpdateEvent
import net.dv8tion.jda.api.events.guild.update.GuildUpdateAfkChannelEvent
import net.dv8tion.jda.api.events.guild.update.GuildUpdateAfkTimeoutEvent
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBannerEvent
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostCountEvent
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostTierEvent
import net.dv8tion.jda.api.events.guild.update.GuildUpdateDescriptionEvent
import net.dv8tion.jda.api.events.guild.update.GuildUpdateExplicitContentLevelEvent
import net.dv8tion.jda.api.events.guild.update.GuildUpdateFeaturesEvent
import net.dv8tion.jda.api.events.guild.update.GuildUpdateIconEvent
import net.dv8tion.jda.api.events.guild.update.GuildUpdateMFALevelEvent
import net.dv8tion.jda.api.events.guild.update.GuildUpdateMaxPresencesEvent
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNotificationLevelEvent
import net.dv8tion.jda.api.events.guild.update.GuildUpdateRegionEvent
import net.dv8tion.jda.api.events.guild.update.GuildUpdateSplashEvent
import net.dv8tion.jda.api.events.guild.update.GuildUpdateSystemChannelEvent
import net.dv8tion.jda.api.events.guild.update.GuildUpdateVanityCodeEvent
import net.dv8tion.jda.api.events.guild.update.GuildUpdateVerificationLevelEvent
import net.dv8tion.jda.api.events.guild.voice.GenericGuildVoiceEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceDeafenEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildDeafenEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildMuteEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMuteEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.data.Config
import org.cascadebot.cascadebot.data.language.Language.i18n
import org.cascadebot.cascadebot.data.managers.GuildDataManager
import org.cascadebot.cascadebot.data.objects.ModlogEventData
import org.cascadebot.cascadebot.moderation.ModlogEmbedDescription
import org.cascadebot.cascadebot.moderation.ModlogEmbedField
import org.cascadebot.cascadebot.moderation.ModlogEmbedPart
import org.cascadebot.cascadebot.moderation.ModlogEvent
import org.cascadebot.cascadebot.utils.CryptUtils
import org.cascadebot.cascadebot.utils.ModlogUtils.getAuditLogFromType
import org.cascadebot.cascadebot.utils.SerializableMessage
import org.cascadebot.cascadebot.utils.lists.CollectionDiff
import java.nio.ByteBuffer
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.function.Consumer
import java.util.stream.Collectors
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.ShortBufferException

class GuildModlogEventListener : ListenerAdapter() {

    override fun onGenericEmote(event: GenericEmoteEvent) {
        val guildData = GuildDataManager.getGuildData(event.guild.idLong)
        val emote = event.emote
        getAuditLogFromType(
            event.guild, event.emote.idLong,
            Consumer { auditLogEntry: AuditLogEntry? ->
                var user: User? = null
                if (auditLogEntry != null) {
                    user = auditLogEntry.user
                } else {
                    CascadeBot.LOGGER.warn("Modlog: Failed to find emote audit log entry")
                }
                val embedFieldList: MutableList<ModlogEmbedPart> = ArrayList()
                val modlogEvent: ModlogEvent
                if (event is EmoteAddedEvent) {
                    modlogEvent = ModlogEvent.EMOTE_CREATED
                } else if (event is EmoteRemovedEvent) {
                    modlogEvent = ModlogEvent.EMOTE_DELETED
                } else if (event is EmoteUpdateNameEvent) {
                    modlogEvent = ModlogEvent.EMOTE_UPDATED_NAME
                    val oldName = ModlogEmbedField(false, "modlog.general.old_name", null)
                    val newName = ModlogEmbedField(false, "modlog.general.new_name", null)
                    oldName.addValueObjects(event.oldName)
                    newName.addValueObjects(event.newName)
                    embedFieldList.add(oldName)
                    embedFieldList.add(newName)
                } else if (event is EmoteUpdateRolesEvent) {
                    modlogEvent = ModlogEvent.EMOTE_UPDATED_ROLES
                    val oldRoles =
                        event.oldRoles
                    val newRoles =
                        event.newRoles
                    val roleListChanges =
                        CollectionDiff(oldRoles, newRoles)
                    if (roleListChanges.added.isNotEmpty()) {
                        val addedRolesEmbed = ModlogEmbedField(false, "modlog.general.added_roles", null)
                        addedRolesEmbed.addValueObjects(
                            roleListChanges.added.stream()
                                .map { role: Role -> role.name + " (" + role.id + ")" }
                                .collect(Collectors.joining("\n"))
                        )
                        embedFieldList.add(addedRolesEmbed)
                    }
                    if (roleListChanges.removed.isNotEmpty()) {
                        val removedRolesEmbed =
                            ModlogEmbedField(false, "modlog.general.removed_roles", null)
                        removedRolesEmbed.addValueObjects(
                            roleListChanges.removed.stream()
                                .map { role: Role -> role.name + " (" + role.id + ")" }
                                .collect(Collectors.joining("\n"))
                        )
                        embedFieldList.add(removedRolesEmbed)
                    }
                } else {
                    return@Consumer
                }
                val eventStore = ModlogEventData(modlogEvent, user, emote, embedFieldList)
                guildData.moderation.sendModlogEvent(event.guild.idLong, eventStore)
            }, ActionType.EMOTE_CREATE, ActionType.EMOTE_DELETE, ActionType.EMOTE_UPDATE
        )
    }

    override fun onGenericGuildMember(event: GenericGuildMemberEvent) {
        val guildData = GuildDataManager.getGuildData(event.guild.idLong)
        val user = event.member.user
        getAuditLogFromType(
            event.guild, event.user.idLong,
            Consumer { auditLogEntry: AuditLogEntry? ->
                val embedFieldList: MutableList<ModlogEmbedPart> = ArrayList()
                val modlogEvent: ModlogEvent
                var responsible: User? = null
                if (event is GuildMemberJoinEvent) {
                    modlogEvent = ModlogEvent.GUILD_MEMBER_JOINED
                    embedFieldList.add(ModlogEmbedDescription("modlog.member.joined", user.asMention))
                } else if (event is GuildMemberLeaveEvent) {
                    if (auditLogEntry != null && auditLogEntry.type == ActionType.KICK) {
                        val eventStore =
                            ModlogEventData(ModlogEvent.GUILD_MEMBER_LEFT, null, user, embedFieldList)
                        guildData.moderation.sendModlogEvent(event.getGuild().idLong, eventStore)
                        responsible = auditLogEntry.user
                        embedFieldList.add(ModlogEmbedDescription("modlog.member.kicked", user.asMention))
                        if (auditLogEntry.reason != null) {
                            val reasonEmbedField = ModlogEmbedField(false, "modlog.general.reason", null)
                            reasonEmbedField.addValueObjects(auditLogEntry.reason!!)
                            embedFieldList.add(reasonEmbedField)
                        }
                        modlogEvent = ModlogEvent.GUILD_MEMBER_KICKED
                    } else {
                        modlogEvent = ModlogEvent.GUILD_MEMBER_LEFT
                    }
                } else if (event is GuildMemberRoleAddEvent) {
                    if (auditLogEntry != null && auditLogEntry.targetIdLong == event.getMember().idLong) {
                        responsible = auditLogEntry.user
                    }
                    modlogEvent = ModlogEvent.GUILD_MEMBER_ROLE_ADDED
                    val addedRolesEmbedField = ModlogEmbedField(false, "modlog.general.added_roles", null)
                    addedRolesEmbedField.addValueObjects(
                        event.roles.stream()
                            .map { obj: Role -> obj.asMention }
                            .collect(Collectors.joining("\n"))
                    )
                    embedFieldList.add(addedRolesEmbedField)
                } else if (event is GuildMemberRoleRemoveEvent) {
                    if (auditLogEntry != null && auditLogEntry.targetIdLong == event.getMember().idLong) {
                        responsible = auditLogEntry.user
                    }
                    modlogEvent = ModlogEvent.GUILD_MEMBER_ROLE_REMOVED
                    val removedRolesEmbedField =
                        ModlogEmbedField(false, "modlog.general.removed_roles", null)
                    removedRolesEmbedField.addValueObjects(
                        event.roles.stream()
                            .map { obj: Role -> obj.asMention }
                            .collect(Collectors.joining("\n"))
                    )
                    embedFieldList.add(removedRolesEmbedField)
                } else if (event is GuildMemberUpdateNicknameEvent) {
                    if (auditLogEntry != null && auditLogEntry.type == ActionType.MEMBER_UPDATE) {
                        responsible = auditLogEntry.user
                    }
                    if (event.oldValue != null) {
                        val oldNickEmbedField = ModlogEmbedField(false, "modlog.member.old_nick", null)
                        oldNickEmbedField.addValueObjects(event.oldValue!!)
                        embedFieldList.add(oldNickEmbedField)
                    }
                    if (event.newValue != null) {
                        val newNickEmbedField = ModlogEmbedField(false, "modlog.member.new_nick", null)
                        newNickEmbedField.addValueObjects(event.newValue!!)
                        embedFieldList.add(newNickEmbedField)
                    }
                    modlogEvent = ModlogEvent.GUILD_MEMBER_NICKNAME_UPDATED
                } else {
                    return@Consumer
                }
                val eventStore = ModlogEventData(modlogEvent, responsible, user, embedFieldList)
                guildData.moderation.sendModlogEvent(event.guild.idLong, eventStore)
            }, ActionType.KICK, ActionType.MEMBER_ROLE_UPDATE, ActionType.MEMBER_UPDATE
        )
    }

    //region Ban events
    override fun onGuildBan(event: GuildBanEvent) {
        val guildData = GuildDataManager.getGuildData(event.guild.idLong)
        val user = event.user
        getAuditLogFromType(
            event.guild, event.user.idLong,
            Consumer { auditLogEntry: AuditLogEntry? ->
                val embedFieldList: MutableList<ModlogEmbedPart> = ArrayList()
                val modlogEvent = ModlogEvent.GUILD_USER_BANNED
                var responsible: User? = null
                if (auditLogEntry != null) {
                    responsible = auditLogEntry.user
                    embedFieldList.add(ModlogEmbedDescription("modlog.member.banned", user.asMention))
                    if (auditLogEntry.reason != null) {
                        val reasonEmbedField = ModlogEmbedField(false, "modlog.general.reason", null)
                        reasonEmbedField.addValueObjects(auditLogEntry.reason!!)
                        embedFieldList.add(reasonEmbedField)
                    }
                } else {
                    CascadeBot.LOGGER.warn("Modlog: Failed to find ban entry")
                }
                val eventStore = ModlogEventData(modlogEvent, responsible, user, embedFieldList)
                guildData.moderation.sendModlogEvent(event.guild.idLong, eventStore)
            }, ActionType.BAN
        )
    }

    override fun onGuildUnban(event: GuildUnbanEvent) {
        val guildData = GuildDataManager.getGuildData(event.guild.idLong)
        val user = event.user
        getAuditLogFromType(
            event.guild, event.user.idLong,
            Consumer { auditLogEntry: AuditLogEntry? ->
                val embedFieldList: MutableList<ModlogEmbedPart> = ArrayList()
                val modlogEvent = ModlogEvent.GUILD_USER_UNBANNED
                var responsible: User? = null
                if (auditLogEntry != null) {
                    responsible = auditLogEntry.user
                } else {
                    CascadeBot.LOGGER.warn("Modlog: Failed to find unban entry")
                }
                val eventStore = ModlogEventData(modlogEvent, responsible, user, embedFieldList)
                guildData.moderation.sendModlogEvent(event.guild.idLong, eventStore)
            }, ActionType.UNBAN
        )
    }
    //endregion

    //endregion
    //region Message
    // TODO
    override fun onGuildMessageDelete(event: GuildMessageDeleteEvent) {
        if (CascadeBot.INS.redisClient == null) {
            return
        }
        val guildData = GuildDataManager.getGuildData(event.guild.idLong)
        val messageID = event.messageId
        val messageString = CascadeBot.INS.redisClient["message:$messageID"] ?: return
        CascadeBot.INS.redisClient.del("message:$messageID")
        val message = getMessageFromString(event.messageIdLong, messageString) ?: return
        val affected = CascadeBot.INS.client.getUserById(message.authorId)
        val embedFieldList: MutableList<ModlogEmbedPart> = ArrayList()
        val messageEmbedField = ModlogEmbedField(false, "modlog.message.message", null)
        messageEmbedField.addValueObjects(message.content)
        embedFieldList.add(messageEmbedField)
        if (message.attachments.isNotEmpty()) {
            val attachmentsBuilder = StringBuilder()
            for ((_, _, _, url) in message.attachments) {
                attachmentsBuilder.append(url).append('\n')
            }
            embedFieldList.add(
                ModlogEmbedField(
                    false,
                    "modlog.message.attachments",
                    null,
                    attachmentsBuilder.toString()
                )
            )
        }
        if (affected == null) {
            return
        }
        //TODO handle embeds/ect...
        getAuditLogFromType(event.guild, message.authorId, Consumer { auditLogEntry: AuditLogEntry? ->
            val responsible: User? = if (auditLogEntry != null) {
                auditLogEntry.user
            } else {
                CascadeBot.LOGGER.warn("Modlog: Failed to find message delete entry")
                null
            }
            if (message.userMentions.isNotEmpty() || message.roleMentions.isNotEmpty()) {
                val eventStore =
                    ModlogEventData(ModlogEvent.GUILD_MESSAGE_DELETED_MENTION, responsible, affected, embedFieldList)
                guildData.moderation.sendModlogEvent(event.guild.idLong, eventStore)
            }
            val eventStore =
                ModlogEventData(ModlogEvent.GUILD_MESSAGE_DELETED, responsible, affected, embedFieldList)
            guildData.moderation.sendModlogEvent(event.guild.idLong, eventStore)
        }, ActionType.MESSAGE_DELETE)
    }

    // TODO
    override fun onGuildMessageUpdate(event: GuildMessageUpdateEvent) {
        if (CascadeBot.INS.redisClient == null) {
            return
        }
        if (event.author.isBot) {
            return
        }
        val guildData = GuildDataManager.getGuildData(event.guild.idLong)
        val message = event.message
        val messageString = CascadeBot.INS.redisClient["message:" + message.id] ?: return
        CascadeBot.INS.redisClient.del("message:" + message.id)
        val oldMessage = getMessageFromString(message.idLong, messageString) ?: return
        val affected = message.author
        val embedFieldList: MutableList<ModlogEmbedPart> = ArrayList()
        val oldEmbedField = ModlogEmbedField(false, "modlog.message.old_message", null)
        oldEmbedField.addValueObjects(oldMessage.content)
        val newEmbedField = ModlogEmbedField(false, "modlog.message.new_message", null)
        newEmbedField.addValueObjects(message.contentRaw)
        // TODO handle embeds/ect...
        embedFieldList.add(oldEmbedField)
        embedFieldList.add(newEmbedField)
        val modlogEvent = ModlogEvent.GUILD_MESSAGE_UPDATED
        val eventStore = ModlogEventData(modlogEvent, null, affected, embedFieldList)
        guildData.moderation.sendModlogEvent(event.guild.idLong, eventStore)
    }

    private fun getMessageFromString(id: Long, messageString: String): SerializableMessage? {
        val message: String
        if (Config.INS.encryptKey != null) {
            val messageId = ByteBuffer.allocate(java.lang.Long.BYTES).putLong(id).array()
            val iv = ByteArray(messageId.size * 2)
            System.arraycopy(messageId, 0, iv, 0, messageId.size)
            System.arraycopy(messageId, 0, iv, messageId.size, messageId.size)
            val bytesJsonArray = JsonParser().parse(messageString).asJsonArray
            val messageBytes = ByteArray(bytesJsonArray.size())
            for (i in 0 until bytesJsonArray.size()) {
                messageBytes[i] = bytesJsonArray[i].asByte
            }
            message = try {
                CryptUtils.decryptString(Config.INS.encryptKey, iv, messageBytes)
            } catch (e: NoSuchPaddingException) {
                CascadeBot.LOGGER.error("Unable to decrypt message!", e)
                return null
            } catch (e: NoSuchAlgorithmException) {
                CascadeBot.LOGGER.error("Unable to decrypt message!", e)
                return null
            } catch (e: InvalidAlgorithmParameterException) {
                CascadeBot.LOGGER.error("Unable to decrypt message!", e)
                return null
            } catch (e: InvalidKeyException) {
                CascadeBot.LOGGER.error("Unable to decrypt message!", e)
                return null
            } catch (e: IllegalBlockSizeException) {
                CascadeBot.LOGGER.error("Unable to decrypt message!", e)
                return null
            } catch (e: ShortBufferException) {
                CascadeBot.LOGGER.error("Unable to decrypt message!", e)
                return null
            } catch (e: BadPaddingException) {
                // TODO emails? notifications?
                CascadeBot.LOGGER.error(
                    "Unabled to decrypt message due to padding error! **This most likely means the data has been messed with**",
                    e
                )
                return null
            }
        } else {
            message = messageString
        }
        return CascadeBot.getGSON().fromJson(message, SerializableMessage::class.java)
    }
    //endregion

    //endregion
    override fun onGenericGuildUpdate(event: GenericGuildUpdateEvent<*>) {
        val affected = event.entity
        val guildData = GuildDataManager.getGuildData(affected.idLong)
        getAuditLogFromType(event.guild, Consumer { auditLogEntry: AuditLogEntry? ->
            val embedFieldList: MutableList<ModlogEmbedPart> = ArrayList()
            var responsible: User? = null
            val modlogEvent: ModlogEvent
            if (auditLogEntry != null) {
                responsible = auditLogEntry.user
            } else {
                CascadeBot.LOGGER.warn("Modlog: Failed to find guild update entry")
            }
            when (event) {
                is GuildUpdateAfkChannelEvent -> {
                    modlogEvent = ModlogEvent.GUILD_UPDATE_AFK_CHANNEL
                    val oldChannel =
                        event.oldAfkChannel
                    val newChannel =
                        event.newAfkChannel
                    embedFieldList.add(
                        ModlogEmbedField(
                            false,
                            "modlog.guild.old_channel",
                            null,
                            if (oldChannel != null) oldChannel.name + " (" + oldChannel.id + ")" else "-"
                        )
                    )
                    embedFieldList.add(
                        ModlogEmbedField(
                            false,
                            "modlog.guild.new_channel",
                            null,
                            if (newChannel != null) newChannel.name + " (" + newChannel.id + ")" else "-"
                        )
                    )
                }

                is GuildUpdateAfkTimeoutEvent -> {
                    modlogEvent = ModlogEvent.GUILD_UPDATE_AFK_TIMEOUT
                    val oldTimeout = i18n(
                        event.getGuild().idLong,
                        "modlog.guild.timeout_seconds",
                        event.oldAfkTimeout.seconds
                    )
                    val newTimeout = i18n(
                        event.getGuild().idLong,
                        "modlog.guild.timeout_seconds",
                        event.newAfkTimeout.seconds
                    )
                    embedFieldList.add(
                        ModlogEmbedField(
                            false,
                            "modlog.guild.timeout",
                            "modlog.general.small_change",
                            oldTimeout,
                            newTimeout
                        )
                    )
                }

                is GuildUpdateBannerEvent -> {
                    if (event.oldBannerUrl != null) {
                        embedFieldList.add(
                            ModlogEmbedField(
                                false, "modlog.guild.old_image", null,
                                event.oldBannerUrl!!
                            )
                        )
                    }
                    if (event.newBannerUrl != null) {
                        embedFieldList.add(
                            ModlogEmbedField(
                                false, "modlog.guild.new_image", null,
                                event.newBannerUrl!!
                            )
                        )
                    }
                    modlogEvent = ModlogEvent.GUILD_UPDATE_BANNER
                }

                is GuildUpdateDescriptionEvent -> {
                    if (event.oldDescription != null) {
                        embedFieldList.add(
                            ModlogEmbedField(
                                false, "modlog.guild.old_description", null,
                                event.oldDescription!!
                            )
                        )
                    }
                    if (event.newDescription != null) {
                        embedFieldList.add(
                            ModlogEmbedField(
                                false, "modlog.guild.new_description", null,
                                event.newDescription!!
                            )
                        )
                    }
                    modlogEvent = ModlogEvent.GUILD_UPDATE_DESCRIPTION
                }

                is GuildUpdateExplicitContentLevelEvent -> {
                    embedFieldList.add(
                        ModlogEmbedField(
                            false,
                            "modlog.guild.content_filter.old",
                            "modlog.guild.content_filter." + event.oldLevel.name.toLowerCase()
                        )
                    )
                    embedFieldList.add(
                        ModlogEmbedField(
                            false,
                            "modlog.guild.content_filter.new",
                            "modlog.guild.content_filter." + event.newLevel.name.toLowerCase()
                        )
                    )
                    modlogEvent = ModlogEvent.GUILD_UPDATE_EXPLICIT_FILTER
                }

                is GuildUpdateFeaturesEvent -> {
                    val featuresChanged = CollectionDiff(
                        event.oldFeatures,
                        event.newFeatures
                    )
                    embedFieldList.add(
                        ModlogEmbedField(
                            false,
                            "modlog.guild.add_feature",
                            null,
                            java.lang.String.join("\n", featuresChanged.added)
                        )
                    )
                    embedFieldList.add(
                        ModlogEmbedField(
                            false,
                            "modlog.guild.removed_feature",
                            null,
                            java.lang.String.join("\n", featuresChanged.removed)
                        )
                    )
                    modlogEvent = ModlogEvent.GUILD_UPDATE_FEATURES
                }

                is GuildUpdateIconEvent -> {
                    val oldIconUrl = event.oldIconUrl
                    val newIconUrl = event.newIconUrl
                    embedFieldList.add(
                        ModlogEmbedField(
                            false,
                            "modlog.guild.old_image",
                            null,
                            oldIconUrl ?: "-"
                        )
                    )
                    embedFieldList.add(
                        ModlogEmbedField(
                            false,
                            "modlog.guild.new_image",
                            null,
                            newIconUrl ?: "-"
                        )
                    )
                    modlogEvent = ModlogEvent.GUILD_UPDATE_ICON
                    //          The max members
                    //            } else if (event instanceof GuildUpdateMaxMembersEvent) {
                    //                String oldMembers = String.valueOf(((GuildUpdateMaxMembersEvent) event).getOldMaxMembers());
                    //                String newMembers = String.valueOf(((GuildUpdateMaxMembersEvent) event).getNewMaxMembers());
                    //
                    //                embedFieldList.add(new ModlogEmbedField(false, "modlog.guild.max_members", "modlog.general.small_change", oldMembers, newMembers));
                    //                modlogEvent = ModlogEvent.GUILD_UPDATE_MAX_MEMBERS;
                }

                is GuildUpdateMaxPresencesEvent -> {
                    val oldPresences = event.oldMaxPresences.toString()
                    val newPresences = event.newMaxPresences.toString()
                    embedFieldList.add(
                        ModlogEmbedField(
                            false,
                            "modlog.guild.max_presences",
                            "modlog.general.small_change",
                            oldPresences,
                            newPresences
                        )
                    )
                    responsible = null
                    modlogEvent = ModlogEvent.GUILD_UPDATE_MAX_PRESENCES
                }

                is GuildUpdateMFALevelEvent -> {
                    val oldLevel = i18n(
                        event.getGuild().idLong,
                        "modlog.guild.mfa." + event.oldMFALevel.name.toLowerCase()
                    )
                    val newLevel = i18n(
                        event.getGuild().idLong,
                        "modlog.guild.mfa." + event.newMFALevel.name.toLowerCase()
                    )
                    embedFieldList.add(
                        ModlogEmbedField(
                            false,
                            "modlog.guild.mfa.mfa_level",
                            "modlog.general.small_change",
                            oldLevel,
                            newLevel
                        )
                    )
                    modlogEvent = ModlogEvent.GUILD_UPDATE_MFA_LEVEL
                }

                is GuildUpdateNameEvent -> {
                    embedFieldList.add(
                        ModlogEmbedField(
                            false,
                            "modlog.general.old_name",
                            null,
                            event.oldName
                        )
                    )
                    embedFieldList.add(
                        ModlogEmbedField(
                            false,
                            "modlog.general.new_name",
                            null,
                            event.newValue
                        )
                    )
                    modlogEvent = ModlogEvent.GUILD_UPDATE_NAME
                }

                is GuildUpdateNotificationLevelEvent -> {
                    val oldLevel = i18n(
                        event.getGuild().idLong,
                        "modlog.guild.notification." + event.oldNotificationLevel.name.toLowerCase()
                    )
                    val newLevel = i18n(
                        event.getGuild().idLong,
                        "modlog.guild.notification." + event.newNotificationLevel.name.toLowerCase()
                    )
                    embedFieldList.add(
                        ModlogEmbedField(
                            false,
                            "modlog.guild.notification.title",
                            "modlog.general.small_change",
                            oldLevel,
                            newLevel
                        )
                    )
                    modlogEvent = ModlogEvent.GUILD_UPDATE_NOTIFICATION_LEVEL
                }

                is GuildUpdateRegionEvent -> {
                    embedFieldList.add(
                        ModlogEmbedField(
                            false,
                            "modlog.guild.old_region",
                            null,
                            event.oldRegion.getName()
                        )
                    )
                    embedFieldList.add(
                        ModlogEmbedField(
                            false,
                            "modlog.guild.new_region",
                            null,
                            event.newRegion.getName()
                        )
                    )
                    modlogEvent = ModlogEvent.GUILD_UPDATE_REGION
                }

                is GuildUpdateSplashEvent -> {
                    val oldSplashUrl = event.oldSplashUrl
                    val newSplashUrl = event.newSplashUrl
                    embedFieldList.add(
                        ModlogEmbedField(
                            false,
                            "modlog.guild.old_splash",
                            null,
                            oldSplashUrl ?: "-"
                        )
                    )
                    embedFieldList.add(
                        ModlogEmbedField(
                            false,
                            "modlog.guild.new_splash",
                            null,
                            newSplashUrl ?: "-"
                        )
                    )
                    modlogEvent = ModlogEvent.GUILD_UPDATE_SPLASH
                }

                is GuildUpdateSystemChannelEvent -> {
                    val oldSystemChannel = event.oldSystemChannel
                    val newSystemChannel = event.newSystemChannel
                    embedFieldList.add(
                        ModlogEmbedField(
                            false,
                            "modlog.guild.sys_channel",
                            "modlog.general.small_change",
                            oldSystemChannel?.asMention ?: "-",
                            newSystemChannel?.asMention ?: "-"
                        )
                    )
                    modlogEvent = ModlogEvent.GUILD_UPDATE_SYSTEM_CHANNEL
                }

                is GuildUpdateVanityCodeEvent -> {
                    if (event.oldVanityCode != null) {
                        embedFieldList.add(
                            ModlogEmbedField(
                                false, "modlog.guild.vanity_code.old", null,
                                event.oldVanityCode!!
                            )
                        )
                    }
                    if (event.oldVanityUrl != null) {
                        embedFieldList.add(
                            ModlogEmbedField(
                                false, "modlog.guild.vanity_url.old", null,
                                event.oldVanityUrl!!
                            )
                        )
                    }
                    if (event.newVanityCode != null) {
                        embedFieldList.add(
                            ModlogEmbedField(
                                false, "modlog.guild.vanity_code.new", null,
                                event.newVanityCode!!
                            )
                        )
                    }
                    if (event.newVanityUrl != null) {
                        embedFieldList.add(
                            ModlogEmbedField(
                                false, "modlog.guild.vanity_url.new", null,
                                event.newVanityUrl!!
                            )
                        )
                    }
                    modlogEvent = ModlogEvent.GUILD_UPDATE_VANITY_CODE
                }

                is GuildUpdateVerificationLevelEvent -> {
                    embedFieldList.add(
                        ModlogEmbedField(
                            false,
                            "modlog.guild.old_verification",
                            "utils.verification_level." + event.oldVerificationLevel.name.toLowerCase()
                        )
                    )
                    embedFieldList.add(
                        ModlogEmbedField(
                            false,
                            "modlog.guild.new_verification",
                            "utils.verification_level." + event.newVerificationLevel.name.toLowerCase()
                        )
                    )
                    modlogEvent = ModlogEvent.GUILD_UPDATE_VERIFICATION_LEVEL
                }

                is GuildUpdateBoostCountEvent -> {
                    embedFieldList.add(
                        ModlogEmbedField(
                            false,
                            "modlog.guild.old_boost_count",
                            null,
                            event.oldValue.toString()
                        )
                    )
                    embedFieldList.add(
                        ModlogEmbedField(
                            false,
                            "modlog.guild.new_boost_count",
                            null,
                            event.newValue.toString()
                        )
                    )
                    modlogEvent = ModlogEvent.GUILD_BOOST_COUNT_UPDATED
                }

                is GuildUpdateBoostTierEvent -> {
                    embedFieldList.add(
                        ModlogEmbedField(
                            false,
                            "modlog.guild.old_boost_tier",
                            "modlog.guild.boost_tier." + event.oldBoostTier.name.toLowerCase()
                        )
                    )
                    embedFieldList.add(
                        ModlogEmbedField(
                            false,
                            "modlog.guild.new_boost_tier",
                            "modlog.guild.boost_tier." + event.newBoostTier.name.toLowerCase()
                        )
                    )
                    modlogEvent = ModlogEvent.GUILD_BOOST_TIER_UPDATED
                }

                else -> {
                    return@Consumer
                }
            }
            val eventStore = ModlogEventData(modlogEvent, responsible, affected, embedFieldList)
            guildData.moderation.sendModlogEvent(event.guild.idLong, eventStore)
        }, ActionType.GUILD_UPDATE)
    }

    override fun onGenericGuildVoice(event: GenericGuildVoiceEvent) {
        val affected = event.member.user
        val guild = event.guild
        val guildData = GuildDataManager.getGuildData(guild.idLong)
        getAuditLogFromType(
            event.guild, event.member.idLong,
            Consumer { auditLogEntry: AuditLogEntry? ->
                val embedFieldList: MutableList<ModlogEmbedPart> = ArrayList()
                val extraDescriptionInfo: MutableList<String>
                val action: ModlogEvent
                var responsible: User? = null
                if (event is GuildVoiceDeafenEvent) {
                    val deafened = event.isDeafened
                    val emote = if (deafened) CascadeBot.INS.shardManager.getEmoteById(
                        Config.INS.globalEmotes["self-deafened"]!!
                    ) else CascadeBot.INS.shardManager.getEmoteById(
                        Config.INS.globalEmotes["undeafened"]!!
                    )
                    extraDescriptionInfo = mutableListOf(
                        emote?.asMention ?: "",
                        deafened.toString()
                    )
                    action = ModlogEvent.VOICE_DEAFEN
                } else if (event is GuildVoiceMuteEvent) {
                    val muted = event.isMuted
                    val emote = if (muted) CascadeBot.INS.shardManager.getEmoteById(
                        Config.INS.globalEmotes["self-muted"]!!
                    ) else CascadeBot.INS.shardManager.getEmoteById(
                        Config.INS.globalEmotes["unmuted"]!!
                    )
                    extraDescriptionInfo = mutableListOf(emote?.asMention ?: "", muted.toString())
                    action = ModlogEvent.VOICE_MUTE
                } else if (event is GuildVoiceGuildDeafenEvent) {
                    if (auditLogEntry != null && auditLogEntry.type == ActionType.MEMBER_UPDATE) {
                        responsible = auditLogEntry.user
                    }
                    val guildDeafened = event.isGuildDeafened
                    val emote = if (guildDeafened) CascadeBot.INS.shardManager.getEmoteById(
                        Config.INS.globalEmotes["server-deafened"]!!
                    ) else CascadeBot.INS.shardManager.getEmoteById(
                        Config.INS.globalEmotes["undeafened"]!!
                    )
                    extraDescriptionInfo = mutableListOf(
                        emote?.asMention ?: "",
                        guildDeafened.toString()
                    )
                    action = ModlogEvent.VOICE_SERVER_DEAFEN
                } else if (event is GuildVoiceGuildMuteEvent) {
                    if (auditLogEntry != null && auditLogEntry.type == ActionType.MEMBER_UPDATE) {
                        responsible = auditLogEntry.user
                    }
                    val guildMuted = event.isGuildMuted
                    val emote = if (guildMuted) CascadeBot.INS.shardManager.getEmoteById(
                        Config.INS.globalEmotes["server-muted"]!!
                    ) else CascadeBot.INS.shardManager.getEmoteById(
                        Config.INS.globalEmotes["unmuted"]!!
                    )
                    extraDescriptionInfo = mutableListOf(
                        emote?.asMention ?: "",
                        guildMuted.toString()
                    )
                    action = ModlogEvent.VOICE_SERVER_MUTE
                } else if (event is GuildVoiceJoinEvent) {
                    extraDescriptionInfo = mutableListOf(event.channelJoined.name)
                    action = ModlogEvent.VOICE_JOIN
                } else if (event is GuildVoiceLeaveEvent) {
                    extraDescriptionInfo = mutableListOf(event.channelLeft.name)
                    if (auditLogEntry != null && auditLogEntry.type == ActionType.MEMBER_VOICE_KICK) {
                        action = ModlogEvent.VOICE_DISCONNECT
                        responsible = auditLogEntry.user
                    } else {
                        action = ModlogEvent.VOICE_LEAVE
                    }
                } else if (event is GuildVoiceMoveEvent) {
                    extraDescriptionInfo = mutableListOf(
                        event.channelLeft.name,
                        event.channelJoined.name
                    )
                    if (auditLogEntry != null && auditLogEntry.type == ActionType.MEMBER_VOICE_MOVE) {
                        action = ModlogEvent.VOICE_FORCE_MOVE
                        responsible = auditLogEntry.user
                    } else {
                        action = ModlogEvent.VOICE_MOVE
                    }
                } else {
                    return@Consumer
                }
                val eventStore = ModlogEventData(action, responsible, affected, embedFieldList)
                eventStore.extraDescriptionInfo = extraDescriptionInfo
                guildData.moderation.sendModlogEvent(event.guild.idLong, eventStore)
            }, ActionType.MEMBER_VOICE_MOVE, ActionType.MEMBER_VOICE_KICK, ActionType.MEMBER_UPDATE
        )
    }

}