/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects.user;

import de.bild.codec.annotations.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cascadebot.cascadebot.data.objects.donation.Tier;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CascadeUser {

    @Id
    private long userId;

    public CascadeUser(long id) {
        userId = id;
    }

    private String tier = "default";

    public Tier getTier() {
        return Tier.getTier(tier);
    }

}
