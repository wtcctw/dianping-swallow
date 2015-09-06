package com.dianping.swallow.common.internal.codec.impl;

import com.dianping.swallow.common.internal.codec.Codec;

/**
 * @author mengwenchao
 *
 * 2015年9月1日 下午3:14:21
 */
public class JsonCodec implements Codec{
	
	private JsonBinder jsonBinder = JsonBinder.getNonEmptyBinder();
	
	private Class<?> encodeClass, decodeClass;
	
	private boolean toBytes = true;
	
	public JsonCodec(Class<?> decodeClass){
		
		this.decodeClass = decodeClass;
	}
	
	public JsonCodec(Class<?> encodeClass, Class<?> decodeClass){
		
		this.encodeClass = encodeClass;
		this.decodeClass = decodeClass;
	}

	@Override
	public Object encode(Object toEncode) {
		
		if(toEncode == null){
			return toEncode;
		}
		
		if(encodeClass != null && toEncode.getClass() != encodeClass){
			return toEncode;
		}
		
		String json = null;
		
		if(toEncode instanceof String){
			json = (String) toEncode;
		}else{
			json = jsonBinder.toJson(toEncode);
		}
		
		if(isToBytes()){
			return json.getBytes(DEFAULT_CHARSET);
		}
		
		return json;
	}

	@Override
	public Object decode(Object toDecode) {
		
		if(! (toDecode instanceof String) ){
			return toDecode;
		}

		return jsonBinder.fromJson((String)toDecode, decodeClass);
	}

	public boolean isToBytes() {
		return toBytes;
	}

	public void setToBytes(boolean toBytes) {
		this.toBytes = toBytes;
	}

}
