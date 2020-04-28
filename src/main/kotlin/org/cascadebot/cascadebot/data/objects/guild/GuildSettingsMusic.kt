package org.cascadebot.cascadebot.data.objects.guild

import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.data.objects.Setting
import org.cascadebot.cascadebot.data.objects.SettingsContainer
import java.util.concurrent.ConcurrentHashMap

@SettingsContainer(module = Module.MUSIC)
class GuildSettingsMusic {

    // TODO: Handle reverting tier for preserve settings
    @Setting
    var preserveVolume = true

    @Setting
    var preserveEqualizer = true

    var volume = 100

    var equalizerBands: MutableMap<Int, Float> = ConcurrentHashMap();

}