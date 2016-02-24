package com.dianping.swallow.web.monitor.zookeeper;

import com.dianping.swallow.web.monitor.jmx.AbstractReportableKafka;
import org.apache.commons.lang.StringUtils;

/**
 * Author   mingdongli
 * 16/2/22  下午5:58.
 */
public abstract class AbstractBaseZkPath extends AbstractReportableKafka implements BaseZkPath{

    public static final String BACK_SLASH = "/";

    protected abstract String baseZkPath();

    public String zkPath(String path){
        if(StringUtils.isBlank(path)){
            throw new UnsupportedOperationException("path must be nonempty");
        }
        return new StringBuilder().append(baseZkPath()).append(BACK_SLASH).append(path).toString();
    }

    public String zkPathFrom(String parent,String child){
        if(StringUtils.isBlank(parent) || StringUtils.isBlank(child)){
            throw new UnsupportedOperationException("parent and child must be nonempty");
        }
        return new StringBuilder().append(parent).append(BACK_SLASH).append(child).toString();
    }
}
