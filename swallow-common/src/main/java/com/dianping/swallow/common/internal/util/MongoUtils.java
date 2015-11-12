package com.dianping.swallow.common.internal.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.BSONTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.ServerAddress;

public class MongoUtils {

	protected static final Logger logger = LoggerFactory
			.getLogger(MongoUtils.class);

	private MongoUtils() {
	}

	public static List<ServerAddress> parseUriToAddressList(String uri) {

		uri = uri.trim();
		String schema = "mongodb://";
		if (uri.startsWith(schema)) { // 兼容老各式uri
			uri = uri.substring(schema.length());
		}
		String[] hostPortArr = uri.split(",");
		List<ServerAddress> result = new ArrayList<ServerAddress>();
		for (int i = 0; i < hostPortArr.length; i++) {
			String[] pair = hostPortArr[i].split(":");
			try {
				result.add(new ServerAddress(pair[0].trim(), Integer
						.parseInt(pair[1].trim())));
			} catch (Exception e) {
				throw new IllegalArgumentException(
						e.getMessage()
								+ ". Bad format of mongo uri："
								+ uri
								+ ". The correct format is mongodb://<host>:<port>,<host>:<port>",
						e);
			}
		}
		return result;
	}

	public static BSONTimestamp longToBSONTimestamp(Long messageId) {
		int time = (int) (messageId >>> 32);
		int inc = (int) (messageId & 0xFFFFFFFF);
		BSONTimestamp timestamp = new BSONTimestamp(time, inc);
		return timestamp;
	}

	public static Long BSONTimestampToLong(BSONTimestamp timestamp) {
		int time = timestamp.getTime();
		int inc = timestamp.getInc();
		Long messageId = ((long) time << 32) | inc;
		return messageId;
	}

	public static BSONTimestamp getTimestampByCurTime() {
		
		return getTimestamp(new Date());
	}

	private static BSONTimestamp getTimestamp(Date date) {
		
		int time = (int) (date.getTime() / 1000);
		BSONTimestamp bst = new BSONTimestamp(time, 0);
		return bst;
	}

	public static Long getLongByCurTime() {
		
		return getLongByDate(new Date());
	}

	public static Long getLongByDate(Date date) {

		BSONTimestamp bst = getTimestamp(date);
		
		if (logger.isDebugEnabled()) {
			logger.debug("[getLongByDate][BSONTimestamp]" + bst);
		}
		Long result = BSONTimestampToLong(bst);
		if (logger.isDebugEnabled()) {
			logger.debug("[getLongByDate][messageId]" + result);
		}
		return result;
	}

	public static void main(String[] args) {

		System.out.println(-1 << 32);

		System.out
				.println(MongoUtils.longToBSONTimestamp(6163932025775456305L));
		BSONTimestamp ts = new BSONTimestamp(1435152261, 49);
		System.out.println(BSONTimestampToLong (ts));
		System.out.println(new Date(ts.getTime() * 1000L));
		System.out.println(Integer.MAX_VALUE / 86400 / 30 / 12);
	}
}
