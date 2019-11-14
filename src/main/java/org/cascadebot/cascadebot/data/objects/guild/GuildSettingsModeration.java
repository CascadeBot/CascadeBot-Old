/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects.guild;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.data.objects.Setting;
import org.cascadebot.cascadebot.data.objects.SettingsContainer;

@SettingsContainer(module = Module.MODERATION)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class GuildSettingsModeration {

    @Setting
    private boolean purgePinnedMessages = false;

}
