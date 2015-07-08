package com.dianping.swallow.web.model.statis.backup;

public class ConsumerBaseStatisData {

	private long senderQpx;
	
	private long senderdelay;
	
	private long ackQpx;
	
	private long ackdelay;

	public long getSenderQpx() {
		return senderQpx;
	}

	public void setSenderQpx(long senderQpx) {
		this.senderQpx = senderQpx;
	}

	public long getSenderdelay() {
		return senderdelay;
	}

	public void setSenderdelay(long senderdelay) {
		this.senderdelay = senderdelay;
	}

	public long getAckQpx() {
		return ackQpx;
	}

	public void setAckQpx(long ackQpx) {
		this.ackQpx = ackQpx;
	}

	public long getAckdelay() {
		return ackdelay;
	}

	public void setAckdelay(long ackdelay) {
		this.ackdelay = ackdelay;
	}
}
