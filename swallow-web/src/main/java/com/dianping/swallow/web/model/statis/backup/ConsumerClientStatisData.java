package com.dianping.swallow.web.model.statis.backup;

import java.util.List;

public class ConsumerClientStatisData {
	
	private ConsumerBaseStatisData statisData;
	
	private List<ConsumerIdStatisData> consumerIdStatisDatas;

	public ConsumerBaseStatisData getStatisData() {
		return statisData;
	}

	public void setStatisData(ConsumerBaseStatisData statisData) {
		this.statisData = statisData;
	}

	public List<ConsumerIdStatisData> getConsumerIdStatisDatas() {
		return consumerIdStatisDatas;
	}

	public void setConsumerIdStatisDatas(List<ConsumerIdStatisData> consumerIdStatisDatas) {
		this.consumerIdStatisDatas = consumerIdStatisDatas;
	}
	
}
