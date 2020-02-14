package org.cascadebot.cascadebot.scheduler;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class ScheduledActionManager {

    public Map<ScheduledAction, ScheduledFuture<?>> scheduledActions = new ConcurrentHashMap<>();

    private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10,
            new ThreadFactoryBuilder().setNameFormat("scheduled-action-%d").build()
    );

    public Duration registerScheduledAction(ScheduledAction action) {
        if (scheduledActions.containsKey(action)) {
            throw new IllegalArgumentException("You cannot register duplicate scheduled actions! Action: " + action.toString());
        }
        ScheduledFuture<?> schedule = executor.schedule(action, action.getDelay(), TimeUnit.MILLISECONDS);
        scheduledActions.put(action, schedule);
        return Duration.between(action.getCreationTime(), action.getExecutionTime());
    }

    public Map<ScheduledAction, ScheduledFuture<?>> getScheduledActions() {
        return Map.copyOf(scheduledActions);
    }

}
