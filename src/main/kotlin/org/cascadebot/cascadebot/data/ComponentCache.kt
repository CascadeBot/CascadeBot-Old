/*
 * Copyright (c) 2022 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import org.cascadebot.cascadebot.utils.interactions.CascadeComponent
import java.time.Duration
import java.util.UUID

object ComponentCache {

    val cache: Cache<UUID, CascadeComponent> = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofDays(14)).build()

}