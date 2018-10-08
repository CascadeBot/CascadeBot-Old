package com.cascadebot.cascadebot.tasks;

import com.cascadebot.cascadebot.CascadeBot;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public interface Task {

    ThreadGroup taskThreadGroup = new ThreadGroup("Task Thread Poll");
    ScheduledExecutorService delayService = Executors.newScheduledThreadPool(10, r -> new Thread(taskThreadGroup, r,
            taskThreadGroup.getName() + taskThreadGroup.activeCount()));

    /**
     * This is the thing you run to start the task.
     * DO NOT OVERRIDE THIS IN YOUR TASK!
     */
    default void run() {
        if(repeat() < 0) {
            delayService.schedule(this::execute, delay(), TimeUnit.MILLISECONDS);
        } else {
            delayService.scheduleAtFixedRate(this::execute, delay(), repeat(), TimeUnit.MILLISECONDS);
        }
    }

    /**
     * This is where your tasks code actually runs!
     */
    void execute();

    default long delay() {
        return 0;
    }

    default long repeat() {
        return -1;
    }
}
