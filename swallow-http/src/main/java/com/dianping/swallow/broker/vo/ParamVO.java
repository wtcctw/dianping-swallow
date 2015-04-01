package com.dianping.swallow.broker.vo;

/**
 * 页面的时间(时，分)选项
 * 
 * @author wukezhu
 */
public class ParamVO {

    private String name;
    private String value;

    public ParamVO(String name, String value) {
        super();
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ParamVO [name=" + name + ", value=" + value + "]";
    }

}
