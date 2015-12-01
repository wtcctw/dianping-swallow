package com.dianping.swallow.common.internal.dao;

import java.util.Date;

public interface HeartbeatDAO<T extends Cluster> extends DAO<T>{

   Date updateLastHeartbeat(String ip);

   Date findLastHeartbeat(String ip);
}
