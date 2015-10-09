package com.dianping.swallow.web.alarmer.container;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.model.alarm.AlarmMeta;
import com.dianping.swallow.web.model.alarm.AlarmType;
import com.dianping.swallow.web.service.AlarmMetaService;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 上午11:33:46
 */
@Component("alarmMetaContainer")
public class AlarmMetaContainerImpl extends AbstractContainer implements AlarmMetaContainer {

	private static final Logger logger = LoggerFactory.getLogger(AlarmMetaContainerImpl.class);

	private static final Map<Integer, AlarmMeta> alarmMetas = new ConcurrentHashMap<Integer, AlarmMeta>();

	@Autowired
	private AlarmMetaService alarmMetaService;

	@Override
	public AlarmMeta getAlarmMeta(int metaId) {
		return alarmMetas.get(metaId);
	}

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		containerName = "AlarmMetaContainer";
		interval = 120;
		delay = 5;
	}

	@Override
	public void doLoadResource() {
		logger.info("[doAlarmMetaTask] scheduled load alarmMeta info.");
		List<AlarmMeta> alarmMetaTemps = alarmMetaService.findByPage(0, AlarmType.values().length);
		if (alarmMetaTemps != null && alarmMetaTemps.size() > 0) {
			for (AlarmMeta alarmMeta : alarmMetaTemps) {
				alarmMetas.put(alarmMeta.getType().getNumber(), alarmMeta);
			}
		}

	}

}
