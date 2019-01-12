/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot;

import java.util.Scanner;

public class ConsoleReader implements Runnable {
    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        boolean stop = false;
        while (!stop) {
            String command = scanner.next(); //We can expand this in the future if need be.
            if(command.equalsIgnoreCase("stop")) {
                ShutdownHandler.stopWrapper();
                stop = true;
            }
        }
    }
}
