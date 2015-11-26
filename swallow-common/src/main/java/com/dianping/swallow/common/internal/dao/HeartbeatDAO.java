package com.dianping.swallow.common.internal.dao;

import java.util.Date;

public interface HeartbeatDAO extends DAO{

   Date updateLastHeartbeat(String ip);

   Date findLastHeartbeat(String ip);
}
