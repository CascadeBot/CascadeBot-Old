/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package com.cascadebot.cascadebot.data.database;

import org.apache.commons.lang3.ClassUtils;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.util.EnumSet;

public class EnumSetCodec implements Codec<EnumSet> {

    @Override
    public EnumSet decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartArray();



        return null;
    }

    @Override
    public void encode(BsonWriter writer, EnumSet value, EncoderContext encoderContext) {
        
    }

    @Override
    public Class<EnumSet> getEncoderClass() {
        return EnumSet.class;
    }

}
