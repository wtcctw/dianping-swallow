package com.dianping.swallow.common.consumer;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * 该类用于作消息过滤。<br>
 * 使用<code>createInSetMessageFilter(Set&lt;String&gt; matchTypeSet)</code>
 * 构造实例时，参数matchTypeSet指定了Consumer只消费“Message.type属性包含在matchTypeSet中”的消息
 *
 * @author kezhu.wu
 */
public final class MessageFilter implements Serializable, Cloneable {

    private static final long serialVersionUID = 5643819915814285301L;

    public final static MessageFilter AllMatchFilter = new MessageFilter(FilterType.AllMatch, null);

    public enum FilterType {
        AllMatch, InSet
    }

    ;

    private FilterType type;
    private Set<String> param;

    private MessageFilter() {
    }

    private MessageFilter(FilterType type, Set<String> param) {
        this.type = type;
        this.param = param;
    }

    public static MessageFilter createInSetMessageFilter(String... types) {

        Set<String> filterSet = new HashSet<String>();
        for (String type : types) {
            filterSet.add(type);
        }

        return new MessageFilter(FilterType.InSet, filterSet);
    }

    public static MessageFilter createInSetMessageFilter(Set<String> matchTypeSet) {
        return new MessageFilter(FilterType.InSet, matchTypeSet);
    }

    public FilterType getType() {
        return type;
    }

    public Set<String> getParam() {
        if (param == null) {
            return null;
        }
        return new HashSet<String>(param);
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof MessageFilter)) {
            return false;
        }

        MessageFilter cmpFilter = (MessageFilter) obj;

        if (this.type != cmpFilter.type || !paramEquals(this.param, cmpFilter.param)) {

            return false;
        }

        return true;
    }

    private boolean paramEquals(Set<String> param1, Set<String> param2) {

        if (param1 == param2) {
            return true;
        }

        if (param1 == null || param2 == null) {
            return false;
        }

        if (param1.size() != param2.size()) {
            return false;
        }

        for (String param : param1) {

            if (!param2.contains(param)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return "MessageFilter [param=" + param + ", type=" + type + "]";
    }

    @Override
    public MessageFilter clone() {
        try {
            MessageFilter messageFilter = (MessageFilter) super.clone();
            return messageFilter;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean isFiltered(MessageFilter messageFilter, String type) {
        //过滤type
        if (isFilterable(messageFilter)) {
            if (!messageFilter.getParam().contains(type)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isFilterable(MessageFilter messageFilter) {
        if (messageFilter != null && messageFilter.getType() == MessageFilter.FilterType.InSet && messageFilter.getParam() != null
                && !messageFilter.getParam().isEmpty()) {
            return true;
        }
        return false;
    }

}
