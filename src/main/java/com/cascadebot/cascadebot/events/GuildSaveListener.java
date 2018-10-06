/*
 * Copyright (c) 2018 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.events;

import com.cascadebot.cascadebot.objects.GuildData;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GuildSaveListener implements RemovalListener<Long, GuildData> {

    @Override
    public void onRemoval(@Nullable Long aLong, @Nullable GuildData data, @Nonnull RemovalCause removalCause) {

    }

}
