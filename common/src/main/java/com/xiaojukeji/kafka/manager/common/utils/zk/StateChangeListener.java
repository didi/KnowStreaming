package com.xiaojukeji.kafka.manager.common.utils.zk;

/**
 * Created by limeng on 2017/12/22
 */
public interface StateChangeListener {

    enum State {
        CONNECTION_RECONNECT, //
        CONNECTION_DISCONNECT, NODE_DATA_CHANGED, CHILD_UPDATED, CHILD_ADDED, CHILD_DELETED,
        //
        ;
    }

    void onChange(State state, String path);

}
