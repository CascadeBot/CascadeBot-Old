/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class ConsoleReader implements Runnable {
    @Override
    public void run() {
        CascadeBot.logger.info("Console reading up and running!");
        boolean stop = false;
        while (!stop) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    if (line.equalsIgnoreCase("stop")) {
                        ShutdownHandler.stopWrapper();
                        stop = true;
                    }
                }
            } catch (IOException ignored) {

            }
        }
    }
}
