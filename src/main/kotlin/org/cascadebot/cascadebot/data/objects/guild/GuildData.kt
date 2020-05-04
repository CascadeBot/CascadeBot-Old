package org.cascadebot.cascadebot.data.objects.guild

import com.google.common.collect.Sets
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import de.bild.codec.annotations.Id
import de.bild.codec.annotations.Transient
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.data.Config
import org.cascadebot.cascadebot.data.language.Locale
import org.cascadebot.cascadebot.data.managers.CascadeUserDataManager
import org.cascadebot.cascadebot.data.objects.donation.DataFlag
import org.cascadebot.cascadebot.data.objects.donation.Flag
import org.cascadebot.cascadebot.data.objects.donation.FlagContainer
import org.cascadebot.cascadebot.data.objects.donation.Tier
import org.cascadebot.cascadebot.data.objects.user.CascadeUser
import org.cascadebot.cascadebot.music.CascadeLavalinkPlayer
import org.cascadebot.cascadebot.utils.buttons.ButtonGroup
import org.cascadebot.cascadebot.utils.buttons.ButtonsCache
import org.cascadebot.cascadebot.utils.buttons.PersistentButtonGroup
import org.cascadebot.cascadebot.utils.pagination.PageCache
import java.util.Date

class GuildData(@field:Id val guildId: Long) {

    private constructor() : this(0L) {
        // Private constructor for MongoDB
    }

    //region Meta information
    private val creationDate = Date()
    //endregion

    val enabledFlags: MutableSet<Flag> = Sets.newConcurrentHashSet()

    // TODO: Keep this here or remove it? It just serves as an accessor for the actual locale
    val locale: Locale
        get() = core.locale

    //region Guild data containers
    val core = GuildSettingsCore()
    val useful = GuildSettingsUseful()
    val moderation = GuildSettingsModeration()
    val management = GuildSettingsManagement()
    val music = GuildSettingsMusic()
    //endregion

    //region Transient fields
    @Transient
    val buttonsCache = ButtonsCache(5)

    @Transient
    val pageCache = PageCache()

    @Transient
    val permissionsManager = PerGuildPermissionsManager()
    //endregion

    val persistentButtons = HashMap<Long, HashMap<Long, PersistentButtonGroup>>()

    //endregion

    private val supporters: MutableSet<Long> = mutableSetOf()
    private val flags: MutableSet<Flag> = mutableSetOf()

    //region Data Loaded Methods
    fun onGuildLoaded() {
        loadMusicSettings()
        permissionsManager.registerPermissions(this)
    }

    private fun loadMusicSettings() {
        val player = CascadeBot.INS.musicHandler.getPlayer(guildId)!!
        if (music.preserveVolume) {
            player.volume = music.volume
        }
        if (music.preserveEqualizer) {
            if (CascadeBot.INS.musicHandler.lavalinkEnabled) {
                if (player is CascadeLavalinkPlayer) {
                    player.setBands(music.equalizerBands)
                }
            }
        }
    }

    fun enableFlag(flag: Flag): Boolean {
        return enabledFlags.add(flag)
    }

    fun disableFlag(flag: Flag): Boolean {
        return enabledFlags.remove(flag)
    }

    fun isFlagEnabled(flag: Flag): Boolean {
        return enabledFlags.contains(flag)
    }

    fun addButtonGroup(channel: MessageChannel, message: Message, group: ButtonGroup) {
        group.setMessage(message.idLong)
        if (group is PersistentButtonGroup) {
            putPersistentButtonGroup(channel.idLong, message.idLong, group)
        } else {
            buttonsCache.put(channel.idLong, message.idLong, group)
        }
    }

    private fun putPersistentButtonGroup(channelId: Long, messageId: Long, buttonGroup: PersistentButtonGroup) {
        if (persistentButtons.containsKey(channelId) && persistentButtons[channelId] != null) {
            persistentButtons[channelId]!![messageId] = buttonGroup
        } else {
            persistentButtons[channelId] = HashMap()
            persistentButtons[channelId]!![messageId] = buttonGroup
        }
    }

    //endregion

    fun getGuildTierName(): String? {
        if (supporters.isEmpty()) {
            return "default"
        }
        var highestTierName = "default"
        for (id in supporters) {
            val user = CascadeUserDataManager.getUser(id)
            if (user.tier.isTierParent(highestTierName)) {
                highestTierName = user.tierName
            }
        }
        return highestTierName
    }

    fun getAllFlags(): FlagContainer {
        val flags = ArrayList(getGuildTier()?.getAllFlags()!!.toList())
        for (guildFlag in this.flags) {
            if (guildFlag !is DataFlag) {
                continue
            }
            if (getGuildTier()?.hasFlag(guildFlag.id)!!) {
                val compareFlag: Flag? = getGuildTier()?.getFlag(guildFlag.id)
                if (compareFlag !is DataFlag) {
                    continue
                }
                if (guildFlag > compareFlag) {
                    flags.remove(compareFlag)
                    flags.add(guildFlag)
                }
            } else {
                flags.add(guildFlag)
            }
        }

        for (id in supporters) {
            val user = CascadeUserDataManager.getUser(id)
            if (user.blackList.contains(guildId)) {
                continue
            }
            for (userFlag in user.flags) {
                if (userFlag !is DataFlag) {
                    continue
                }
                if (getGuildTier()?.hasFlag(userFlag.id)!!) {
                    val compareFlag: Flag? = getGuildTier()?.getFlag(userFlag.id)
                    if (compareFlag !is DataFlag) {
                        continue
                    }
                    if (userFlag > compareFlag) {
                        flags.remove(compareFlag)
                        flags.add(userFlag)
                    }
                } else {
                    flags.add(userFlag)
                }
            }
        }

        return FlagContainer(HashSet(flags))
    }

