package org.cascadebot.cascadebot.utils;

import org.slf4j.MDC;

public class GuildDataUtils {

    public static void assertWriteMode() {
        String mode = MDC.get("writeMode");
        if (mode == null) {
            throw new UnsupportedOperationException("Cannot write guild data if not in write mode!");
        }
        boolean writeMode = Boolean.parseBoolean(mode);
        if (!writeMode) {
            throw new UnsupportedOperationException("Cannot write guild data if not in write mode!");
        }
    }

}
