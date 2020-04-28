/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */
package org.cascadebot.cascadebot.data.objects.user

import de.bild.codec.annotations.Id
import org.cascadebot.cascadebot.data.objects.donation.Flag
import org.cascadebot.cascadebot.data.objects.donation.Tier

class CascadeUser(@field:Id private val userId: Long) {

    private constructor() : this(0L) {
        // Private constructor for MongoDB
    }

    val tierName = "default"
    val tier: Tier
        get() = Tier.getTier(tierName)!!

    val blackList: MutableList<Long> = mutableListOf();
    val flags: MutableList<Flag> = mutableListOf();

}