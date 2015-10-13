package com.dianping.swallow.common.internal;

import java.io.IOException;
import java.util.List;

import org.junit.Before;

import com.dianping.swallow.common.internal.util.MongoUtils;
import com.mongodb.ServerAddress;


/**
 * @author mengwenchao
 *
 * 2015年10月13日 上午11:50:32
 */
public class AbstractMongoTest extends AbstractDbTest{
	
	private String mongoAddress =  "mongodb://192.168.213.143:27018";
	
	@Before
	public void beforeAbstractMongoTest() throws IOException{
		
		
		mongoAddress = serverProperties.getProperty("mongoAddress");
	}
	
	
	@Override
	protected java.lang.String getDbAddress() {
		return mongoAddress;
	}

	
	protected List<ServerAddress> getServerSeeds(){
		
		return MongoUtils.parseUriToAddressList(mongoAddress);
	}
	
}
