/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot;

import com.cascadebot.cascadebot.commands.developer.EvalCommand;
import com.cascadebot.cascadebot.events.CommandListener;
import com.cascadebot.cascadebot.tasks.Task;
import com.cascadebot.shared.ExitCodes;

public class ShutdownHandler {

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(ShutdownHandler::shutdown));
    }

    public static void stop() {
        System.exit(ExitCodes.STOP);
    }

    public static void restart() {
        System.exit(ExitCodes.RESTART);
    }

    public static void exitWithError() {
        System.exit(ExitCodes.ERROR_STOP_NO_RESTART);
    }

    private static void shutdown() {
        EvalCommand.shutdownEvalPool();
        CommandListener.shutdownCommandPool();
        Task.shutdownTaskPool();
        CascadeBot.INS.getShardManager().shutdown();
    }

}
