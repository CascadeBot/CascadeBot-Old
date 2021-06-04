package org.cascadebot.cascadebot.utils;

import org.cascadebot.cascadebot.data.objects.GuildData;
import org.slf4j.MDC;

public class GuildDataUtils {

    public static void assertWriteMode() {
        boolean writeMode = GuildData.Companion.getWriteMode().get();
        if (!writeMode) {
            throw new UnsupportedOperationException("Cannot write guild data if not in write mode!");
        }
    }

}
