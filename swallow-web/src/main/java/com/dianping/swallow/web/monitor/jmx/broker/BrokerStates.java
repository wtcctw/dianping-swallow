package com.dianping.swallow.web.monitor.jmx.broker;

/**
 * Author   mingdongli
 * 16/2/2  下午2:08.
 */
public enum BrokerStates {

    NotRunning(0),
    Starting(1),
    RecoveringFromUncleanShutdown(2),
    RunningAsBroker(3),
    RunningAsController(4),
    PendingControlledShutdown(6),
    BrokerShuttingDown(7);

    private int state;

    BrokerStates(int state){
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public static BrokerStates findByState(int state) {
        for (BrokerStates code : values()) {
            if (state == code.getState()) {
                return code;
            }
        }
        throw new RuntimeException("Error state : " + state);
    }
}
