package com.dianping.swallow.common.internal.consumer;

import com.dianping.swallow.common.message.Destination;

/**
 * @author mengwenchao
 * 
 *         2015年11月11日 上午11:48:19
 */
public class ConsumerId {

	private String consumerId;
	private Destination dest;

	public ConsumerId(String consumerId, Destination dest) {
		this.consumerId = consumerId;
		this.dest = dest;
	}

	public String getConsumerId() {
		return consumerId;
	}

	public void setConsumerId(String consumerId) {
		this.consumerId = consumerId;
	}

	public Destination getDest() {
		return dest;
	}

	public void setDest(Destination dest) {
		this.dest = dest;
	}

	/**
	 * 以topic和consumerId为主键
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((consumerId == null) ? 0 : consumerId.hashCode());
		result = prime * result + ((dest.getName() == null) ? 0 : dest.getName().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		
		ConsumerInfo other = (ConsumerInfo) obj;
		if (consumerId == null) {
			if (other.getConsumerId() != null)
				return false;
		} else if (!consumerId.equals(other.getConsumerId()))
			return false;
		
		if (dest == null) {
			if (other.getDest() != null)
				return false;
		} else if (!dest.equals(other.getDest()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ConsumerId:" + consumerId + "," + dest + ":" + dest.getName();
	}
}
