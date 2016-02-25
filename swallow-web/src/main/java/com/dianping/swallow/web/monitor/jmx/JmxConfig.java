package com.dianping.swallow.web.monitor.jmx;

/**
 * Author   mingdongli
 * 16/2/25  上午11:34.
 */
public class JmxConfig {

    private String group;

    private String name;

    private String type;

    private String clazz;

    public JmxConfig(){

    }

    public JmxConfig(String group, String name, String type, String clazz) {
        this.group = group;
        this.name = name;
        this.type = type;
        this.clazz = clazz;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }
}
