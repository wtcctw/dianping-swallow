package com.dianping.swallow.web.model.statis.backup;

import java.util.List;

public class ProducerServerStatisData extends AbstractServerStatisData {
	
	private List<ProducerServerMachineStatisData> statisDatas;

	public List<ProducerServerMachineStatisData> getStatisDatas() {
		return statisDatas;
	}

	public void setStatisDatas(List<ProducerServerMachineStatisData> statisDatas) {
		this.statisDatas = statisDatas;
	}

}
