package com.dianping.swallow.test.mongo;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Before;

import com.dianping.swallow.test.AbstractUnitTest;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

/**
 * @author mengwenchao
 *
 * 2015年3月23日 下午3:55:36
 */
public class AbstractMongoTest extends AbstractUnitTest{

	protected MongoClient mongo; 
	
	@Before
	public void beforeAbstractMongoTest() throws IOException{

		mongo = createMongo();
		
	}

	private MongoClient createMongo() throws IOException {
		
		Properties p = new Properties();
		p.load(getClass().getClassLoader().getResourceAsStream("swallow-mongo-lion.properties"));
		String producerUri = p.getProperty("swallow.mongo.producerServerURI");
		
		String defaultMongo = "default=mongodb://";
		int defaultIndexBegin = producerUri.indexOf(defaultMongo) + defaultMongo.length();
		int defaultIndexEnd = producerUri.indexOf(";", defaultIndexBegin);
		if(defaultIndexEnd == -1){
			defaultIndexEnd = producerUri.length();
		}
		mongo = new MongoClient(getReplicaSeeds(producerUri.substring(defaultIndexBegin, defaultIndexEnd)));
		return mongo;
	}

	/**
	 * @param subSequence
	 * @return
	 * @throws UnknownHostException 
	 * @throws NumberFormatException 
	 */
	private List<ServerAddress> getReplicaSeeds(String addresses) throws NumberFormatException, UnknownHostException {
		List<ServerAddress>  result = new ArrayList<ServerAddress>();
		
		for(String address : addresses.split(",")){
			
			String[] hp = address.split(":");
			result.add(new ServerAddress(hp[0], Integer.parseInt(hp[1])));
		}
		return result;
	}
	
}
