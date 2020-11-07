package com.xiaojukeji.kafka.manager.monitor.component.n9e.entry;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/10/18
 */
public class N9eStrategyFilter {
    private String topt;

    private String tkey;

    private List<String> tval;

    public String getTopt() {
        return topt;
    }

    public void setTopt(String topt) {
        this.topt = topt;
    }

    public String getTkey() {
        return tkey;
    }

    public void setTkey(String tkey) {
        this.tkey = tkey;
    }

    public List<String> getTval() {
        return tval;
    }

    public void setTval(List<String> tval) {
        this.tval = tval;
    }

    @Override
    public String toString() {
        return "N9eStrategyFilter{" +
                "topt='" + topt + '\'' +
                ", tkey='" + tkey + '\'' +
                ", tval=" + tval +
                '}';
    }
}