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
import org.bson.BsonArray
import org.bson.BsonDocument
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.data.database.BsonObject
import org.cascadebot.cascadebot.data.database.DataHandler
import org.cascadebot.cascadebot.data.language.Locale
import org.cascadebot.cascadebot.data.managers.GuildDataManager
import org.cascadebot.cascadebot.music.CascadeLavalinkPlayer
import org.cascadebot.cascadebot.utils.interactions.PersistentComponent
import org.cascadebot.cascadebot.utils.interactions.CascadeActionRow
import org.cascadebot.cascadebot.utils.interactions.InteractionCache
import org.cascadebot.cascadebot.utils.interactions.ComponentContainer
import org.cascadebot.cascadebot.utils.pagination.PageCache
import org.cascadebot.cascadebot.utils.votes.VoteGroup
import java.util.Date
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import java.util.function.Consumer
import kotlin.concurrent.withLock
import kotlin.reflect.jvm.javaType
import kotlin.reflect.typeOf
import org.cascadebot.cascadebot.utils.GuildDataUtils.assertWriteMode
import org.cascadebot.cascadebot.utils.diff.DiffUtils
import org.cascadebot.cascadebot.utils.diff.Difference
import org.cascadebot.cascadebot.utils.ifContains
import org.cascadebot.cascadebot.utils.ifContainsArray
import org.cascadebot.cascadebot.utils.ifContainsDocument
import org.cascadebot.cascadebot.utils.ifContainsLong
import java.util.ArrayList
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Function
import kotlin.concurrent.getOrSet

class GuildData(@field:Id val guildId: Long): BsonObject {

    @Transient
    @kotlin.jvm.Transient
    val lock: ReadWriteLock = ReentrantReadWriteLock()

