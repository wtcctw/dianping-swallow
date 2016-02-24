package com.dianping.swallow.web.monitor.zookeeper.topic;

import java.util.List;

/**
 * Author   mingdongli
 * 16/2/22  下午7:04.
 */
public class PartitionDescription {

    private int controller_epoch;

    private int leader;

    private int version;

    private int leader_epoch;

    private List<Integer> isr;

    public int getController_epoch() {
        return controller_epoch;
    }

    public void setController_epoch(int controller_epoch) {
        this.controller_epoch = controller_epoch;
    }

    public int getLeader() {
        return leader;
    }

    public void setLeader(int leader) {
        this.leader = leader;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getLeader_epoch() {
        return leader_epoch;
    }

    public void setLeader_epoch(int leader_epoch) {
        this.leader_epoch = leader_epoch;
    }

    public List<Integer> getIsr() {
        return isr;
    }

    public void setIsr(List<Integer> isr) {
        this.isr = isr;
    }
}
