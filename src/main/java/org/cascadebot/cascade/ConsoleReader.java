/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascade;

import org.cascadebot.cascade.data.Config;
import org.cascadebot.cascade.permissions.Security;
import org.cascadebot.shared.Regex;
import org.cascadebot.shared.SecurityLevel;
import org.cascadebot.shared.SharedConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleReader implements Runnable {

    @Override
    public void run() {
        Cascade.LOGGER.info("Console reading up and running!");
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
                        } else if (args[0].equalsIgnoreCase("user")) {
                            if (args.length > 2) {
                                long id = Long.parseLong(args[1]);
                                if (Config.INS.getAuth().verifyEncrypt(args[1], args[2])) {
                                    if (Security.isAuthorised(id, SecurityLevel.STAFF)) {
                                        System.out.println(SharedConstants.WRAPPER_OP_PREFIX + " authorized " + args[1]);
                                        continue;
                                    }
                                }
                                System.out.println(SharedConstants.WRAPPER_OP_PREFIX + " not_authorized " + args[1]);
                            } else {
                                // This should never be called and if it is something is very wrong
                                Cascade.LOGGER.error("Received authorization request from bot with no user id and/or hmac");
                            }
                        } else {

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
