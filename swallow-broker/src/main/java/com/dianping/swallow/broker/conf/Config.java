package com.dianping.swallow.broker.conf;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class Config implements InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(Config.class);
    private static Config       instance;

    @PostConstruct
    public void init() throws FileNotFoundException, IOException {
        LOG.info("Properties: " + this.toString());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        instance = this;
    }

    public static Config getInstance() {
        return instance;
    }

}
