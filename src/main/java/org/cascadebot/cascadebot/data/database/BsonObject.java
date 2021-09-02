package org.cascadebot.cascadebot.data.database;

import org.bson.BsonDocument;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface BsonObject {

    void fromBson(@NotNull BsonDocument bsonDocument);

    //void handleRemove(@NotNull List<String> paths); // TODO not list string

}
