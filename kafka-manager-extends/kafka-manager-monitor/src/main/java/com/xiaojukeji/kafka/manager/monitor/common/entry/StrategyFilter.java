package com.xiaojukeji.kafka.manager.monitor.common.entry;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/5/27
 */
public class StrategyFilter {
    private String tkey;

    private String topt;

    private String tval;

    public String getTkey() {
        return tkey;
    }

    public void setTkey(String tkey) {
        this.tkey = tkey;
    }

    public String getTopt() {
        return topt;
    }

    public void setTopt(String topt) {
        this.topt = topt;
    }

    public String getTval() {
        return tval;
    }

    public void setTval(String tval) {
        this.tval = tval;
    }

    @Override
    public String toString() {
        return "StrategyFilter{" +
                "tkey='" + tkey + '\'' +
                ", topt='" + topt + '\'' +
                ", tval=" + tval +
                '}';
    }

    public boolean paramLegal() {
        if (tkey == null
                || topt == null
                || tval == null) {
            return false;
        }
        return true;
    }
}