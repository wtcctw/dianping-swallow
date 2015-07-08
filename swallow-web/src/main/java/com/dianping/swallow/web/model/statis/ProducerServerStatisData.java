package com.dianping.swallow.web.model.statis;

import java.util.List;

public class ProducerServerStatisData extends AbstractServerStatisData {
	
	private List<ProducerMachineStatisData> statisDatas;

	public List<ProducerMachineStatisData> getStatisDatas() {
		return statisDatas;
	}

	public void setStatisDatas(List<ProducerMachineStatisData> statisDatas) {
		this.statisDatas = statisDatas;
	}

}
