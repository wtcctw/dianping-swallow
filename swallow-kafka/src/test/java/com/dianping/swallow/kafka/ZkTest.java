package com.dianping.swallow.kafka;

import java.io.File;

import kafka.utils.ZKStringSerializer$;

import org.I0Itec.zkclient.ZkClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author mengwenchao
 *
 * 2015年11月17日 下午5:18:51
 */
public class ZkTest {

	private ZkClient zkClient;
	
	private String zkAddess = "192.168.104.13";
	
	private String path;
	
	@Before
	public void beforeZkTest(){
		
		this.zkClient = new ZkClient(zkAddess, 10000, 10000, ZKStringSerializer$.MODULE$);
		path = getPath("d1/d2/d3");
	}
	
	@Test
	public void testCreate(){
		
		
		Object obj = zkClient.readData(path, true);
		System.out.println(obj);
		
		
	}
	
	protected String getPath(String path){
		
		return new File(new File("/test"), path).getPath();
		
	}
	
	@After
	public void  afterZkTest(){
//		zkClient.delete(path);
	}

}
