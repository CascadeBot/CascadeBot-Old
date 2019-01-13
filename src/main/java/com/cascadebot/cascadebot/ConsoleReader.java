/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot;

import com.cascadebot.shared.Regex;
import com.cascadebot.shared.SharedConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class ConsoleReader implements Runnable {

    @Override
    public void run() {
        CascadeBot.logger.info("Console reading up and running!");
        boolean stop = false;
        while (!stop && !Thread.interrupted()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith(SharedConstants.BOT_OP_PREFIX)) {
                        line = Regex.MULTISPACE_REGEX.matcher(line).replaceAll(" "); // Remove multiple spaces and replace with a single space
                        String[] args = line.substring(line.indexOf(" ") + 1).split(" "); // Remove prefix from the args
                        if (args[0].equalsIgnoreCase("stop_wrapper")) {
                            ShutdownHandler.stopWrapper();
                            break;
                        } else if (args[0].equalsIgnoreCase("stop")) {
                            ShutdownHandler.stopByWrapper();
                            break;
                        } else {
                            // etc etc etc
                        }
                    } else {
                        // TODO: Log to web console
                    }
                }
            } catch (IOException ignored) {

            }
        }
    }
}
