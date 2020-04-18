/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data.objects;

import lombok.Getter;
import lombok.Setter;
import org.cascadebot.cascadebot.commandmeta.Module;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SettingsContainer(module = Module.MUSIC)
@Getter
@Setter
public class GuildSettingsMusic {

    @Setting
    private boolean preserveVolume = true; //TODO handle reverting tier

    @Setting
    private boolean preserveEqualizer = true;

    private int volume = 100;

    private Map<Integer, Float> equalizerBands = new ConcurrentHashMap<>();

}
