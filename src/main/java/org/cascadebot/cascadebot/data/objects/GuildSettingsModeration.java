/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.cascadebot.cascadebot.commandmeta.Module;

@SettingsContainer(module = Module.MODERATION)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class GuildSettingsModeration {

    @Setting
    private boolean purgePinnedMessages = false;

}
