/*
 * Copyright (c) 2022 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data

import org.cascadebot.cascadebot.utils.ChannelId
import org.cascadebot.cascadebot.utils.ComponentId
import org.cascadebot.cascadebot.utils.interactions.CascadeComponent

class ComponentCache(val maxSize: Int) {

    private val componentCache: HashMap<ChannelId, LinkedHashMap<ComponentId, CascadeComponent>> = HashMap()

    val cache
        get() = componentCache.toMap()

    fun put(channelId: ChannelId, component: CascadeComponent) {
        componentCache.putIfAbsent(channelId, object : LinkedHashMap<ComponentId, CascadeComponent>() {
            override fun removeEldestEntry(eldest: MutableMap.MutableEntry<ComponentId, CascadeComponent>?): Boolean {
                return size > maxSize
            }
        })

        // Non-null assertion should be fine since if it's absent it would be inserted above
        componentCache[channelId]!![component.id] = component
    }

    fun remove(channelId: ChannelId, componentId: ComponentId) {
        if (channelId in componentCache) {
            componentCache[channelId]!!.remove(componentId)
        }
    }

}