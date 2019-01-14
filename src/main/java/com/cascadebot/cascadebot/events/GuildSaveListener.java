/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.events;

import com.cascadebot.cascadebot.CascadeBot;
import com.cascadebot.cascadebot.data.mapping.GuildDataMapper;
import com.cascadebot.cascadebot.data.objects.GuildData;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GuildSaveListener implements RemovalListener<Long, GuildData> {

    @Override
    public void onRemoval(@Nullable Long aLong, @Nullable GuildData data, @Nonnull RemovalCause removalCause) {
        if (aLong == null) return;
        GuildDataMapper.insert(aLong, data);
        CascadeBot.logger.debug("Guild with ID: {} was saved to the database as it was removed from the map due to: {}", aLong, removalCause.toString());
        // TODO: FUTURE: Use this for statistics?
    }

}
