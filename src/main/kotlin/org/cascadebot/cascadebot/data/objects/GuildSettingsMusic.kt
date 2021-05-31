package org.cascadebot.cascadebot.data.objects

import org.cascadebot.cascadebot.commandmeta.Module
import java.util.concurrent.ConcurrentHashMap

@SettingsContainer(module = Module.MUSIC)
class GuildSettingsMusic {

    private val objClass = this.javaClass.name

    // TODO: Handle reverting tier for preserve settings
    @Setting
    var preserveVolume = true

    @Setting
    var preserveEqualizer = true

    var volume = 100

    var equalizerBands: MutableMap<Int, Float> = ConcurrentHashMap();

    @Setting
    var joinOnPlay = true;

}