/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.events;

import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.data.objects.guild.GuildData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GuildSaveListener implements RemovalListener<Long, GuildData> {

    @Override
    public void onRemoval(@Nullable Long aLong, @Nullable GuildData data, @Nonnull RemovalCause removalCause) {
        if (aLong == null) return;
        GuildDataManager.replace(aLong, data);
        CascadeBot.LOGGER.debug("Guild with ID: {} was saved to the database as it was removed from the map due to: {}", aLong, removalCause.toString());
        // TODO: FUTURE: Use this for statistics?
    }

}
