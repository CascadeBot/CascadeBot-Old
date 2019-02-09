/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot;

import com.cascadebot.cascadebot.data.Config;
import com.cascadebot.cascadebot.permissions.Security;
import com.cascadebot.shared.Regex;
import com.cascadebot.shared.SecurityLevel;
import com.cascadebot.shared.SharedConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

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
                        } else if (args[0].equalsIgnoreCase("user")) {
                            if (args.length > 2) {
                                Long id = Long.parseLong(args[1]);
                                if (Config.INS.getAuth().verifyEncrypt(args[1], args[2])) {
                                    String[] roles = args[3].split(",");

                                    Set<Long> ids = Arrays.stream(roles).map(Long::parseLong).collect(Collectors.toSet());

                                    SecurityLevel userLevel = Security.getLevelById(id, ids);
                                    if (userLevel != null) {
                                        CascadeBot.logger.info(userLevel.name());
                                        if (userLevel.isAuthorised(SecurityLevel.STAFF)) {
                                            System.out.println(SharedConstants.WRAPPER_OP_PREFIX + " authorized " + args[1] + " " + userLevel.name());
                                            continue;
                                        }
                                    }
                                }
                                System.out.println(SharedConstants.WRAPPER_OP_PREFIX + " not_authorized " + args[1]);
                            } else {
                                // This should never be called and if it is something is very wrong
                                CascadeBot.logger.error("Received authorization request from bot with no user id and/or hmac");
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
