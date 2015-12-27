package com.dianping.swallow.common.internal.message;

import java.nio.charset.Charset;

import com.dianping.swallow.common.internal.util.ByteUtil;
import com.dianping.swallow.common.message.BytesMessage;

/**
 * @author mengwenchao
 *
 * 2015年12月15日 下午4:25:03
 */
public class BytesSwallowMessage extends SwallowMessage implements BytesMessage{
	
	private static final long serialVersionUID = 1L;
	
	private Charset charset = Charset.forName("utf-8");
	
	private byte []content;


	public void setBytesContent(byte []content){
		this.content = content;
	}

	@Override
	public byte[] getBytesContent() {
		return content;
	}

	@Override
	public void setEncoding(Charset charset) {
		this.charset = charset;
	}
	
	@Override
	public String getContent() {
		return new String(content, charset);
	}

	
	@Override
	protected long contentLength() {
		return content.length;
	}
	
	
	@Override
	public String toString() {
		return "BytesSwallowMessage[ " +toSuccessKeyValuePairs()+ "&content=" + content + "]";
	}
	
	@Override
	public String toKeyValuePairs() {
		return toSuccessKeyValuePairs() + "&content=" + ByteUtil.toHexString(content);
	}
}
