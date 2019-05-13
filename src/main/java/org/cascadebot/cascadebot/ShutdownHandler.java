/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot;

import org.cascadebot.cascadebot.commands.developer.EvalCommand;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.events.CommandListener;
import org.cascadebot.cascadebot.tasks.Task;
import org.cascadebot.shared.ExitCodes;
import org.cascadebot.shared.SharedConstants;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ShutdownHandler {

    public static final AtomicBoolean SHUTDOWN_LOCK = new AtomicBoolean();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(ShutdownHandler::shutdown));
    }

    public static void stopWrapper() {
        if (!SHUTDOWN_LOCK.getAndSet(true)) return;
        System.exit(ExitCodes.STOP_WRAPPER);
    }

    public static void stopByWrapper() {
        if (!SHUTDOWN_LOCK.getAndSet(true)) return;
        System.exit(ExitCodes.STOPPED_BY_WRAPPER);
    }

    public static void stop() {
        if (!SHUTDOWN_LOCK.getAndSet(true)) return;
        System.out.println(SharedConstants.WRAPPER_OP_PREFIX + " STOP");
        System.out.flush();
        System.exit(ExitCodes.STOP);
    }

    public static void restart() {
        if (!SHUTDOWN_LOCK.getAndSet(true)) return;
        System.out.println(SharedConstants.WRAPPER_OP_PREFIX + " RESTART");
        System.out.flush();
        System.exit(ExitCodes.RESTART);
    }

    public static void exitWithError() {
        if (!SHUTDOWN_LOCK.getAndSet(true)) return;
        System.exit(ExitCodes.ERROR_STOP_NO_RESTART);
    }

    private static void shutdown() {
        CascadeBot.LOGGER.info("Bot shutting down gracefully!");
        long startTime = System.currentTimeMillis(); // Ensures all data is saved before exiting
        GuildDataManager.getGuilds().asMap().forEach(GuildDataManager::replaceSync);
        CascadeBot.LOGGER.info("Took " + (System.currentTimeMillis() - startTime) + "ms to save!");
        EvalCommand.shutdownEvalPool();
        CommandListener.shutdownCommandPool();
        Task.shutdownTaskPool();
        CascadeBot.INS.getShardManager().shutdown();
    }

}
