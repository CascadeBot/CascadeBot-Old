/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects;

import de.bild.codec.annotations.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.cascadebot.cascadebot.data.objects.donation.Tier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CascadeUser {

    @Id
    private long userId;

    public CascadeUser(long id) {
        userId = id;
    }

    private Tier tier;

}
