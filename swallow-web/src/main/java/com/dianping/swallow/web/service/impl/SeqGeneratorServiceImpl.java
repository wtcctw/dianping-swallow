package com.dianping.swallow.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.SeqGeneratorDao;
import com.dianping.swallow.web.service.SeqGeneratorService;

@Service("seqGeneratorService")
public class SeqGeneratorServiceImpl implements SeqGeneratorService {
	
	@Autowired
	private SeqGeneratorDao seqGeneratorDao;

	@Override
	public long nextSeq(String category) {
		return seqGeneratorDao.nextSeq(category);
	}

}
