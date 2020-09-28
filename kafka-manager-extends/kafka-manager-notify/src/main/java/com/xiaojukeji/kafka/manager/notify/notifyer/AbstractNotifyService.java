package com.xiaojukeji.kafka.manager.notify.notifyer;

public abstract class AbstractNotifyService {
    public abstract boolean sendMsg(String username, String content);
}
