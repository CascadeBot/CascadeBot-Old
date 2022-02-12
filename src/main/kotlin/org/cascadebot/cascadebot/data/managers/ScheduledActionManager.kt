package org.cascadebot.cascadebot.data.managers

import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.mongodb.client.FindIterable
import com.mongodb.client.model.Filters.eq
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.data.database.DebugLogCallback
import org.cascadebot.cascadebot.data.entities.ScheduledActionEntity
import java.time.Duration
import java.util.Collections
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

object ScheduledActionManager {

    private val COLLECTION: String = "scheduled_actions"

    var scheduledActions: MutableMap<ScheduledActionEntity, ScheduledFuture<*>> = Collections.synchronizedMap(mutableMapOf())

    private val executor = ScheduledThreadPoolExecutor(10,
            ThreadFactoryBuilder().setNameFormat("scheduled-action-%d").build()
    )

    @JvmStatic
    @JvmOverloads
    fun registerScheduledAction(action: ScheduledActionEntity, new: Boolean = true): Duration {
        require(!scheduledActions.containsKey(action)) { "You cannot register duplicate scheduled actions! Action: $action" }
        require(action.type.verifyDataType(action.data)) { "The type of data is not valid for this action type! Expected: ${action.type.expectedClass.simpleName}, Actual: ${action.data::class.simpleName}" }
        val schedule = executor.schedule(action, action.delay, TimeUnit.MILLISECONDS)
        if (new) saveScheduledAction(action)
        scheduledActions[action] = schedule
        return Duration.between(action.creationTime, action.executionTime)
    }

    fun saveScheduledAction(action: ScheduledActionEntity) {
        CascadeBot.INS.databaseManager.runAsyncTask {
            it.getCollection(COLLECTION, ScheduledActionEntity::class.java).insertOne(
                    action,
                    DebugLogCallback("Inserted new scheduled action: $action")
            )
        }
    }

    fun deleteScheduledAction(id: Int) {
        // TODO this
    }

    fun find(condition: (ScheduledActionEntity) -> Boolean): ScheduledActionEntity? {
        return scheduledActions.keys.find(condition)
    }

    fun filter(condition: (ScheduledActionEntity) -> Boolean): List<ScheduledActionEntity> {
        return scheduledActions.keys.filter(condition)
    }

    fun removeIf(condition: (ScheduledActionEntity) -> Boolean): Boolean {
        return scheduledActions.entries.removeIf { condition(it.key) }
    }

    private fun getScheduledActions(guildId: Long? = null): FindIterable<ScheduledActionEntity> {
        val collection = CascadeBot.INS.databaseManager.database.getCollection(COLLECTION, ScheduledActionEntity::class.java)
        return if (guildId == null) collection.find() else collection.find(eq("guildId", guildId))
    }

    @JvmStatic
    @JvmOverloads
    fun loadAndRegister(guildId: Long? = null) {
        var count = 0
        getScheduledActions(guildId).forEach {
            registerScheduledAction(it, false)
            count++
        }
        CascadeBot.LOGGER.info("Loaded $count scheduled actions!")
    }

}