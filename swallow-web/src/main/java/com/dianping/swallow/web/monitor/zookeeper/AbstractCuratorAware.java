package com.dianping.swallow.web.monitor.zookeeper;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.common.internal.exception.SwallowException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.BoundedExponentialBackoffRetry;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.*;

/**
 * Author   mingdongli
 * 16/2/22  下午6:21.
 */
public abstract class AbstractCuratorAware extends AbstractBaseZkPath implements CuratorAware, InitializingBean{

    private static final String CAT_TYPE = "ZK-Fetcher";

    protected ScheduledExecutorService zkFetcherExecutor = Executors.newScheduledThreadPool(1);

    protected ConcurrentMap<String, CuratorFramework> curatorFrameworkMap = new ConcurrentHashMap<String, CuratorFramework>();

    protected JsonBinder jsonBinder = JsonBinder.getNonEmptyBinder();

    @Override
    public void afterPropertiesSet() throws Exception {
        initCustomConfig();
        zkFetcherExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName());
                catWrapper.doAction(new SwallowAction() {
                    @Override
                    public void doAction() throws SwallowException {
                        doFetchZkData();
                    }
                });
            }
        }, getDelay(), getInterval(), TimeUnit.SECONDS);

    }

    public CuratorFramework getCurator(CuratorConfig config){
        String zkServer = config.getZkConnect();
        CuratorFramework curator = curatorFrameworkMap.get(zkServer);
        if(curator == null){
            curator = CuratorFrameworkFactory.newClient(config.getZkConnect(),
                    new BoundedExponentialBackoffRetry(config.getBaseSleepTimeMs(), config.getMaxSleepTimeMs(), config.getZkMaxRetry()));
            curator.start();
            curatorFrameworkMap.put(zkServer, curator);
        }
        return curator;
    }

    public CuratorFramework getCurator(String zkConnect){
        CuratorConfig curatorConfig = new CuratorConfig(zkConnect);
        return getCurator(curatorConfig);
    }

    protected void initCustomConfig(){

    }

    abstract protected void doFetchZkData();

}
