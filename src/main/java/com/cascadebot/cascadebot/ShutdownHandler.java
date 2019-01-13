/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot;

import com.cascadebot.cascadebot.commands.developer.EvalCommand;
import com.cascadebot.cascadebot.events.CommandListener;
import com.cascadebot.cascadebot.tasks.Task;
import com.cascadebot.shared.ExitCodes;
import com.cascadebot.shared.SharedConstants;

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
        CascadeBot.logger.info("Bot shutting down gracefully!");
        EvalCommand.shutdownEvalPool();
        CommandListener.shutdownCommandPool();
        Task.shutdownTaskPool();
        CascadeBot.INS.getShardManager().shutdown();
    }

}
