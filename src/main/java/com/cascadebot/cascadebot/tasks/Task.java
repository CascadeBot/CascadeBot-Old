package com.cascadebot.cascadebot.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public abstract class Task implements Runnable {

    private static final String IDLE_NAME = "idle agent worker thread";
    private static final String RUNNING_NAME = "%s agent worker thread";

    private static final ThreadGroup TASK_THREADS = new ThreadGroup("Task Thread Poll");
    private static final ScheduledExecutorService AGENTS = Executors.newScheduledThreadPool(2, runnable -> {
        Thread thread = new Thread(TASK_THREADS, runnable, IDLE_NAME);
        thread.setPriority(4);
        return thread;
    });
    private static final Map<String, ScheduledFuture<?>> tasks = new HashMap<>();


    public static ScheduledExecutorService getScheduler() {
        return AGENTS;
    }


    //only one of each agent, non-static is fine
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final String name;
    private final long timeToSleepInMillis;
    private final long delayInMillis;

    public Task(String name, long timeToSleepInMillis) {
        this.name = name;
        this.timeToSleepInMillis = timeToSleepInMillis;
        this.delayInMillis = 0L;
    }

    public Task(String name, long timeToSleep, TimeUnit unit) {
        this.name = name;
        this.timeToSleepInMillis = unit.toMillis(timeToSleep);
        this.delayInMillis = 0L;
    }

    public Task(String name, long timeToSleepInMillis, long delayInMillis) {
        this.name = name;
        this.timeToSleepInMillis = timeToSleepInMillis;
        this.delayInMillis = delayInMillis;
    }

    public Task(String name, long timeToSleep, long delay, TimeUnit unit) {
        this.name = name;
        this.timeToSleepInMillis = unit.toMillis(timeToSleep);
        this.delayInMillis = unit.toMillis(delay);
    }

    public static boolean cancelTask(String taskName) {
        Iterator<Map.Entry<String, ScheduledFuture<?>>> i = tasks.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, ScheduledFuture<?>> next = i.next();
            if (next.getKey() == null) continue;
            if (next.getKey().equals(taskName)) {
                next.getValue().cancel(false);
                i.remove();
                return true;
            }
        }
        return false;
    }

    public static void shutdown() {
        AGENTS.shutdown();
    }

    public static Map<String, ScheduledFuture<?>> getTasks() {
        return tasks;
    }

    @Override
    public final void run() {
        try {
            Thread.currentThread().setName(String.format(RUNNING_NAME, name));
            execute();
        } catch (Throwable t) {
            log.warn("Whoa! Unhandled throwable!", t);
        } finally {
            Thread.currentThread().setName(IDLE_NAME);
        }
    }

    /**
     * This is where your tasks code actually runs!
     */
    protected abstract void execute();

    public boolean cancel() {
        return Task.cancelTask(this.name);
    }

    public boolean start() {
        if (tasks.containsKey(this.name)) return false;
        tasks.put(this.name, AGENTS.scheduleAtFixedRate(this, delayInMillis, timeToSleepInMillis, TimeUnit.MILLISECONDS));
        return true;
    }

    public boolean startNow() {
        if (tasks.containsKey(this.name)) return false;
        tasks.put(this.name, AGENTS.scheduleAtFixedRate(this, 0L, timeToSleepInMillis, TimeUnit.MILLISECONDS));
        return true;
    }


}
