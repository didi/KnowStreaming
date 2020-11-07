package com.xiaojukeji.kafka.manager.monitor.component.n9e.entry;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/10/19
 */
public class N9eNotifyGroup {
    private List<N9eNotifyGroupElem> list;

    public List<N9eNotifyGroupElem> getList() {
        return list;
    }

    public void setList(List<N9eNotifyGroupElem> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "N9eNotifyGroup{" +
                "list=" + list +
                '}';
    }
}