    companion object {

        @Transient
        @kotlin.jvm.Transient
        val writeMode: ThreadLocal<Boolean> = ThreadLocal()

        init {
            writeMode.set(false)
        }

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

    //region Transient fields
    @Transient
    @kotlin.jvm.Transient
    val componentCache = InteractionCache(5)

    @Transient
    @kotlin.jvm.Transient
    val pageCache = PageCache()

    @Transient
    @kotlin.jvm.Transient
    val permissionsManager = PerGuildPermissionsManager()
    //endregion

    val persistentComponents = HashMap<Long, HashMap<Long, List<List<PersistentComponent>>>>()

    val voteGroups: MutableMap<String, VoteGroup> = mutableMapOf()

    //endregion

    //region Data Loaded Methods
    fun onGuildLoaded() {
        loadMusicSettings()
        loadComponents()
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

    private fun loadComponents() {
        for (channelEntry in persistentComponents.entries) {
            val channelId = channelEntry.key
            for(messageEntry in channelEntry.value.entries) {
                val messageId = messageEntry.key
                val container = ComponentContainer()
                for (storedRow in messageEntry.value) {
                    val row = CascadeActionRow()
                    for (storedComp in storedRow) {
                        row.addComponent(storedComp.component)
                    }
                    container.addRow(row)
                }
                componentCache.put(channelId, messageId, container)
            }
        }
    }
    //endregion

    //region Flags
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
    //endregion

    //region Components
    fun addComponents(channel: MessageChannel, message: Message, container: ComponentContainer) {
        if (container.persistent) {
            val containerList: MutableList<List<PersistentComponent>> = mutableListOf()
            for (row in container.getComponents()) {
                val rowList: MutableList<PersistentComponent> = mutableListOf()
                for (component in row.getComponents()) {
                    rowList.add(PersistentComponent.values().find { it.component == component }!!)
                }
                containerList.add(rowList.toList())
            }
            putPersistentComponents(channel.idLong, message.idLong, containerList.toList())
        }
        componentCache.put(channel.idLong, message.idLong, container)
    }

    private fun putPersistentComponents(channelId: Long, messageId: Long, persistentComponentList: List<List<PersistentComponent>>) {
        assertWriteMode()

        if (persistentComponents.containsKey(channelId) && persistentComponents[channelId] != null) {
            persistentComponents[channelId]!![messageId] = persistentComponentList
        } else {
            persistentComponents[channelId] = HashMap()
            persistentComponents[channelId]!![messageId] = persistentComponentList
        }
    }

    //endregion

    fun findVoteGroupByMessageAndChannel(channelId: Long, messageId: Long): VoteGroup? {
        return voteGroups.entries.map { it.value }.find { it.channelId == channelId && it.messageId == messageId }
    }
    fun write(writer: Consumer<GuildData>) {
        this.lock.writeLock().withLock {
            val copy: GuildData = CascadeBot.INS.databaseManager.guildDataDataHandler.deepCopy(this)
            writeMode.set(true)
            writer.accept(copy);
            writeMode.set(false)
            GuildDataManager.updateBson(guildId, CascadeBot.INS.databaseManager.guildDataDataHandler.diffUpdate(this, copy), copy)
            //println(GsonBuilder().setPrettyPrinting().create().toJson(diff))
        }
    }

    fun <T : Any?> writeInline(writer: Function<GuildData, T>) : T {
        this.lock.writeLock().withLock {
            val copy: GuildData = CascadeBot.INS.databaseManager.guildDataDataHandler.deepCopy(this)
            writeMode.set(true)
            val output = writer.apply(copy);
            writeMode.set(false)
            GuildDataManager.updateBson(guildId, CascadeBot.INS.databaseManager.guildDataDataHandler.diffUpdate(this, copy), copy)
            //println(GsonBuilder().setPrettyPrinting().create().toJson(diff))
            return output
        }
    }

    override fun fromBson(bsonDocument: BsonDocument) {
        bsonDocument.ifContainsArray("enabledFlags") { array ->
            enabledFlags.clear();
            array.map { it.asString().value }
                .map { Flag.valueOf(it) }
                .forEach { enabledFlags.add(it) }
        }
        bsonDocument.ifContainsLong("mutedRoleId") { mutedRoleId = it }
        bsonDocument.ifContainsArray("persistentButtons") {
            persistentComponents.clear()
            for (entry in it) {
                val channelId = entry.asDocument()["key"]!!.asNumber().longValue()
                val messages = entry.asDocument()["value"]!!.asArray();
                val messageMap = HashMap<Long, List<List<PersistentComponent>>>()
                for (message in messages) {
                    val messageId = message.asDocument()["key"]!!.asNumber().longValue()
                    val rowObj = message.asDocument()["value"]!!.asArray();
                    val messageComp: MutableList<List<PersistentComponent>> = mutableListOf()
                    for (rowBson in rowObj) {
                        val rowComp: MutableList<PersistentComponent> = mutableListOf()
                        for (compBson in rowBson.asArray()) {
                            rowComp.add(PersistentComponent.valueOf(compBson.asString().value))
                        }
                        messageComp.add(rowComp)
                    }
                    messageMap[messageId] = messageComp
                }
                persistentComponents[channelId] = messageMap
            }
            loadComponents()
        }
        bsonDocument.ifContainsDocument("core") { core.fromBson(it) }
        bsonDocument.ifContainsDocument("useful") { useful.fromBson(it) }
        bsonDocument.ifContainsDocument("moderation") { moderation.fromBson(it) }
        bsonDocument.ifContainsDocument("management") { management.fromBson(it) }
        bsonDocument.ifContainsDocument("music") { music.fromBson(it) }
    }

    override fun handleRemove(tree: DataHandler.RemovedTree) {
        tree.ifContains("useful") {
            useful.handleRemove(it)
        }
        tree.ifContains("moderation") {
            moderation.handleRemove(it)
        }
        tree.ifContains("management") {
            management.handleRemove(it)
        }
    }

}



