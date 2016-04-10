package com.dianping.swallow.common.server.monitor.data;

/**
 * @author qi.yin
 *         2016/04/09  下午6:20.
 */
public enum RetrieveType {

    MIN_POINT,

    MAX_POINT,

    LESS_POINT,

    MORE_POINT,

    GENERAL_SECTION,

    ALL_SECTION;

    public boolean isPoint() {
        if (this == MIN_POINT || this == MAX_POINT || this == LESS_POINT || this == MORE_POINT) {
            return true;
        }
        return false;
    }

    public boolean isSection() {
        if (this == GENERAL_SECTION || this == ALL_SECTION) {
            return true;
        }
        return false;
    }
}