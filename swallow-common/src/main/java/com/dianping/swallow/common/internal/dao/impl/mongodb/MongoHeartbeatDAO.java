package com.dianping.swallow.common.internal.dao.impl.mongodb;

import java.util.Date;




import com.dianping.swallow.common.internal.dao.HeartbeatDAO;
import com.dianping.swallow.common.internal.dao.impl.AbstractDao;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class MongoHeartbeatDAO extends AbstractDao implements HeartbeatDAO {

	public static final String  TICK = "t";

	protected MongoManager mongoManager;

	public void setMongoManager(DefaultMongoManager mongoManager) {
		
		this.mongoManager = mongoManager;
	}


   @Override
   public Date updateLastHeartbeat(String ip) {
	   
      DBCollection collection = mongoManager.getHeartbeatCollection(ip.replace('.', '_'));

      Date curTime = new Date();
      DBObject insert = BasicDBObjectBuilder.start().add(TICK, curTime).get();
      collection.insert(insert);
      return curTime;
   }

   @Override
   public Date findLastHeartbeat(String ip) {
	   
      DBCollection collection = mongoManager.getHeartbeatCollection(ip.replace('.', '_'));

      DBObject fields = BasicDBObjectBuilder.start().add(TICK, Integer.valueOf(1)).get();
      DBObject orderBy = BasicDBObjectBuilder.start().add(TICK, Integer.valueOf(-1)).get();
      DBCursor cursor = collection.find(null, fields).sort(orderBy).limit(1);
      try {
         if (cursor.hasNext()) {
            DBObject result = cursor.next();
            return (Date) result.get(TICK);
         }
      } finally {
         cursor.close();
      }
      return null;
   }

}
