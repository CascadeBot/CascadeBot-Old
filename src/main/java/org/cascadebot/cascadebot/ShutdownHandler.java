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

public class ShutdownHandler {

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(ShutdownHandler::shutdown));
    }

    public static void stopWrapper() {
        System.exit(ExitCodes.STOP_WRAPPER);
    }

    public static void stopByWrapper() {
        System.exit(ExitCodes.STOPPED_BY_WRAPPER);
    }

    public static void stop() {
        System.out.println(SharedConstants.WRAPPER_OP_PREFIX + " STOP");
        System.out.flush();
        System.exit(ExitCodes.STOP);
    }

    public static void restart() {
        System.out.println(SharedConstants.WRAPPER_OP_PREFIX + " RESTART");
        System.out.flush();
        System.exit(ExitCodes.RESTART);
    }

    public static void exitWithError() {
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
