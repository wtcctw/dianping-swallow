package com.dianping.swallow.web.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dianping.swallow.web.service.SeqGeneratorService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:applicationContext.xml" })
public class SeqGeneratorSeviceImplTest {

	private static final String CATEGORY = "test";

	@Autowired
	private SeqGeneratorService seqGeneratorService;

	@Test
	public void seqGeneratorTest() {
		for (int i = 0; i < 10; i++) {
			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					printSeq();
				}

			});
			thread.setName("thread_" + i);
			thread.setDaemon(false);
			thread.start();
		}
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void printSeq() {
		for (int i = 0; i < 20; i++) {
			System.out.println(Thread.currentThread().getName() + " " + Integer.toString(i) + "  "
					+ seqGeneratorService.nextSeq(CATEGORY));
		}
	}
}
