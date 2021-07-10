package com.xiaojukeji.kafka.manager.common.zookeeper;

/**
 * @author limeng
 * @date 2017/12/22
 */
public interface StateChangeListener {
    enum State {
        CONNECTION_RECONNECT, //
        CONNECTION_DISCONNECT,
        NODE_DATA_CHANGED,
        CHILD_UPDATED,
        CHILD_ADDED,
        CHILD_DELETED,
        //
        ;
    }

    void init();

    void onChange(State state, String path);
}
