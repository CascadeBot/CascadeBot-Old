package com.cascadebot.cascadebot.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


// Slightly modified version of FredBoat's "FredBoatAgent"
public abstract class Task implements Runnable {

    private static final String IDLE_NAME = "idle agent worker thread";
    private static final String RUNNING_NAME = "%s agent worker thread";

    private static final Map<Class<? extends Task>, Long> LAST_RUN_TIME = new ConcurrentHashMap<>();

    private static final ThreadGroup TASK_THREADS = new ThreadGroup("Task Thread Poll");
    private static final ScheduledExecutorService AGENTS = Executors.newScheduledThreadPool(2, runnable -> {
        Thread thread = new Thread(TASK_THREADS, runnable, IDLE_NAME);
        thread.setPriority(4);
        return thread;
    });
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

    @Override
    public final void run() {
        LAST_RUN_TIME.put(this.getClass(), System.currentTimeMillis());
        try {
            Thread.currentThread().setName(name);
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

    public static void start(Task agent) {
        LAST_RUN_TIME.put(agent.getClass(), 0L);
        AGENTS.scheduleAtFixedRate(agent, agent.delayInMillis, agent.timeToSleepInMillis, TimeUnit.MILLISECONDS);
    }

    //start the agent without a delay
    public static void startNow(Task agent) {
        LAST_RUN_TIME.put(agent.getClass(), 0L);
        AGENTS.scheduleAtFixedRate(agent, 0L, agent.timeToSleepInMillis, TimeUnit.MILLISECONDS);
    }

    public static Map<Class<? extends Task>, Long> getLastRunTimes() {
        return Collections.unmodifiableMap(LAST_RUN_TIME);
    }

    public static void shutdown() {
        AGENTS.shutdown();
    }


}
