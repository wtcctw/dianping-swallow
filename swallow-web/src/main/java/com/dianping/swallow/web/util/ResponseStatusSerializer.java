package com.dianping.swallow.web.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author mingdongli
 *
 *         2015年7月28日下午6:32:13
 */
public class ResponseStatusSerializer extends JsonSerializer<ResponseStatus> {

	@Override
	public void serialize(ResponseStatus value, JsonGenerator generator, SerializerProvider arg2) throws IOException,
			JsonProcessingException {

		generator.writeStartObject();
		generator.writeFieldName("status");
		generator.writeNumber(value.getStatus());
		generator.writeFieldName("message");
		generator.writeString(value.getMessage());
		generator.writeEndObject();

	}

}
