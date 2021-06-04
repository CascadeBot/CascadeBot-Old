package org.cascadebot.cascadebot.data.objects

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.util.ClassUtil.classOf
import com.google.common.collect.Sets
import com.google.common.math.LongMath
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import de.bild.codec.annotations.Id
import de.bild.codec.annotations.Transient
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.data.language.Locale
import org.cascadebot.cascadebot.data.managers.GuildDataManager
import org.cascadebot.cascadebot.music.CascadeLavalinkPlayer
import org.cascadebot.cascadebot.utils.buttons.ButtonGroup
import org.cascadebot.cascadebot.utils.buttons.ButtonsCache
import org.cascadebot.cascadebot.utils.buttons.PersistentButtonGroup
import org.cascadebot.cascadebot.utils.diff.DiffUtils
import org.cascadebot.cascadebot.utils.diff.Difference
import org.cascadebot.cascadebot.utils.pagination.PageCache
import org.slf4j.MDC
import java.lang.UnsupportedOperationException
import java.lang.reflect.Type
import java.util.Date
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import java.util.function.Consumer
import kotlin.concurrent.withLock
import kotlin.reflect.jvm.javaType
import kotlin.reflect.typeOf
import org.cascadebot.cascadebot.utils.GuildDataUtils.assertWriteMode
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Function
import kotlin.concurrent.getOrSet

class GuildData(@field:Id val guildId: Long): Cloneable {

    @Transient
    @kotlin.jvm.Transient
    val lock: ReadWriteLock = ReentrantReadWriteLock()

    companion object {

        val writeMode: ThreadLocal<Boolean> = ThreadLocal()

    }

    private constructor() : this(0L) {
        // Private constructor for MongoDB
    }

    //region Meta information
    private val creationDate = Date()
    //endregion

    val enabledFlags: MutableSet<Flag> = Sets.newConcurrentHashSet()

    // This is a cache of the muted role so we can use the same one each time if, for
    // some reason, there are multiple "Muted" roles
    var mutedRoleId: Long = -1

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

    val objClass = this.javaClass.packageName + "." + this.javaClass.name

    //region Transient fields
    @Transient
    @kotlin.jvm.Transient
    val buttonsCache = ButtonsCache(5)

    @Transient
    @kotlin.jvm.Transient
    val pageCache = PageCache()

    @Transient
    @kotlin.jvm.Transient
    val permissionsManager = PerGuildPermissionsManager()
    //endregion

    val persistentButtons = HashMap<Long, HashMap<Long, PersistentButtonGroup>>()

    //endregion
    //region Data Loaded Methods
    fun onGuildLoaded() {
        loadMusicSettings()
        permissionsManager.registerPermissions(this)
        moderation.buildWebhookClients()
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
        assertWriteMode()
        return enabledFlags.add(flag)
    }

    fun disableFlag(flag: Flag): Boolean {
        assertWriteMode()
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
        assertWriteMode()

        if (persistentButtons.containsKey(channelId) && persistentButtons[channelId] != null) {
            persistentButtons[channelId]!![messageId] = buttonGroup
        } else {
            persistentButtons[channelId] = HashMap()
            persistentButtons[channelId]!![messageId] = buttonGroup
        }
    }

    //endregion

    fun write(writer: Consumer<GuildData>) {
        this.lock.writeLock().withLock {
            val copy: GuildData = CascadeBot.getGSON().fromJson(CascadeBot.getGSON().toJson(this), this.javaClass)
            writeMode.set(true)
            writer.accept(copy);
            writeMode.set(false)
            val diff: Difference = DiffUtils.diff(this, copy)
            GuildDataManager.updateDiff(guildId, diff, copy)
            //println(GsonBuilder().setPrettyPrinting().create().toJson(diff))
        }
    }

    fun <T : Any?> writeInline(writer: Function<GuildData, T>) : T {
        this.lock.writeLock().withLock {
            val copy: GuildData = CascadeBot.getGSON().fromJson(CascadeBot.getGSON().toJson(this), this.javaClass)
            writeMode.set(true)
            val output = writer.apply(copy);
            writeMode.set(false)
            val diff: Difference = DiffUtils.diff(this, copy)
            GuildDataManager.updateDiff(guildId, diff, copy)
            //println(GsonBuilder().setPrettyPrinting().create().toJson(diff))
            return output
        }
    }

}



