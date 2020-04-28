/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */
package org.cascadebot.cascadebot.data.objects.user

import de.bild.codec.annotations.Id
import lombok.AccessLevel
import lombok.Getter
import lombok.NoArgsConstructor
import org.cascadebot.cascadebot.data.objects.donation.Tier

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class CascadeUser(@field:Id private val userId: Long) {
    val tierName = "default"
    val tier: Tier
        get() = Tier.getTier(tierName)

}