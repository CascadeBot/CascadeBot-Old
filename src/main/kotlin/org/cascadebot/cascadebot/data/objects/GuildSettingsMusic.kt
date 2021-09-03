package org.cascadebot.cascadebot.data.objects

import org.bson.BsonDocument
import org.cascadebot.cascadebot.commandmeta.Module
import org.cascadebot.cascadebot.data.database.BsonObject
import org.cascadebot.cascadebot.data.database.DataHandler
import org.cascadebot.cascadebot.utils.ifContainsArray
import org.cascadebot.cascadebot.utils.ifContainsBoolean
import org.cascadebot.cascadebot.utils.ifContainsInt
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
        bsonDocument.ifContainsBoolean("preserveVolume") {
            preserveVolume = it;
        }
        bsonDocument.ifContainsBoolean("preserveEqualizer") {
            preserveEqualizer = it;
        }
        bsonDocument.ifContainsInt("volume") {
            volume = it;
        }
        bsonDocument.ifContainsBoolean("joinOnPlay") {
            joinOnPlay = it;
        }
        bsonDocument.ifContainsArray("equalizerBands") {
            equalizerBands.clear()
            for (banBson in it) {
                val doc = banBson.asDocument()
                equalizerBands[doc["key"]!!.asNumber().intValue()] = doc["value"]!!.asDouble().value.toFloat()
            }
        }
    }

    override fun handleRemove(tree: DataHandler.RemovedTree) {

    }

}