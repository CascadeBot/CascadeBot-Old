package org.cascadebot.cascadebot.data.objects

import org.bson.BsonDocument
import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.data.database.BsonObject
import java.util.concurrent.ConcurrentHashMap

@SettingsContainer(module = Module.MUSIC)
class GuildSettingsMusic : BsonObject {
    // TODO: Handle reverting tier for preserve settings
    @Setting
    var preserveVolume = true

    @Setting
    var preserveEqualizer = true

    var volume = 100

    var equalizerBands: MutableMap<Int, Float> = ConcurrentHashMap();

    @Setting
    var joinOnPlay = true;

    override fun fromBson(bsonDocument: BsonDocument) {

    }

}