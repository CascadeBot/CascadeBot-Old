package org.cascadebot.cascadebot.data.managers

import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.mongodb.client.model.Filters.eq
import org.bson.types.ObjectId
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.data.database.DebugLogCallback
import org.cascadebot.cascadebot.scheduler.ScheduledAction
import java.time.Duration
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class ScheduledActionManager {

    private val COLLECTION: String = "scheduled_actions"

    var scheduledActions: MutableMap<ScheduledAction, ScheduledFuture<*>> = mutableMapOf()

    private val executor = ScheduledThreadPoolExecutor(10,
            ThreadFactoryBuilder().setNameFormat("scheduled-action-%d").build()
    )

    fun registerScheduledAction(action: ScheduledAction, new: Boolean = true): Duration {
        require(!scheduledActions.containsKey(action)) { "You cannot register duplicate scheduled actions! Action: $action" }
        val schedule = executor.schedule(action, action.delay, TimeUnit.MILLISECONDS)
        if (new) saveScheduledAction(action)
        scheduledActions[action] = schedule
        return Duration.between(action.creationTime, action.executionTime)
    }

    fun saveScheduledAction(action: ScheduledAction) {
        CascadeBot.INS.databaseManager.runAsyncTask {
            it.getCollection(COLLECTION, ScheduledAction::class.java).insertOne(
                    action,
                    DebugLogCallback("Inserted new scheduled action: $action")
            )
        }
    }

    fun deleteScheduledAction(id: ObjectId) {
        CascadeBot.INS.databaseManager.runAsyncTask {
            it.getCollection(COLLECTION, ScheduledAction::class.java).deleteOne(
                    eq("_id", id),
                    DebugLogCallback("Deleted scheduled action! Id: $id")
            )
        }
    }

    fun getScheduledActions(guildId: Long) =
            CascadeBot.INS.databaseManager.database.getCollection(COLLECTION, ScheduledAction::class.java)
                    .find(eq("guildId", guildId))

    fun getScheduledActions() =
            CascadeBot.INS.databaseManager.database.getCollection(COLLECTION, ScheduledAction::class.java).find()

}