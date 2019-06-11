/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascade;

import org.slf4j.MDC;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.stream.Collectors;

public class MDCException extends RuntimeException {

    private MDCException(String message, Throwable cause) {
        super(message, cause);

    }

    public static MDCException from(Throwable cause) {
        String message = "";
        if (MDC.getCopyOfContextMap() != null) {
            message = MDC.getCopyOfContextMap()
                         .entrySet()
                         .stream()
                         .map(entry -> entry.getKey() + ": " + entry.getValue())
                         .collect(Collectors.joining("\n"));
        }
        return new MDCException(message, cause);
    }

    /*

        We don't want the main exception to be printed as it is just a wrapper.
        That is why these exist :P

    */

    @Override
    public void printStackTrace(PrintWriter s) {
        s.println(getMessage());
        s.println();
        getCause().printStackTrace(s);
    }

    @Override
    public void printStackTrace(PrintStream s) {
        s.println(getMessage());
        s.println();
        getCause().printStackTrace(s);
    }

}
