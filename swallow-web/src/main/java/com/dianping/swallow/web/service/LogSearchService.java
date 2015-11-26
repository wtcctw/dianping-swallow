package com.dianping.swallow.web.service;

import java.util.List;

/**
 * @author qi.yin
 *         2015/11/17  下午8:01.
 */
public interface LogSearchService {

    List<SearchResult> search(String topic, String cid, long mid);

    public static class SearchResult {

        public SearchResult() {

        }

        public SearchResult(String topic, String cid, long mid) {
            this.topic = topic;
            this.cid = cid;
            this.mid = mid;
        }

        private String type;
        private String topic;
        private String cid;
        private long mid;
        private String date;
        private String ip;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public String getCid() {
            return cid;
        }

        public void setCid(String cid) {
            this.cid = cid;
        }

        public long getMid() {
            return mid;
        }

        public void setMid(long mid) {
            this.mid = mid;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }
    }
}


