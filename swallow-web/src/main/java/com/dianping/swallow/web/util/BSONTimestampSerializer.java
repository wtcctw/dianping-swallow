package com.dianping.swallow.web.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.bson.types.BSONTimestamp;

import java.io.IOException;

/**
 * Author   mingdongli
 * 15/12/1  上午11:08.
 */
public class BSONTimestampSerializer extends JsonSerializer<BSONTimestamp> {

    @Override
    public void serialize(BSONTimestamp value, JsonGenerator generator, SerializerProvider arg2) throws IOException {

        if (value != null) {
            generator.writeStartObject();


            int inc = value.getInc();
            generator.writeFieldName("_inc");
            generator.writeNumber(String.valueOf(inc));

            int time = value.getTime();
            generator.writeFieldName("_time");
            generator.writeNumber(String.valueOf(time));

            generator.writeEndObject();
        }else{

        }

    }

}