    fun optOutUser(user: CascadeUser): Boolean {
        return if (Config.INS.patreonServiceUrl != null) {
            val request = Request.Builder()
                    .url(Config.INS.patreonServiceUrl + "/user/" + user.userId + "/guilds/" + guildId)
                    .method("DELETE", null)
                    .addHeader("Authorization", "Bearer " + Config.INS.patreonServiceKey)
                    .build()
            val response = CascadeBot.INS.httpClient.newCall(request).execute()
            response.code == 200
        } else {
            user.blackList.add(guildId)
        }
    }

    fun optInUser(user: CascadeUser): Boolean {
        return if (Config.INS.patreonServiceUrl != null) {
            val request = Request.Builder()
                    .url(Config.INS.patreonServiceUrl + "/user/" + user.userId + "/guilds/" + guildId)
                    .method("POST", null)
                    .addHeader("Authorization", "Bearer " + Config.INS.patreonServiceKey)
                    .build()
            val response = CascadeBot.INS.httpClient.newCall(request).execute()
            response.code == 200
        } else {
            user.blackList.remove(guildId)
        }
    }

    //region Guild Flags
    fun addFlag(flag: Flag): Boolean {
        return if (Config.INS.patreonServiceUrl != null) {
            val jsonObject = JsonObject()
            val addFlags = JsonArray()
            addFlags.add(flag.id)
            jsonObject.add("add", addFlags)
            val requestBody = CascadeBot.getGSON().toJson(jsonObject).toRequestBody()
            val request = Request.Builder()
                    .url(Config.INS.patreonServiceUrl + "/guild/" + guildId + "/flags")
                    .method("PATCH", requestBody)
                    .addHeader("Authorization", "Bearer " + Config.INS.patreonServiceKey)
                    .build()
            val response = CascadeBot.INS.httpClient.newCall(request).execute()
            response.code == 200
        } else {
            flags.add(flag)
        }
    }

    fun removeFlag(flag: Flag): Boolean {
        return if (Config.INS.patreonServiceUrl != null) {
            val jsonObject = JsonObject()
            val removeFlags = JsonArray()
            removeFlags.add(flag.id)
            jsonObject.add("remove", removeFlags)
            val requestBody = CascadeBot.getGSON().toJson(jsonObject).toRequestBody()
            val request = Request.Builder()
                    .url(Config.INS.patreonServiceUrl + "/guild/" + guildId + "/flags")
                    .method("PATCH", requestBody)
                    .addHeader("Authorization", "Bearer " + Config.INS.patreonServiceKey)
                    .build()
            val response = CascadeBot.INS.httpClient.newCall(request).execute()
            response.code == 200
        } else {
            flags.remove(flag)
        }
    }

    fun addFlags(flags: List<Flag>): Boolean {
        return if (Config.INS.patreonServiceUrl != null) {
            val jsonObject = JsonObject()
            val addFlags = CascadeBot.getGSON().toJsonTree(flags);
            jsonObject.add("add", addFlags)
            val requestBody = CascadeBot.getGSON().toJson(jsonObject).toRequestBody()
            val request = Request.Builder()
                    .url(Config.INS.patreonServiceUrl + "/guild/" + guildId + "/flags")
                    .method("PATCH", requestBody)
                    .addHeader("Authorization", "Bearer " + Config.INS.patreonServiceKey)
                    .build()
            val response = CascadeBot.INS.httpClient.newCall(request).execute()
            response.code == 200
        } else {
            this.flags.addAll(flags)
        }
    }

    fun removeFlags(flags: List<Flag>): Boolean {
        return if (Config.INS.patreonServiceUrl != null) {
            val jsonObject = JsonObject()
            val removeFlags = CascadeBot.getGSON().toJsonTree(flags);
            jsonObject.add("remove", removeFlags)
            val requestBody = CascadeBot.getGSON().toJson(jsonObject).toRequestBody()
            val request = Request.Builder()
                    .url(Config.INS.patreonServiceUrl + "/guild/" + guildId + "/flags")
                    .method("PATCH", requestBody)
                    .addHeader("Authorization", "Bearer " + Config.INS.patreonServiceKey)
                    .build()
            val response = CascadeBot.INS.httpClient.newCall(request).execute()
            response.code == 200
        } else {
            this.flags.removeAll(flags)
        }
    }

    fun clearFlags(): Boolean {
        return if (Config.INS.patreonServiceUrl != null) {
            val jsonObject = JsonObject()
            jsonObject.add("clear", JsonPrimitive(true))
            val requestBody = CascadeBot.getGSON().toJson(jsonObject).toRequestBody()
            val request = Request.Builder()
                    .url(Config.INS.patreonServiceUrl + "/guild/" + guildId + "/flags")
                    .method("PATCH", requestBody)
                    .addHeader("Authorization", "Bearer " + Config.INS.patreonServiceKey)
                    .build()
            val response = CascadeBot.INS.httpClient.newCall(request).execute()
            response.code == 200
        } else {
            flags.clear()
            true
        }
    }
    //endregion

    private fun getGuildTier(): Tier? {
        if (supporters.isEmpty()) {
            return Tier.getTier("default")
        }
        var highest = Tier.getTier("default")
        var highestTierName = "default"
        for (id in supporters) {
            val user = CascadeUserDataManager.getUser(id)
            if (user.blackList.contains(guildId)) {
                continue
            }
            if (user.tier.isTierParent(highestTierName)) {
                highest = user.tier
                highestTierName = user.tierName
            }
        }
        return highest
    }

}
