package com.xiaojukeji.kafka.manager.kcm.component.agent.n9e.entry;

import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.kcm.common.bizenum.ClusterTaskSubStateEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zengqiao
 * @date 20/9/7
 */
public class N9eTaskResult {
    private List<String> waiting;

    private List<String> running;

    private List<String> failed;

    private List<String> success;

    private List<String> timeout;

    private List<String> cancelled;

    private List<String> ignored;

    private List<String> killing;

    private List<String> kill_failed;

    public List<String> getWaiting() {
        return waiting;
    }

    public void setWaiting(List<String> waiting) {
        this.waiting = waiting;
    }

    public List<String> getRunning() {
        return running;
    }

    public void setRunning(List<String> running) {
        this.running = running;
    }

    public List<String> getFailed() {
        return failed;
    }

    public void setFailed(List<String> failed) {
        this.failed = failed;
    }

    public List<String> getSuccess() {
        return success;
    }

    public void setSuccess(List<String> success) {
        this.success = success;
    }

    public List<String> getTimeout() {
        return timeout;
    }

    public void setTimeout(List<String> timeout) {
        this.timeout = timeout;
    }

    public List<String> getCancelled() {
        return cancelled;
    }

    public void setCancelled(List<String> cancelled) {
        this.cancelled = cancelled;
    }

    public List<String> getIgnored() {
        return ignored;
    }

    public void setIgnored(List<String> ignored) {
        this.ignored = ignored;
    }

    public List<String> getKilling() {
        return killing;
    }

    public void setKilling(List<String> killing) {
        this.killing = killing;
    }

    public List<String> getKill_failed() {
        return kill_failed;
    }

    public void setKill_failed(List<String> kill_failed) {
        this.kill_failed = kill_failed;
    }

    @Override
    public String toString() {
        return "N9eTaskResultDTO{" +
                "waiting=" + waiting +
                ", running=" + running +
                ", failed=" + failed +
                ", success=" + success +
                ", timeout=" + timeout +
                ", cancelled=" + cancelled +
                ", ignored=" + ignored +
                ", killing=" + killing +
                ", kill_failed=" + kill_failed +
                '}';
    }

    public Map<String, ClusterTaskSubStateEnum> convert2HostnameStatusMap() {
        Map<String, ClusterTaskSubStateEnum> hostnameStatusMap = new HashMap<>();
        if (ValidateUtils.isNull(waiting)) {
            waiting = new ArrayList<>();
        }
        for (String hostname: waiting) {
            hostnameStatusMap.put(hostname, ClusterTaskSubStateEnum.WAITING);
        }

        if (ValidateUtils.isNull(running)) {
            running = new ArrayList<>();
        }
        for (String hostname: running) {
            hostnameStatusMap.put(hostname, ClusterTaskSubStateEnum.RUNNING);
        }

        if (ValidateUtils.isNull(failed)) {
            failed = new ArrayList<>();
        }
        for (String hostname: failed) {
            hostnameStatusMap.put(hostname, ClusterTaskSubStateEnum.FAILED);
        }

        if (ValidateUtils.isNull(success)) {
            success = new ArrayList<>();
        }
        for (String hostname: success) {
            hostnameStatusMap.put(hostname, ClusterTaskSubStateEnum.SUCCEED);
        }

        if (ValidateUtils.isNull(timeout)) {
            timeout = new ArrayList<>();
        }
        for (String hostname: timeout) {
            hostnameStatusMap.put(hostname, ClusterTaskSubStateEnum.TIMEOUT);
        }

        if (ValidateUtils.isNull(cancelled)) {
            cancelled = new ArrayList<>();
        }
        for (String hostname: cancelled) {
            hostnameStatusMap.put(hostname, ClusterTaskSubStateEnum.CANCELED);
        }

        if (ValidateUtils.isNull(ignored)) {
            ignored = new ArrayList<>();
        }
        for (String hostname: ignored) {
            hostnameStatusMap.put(hostname, ClusterTaskSubStateEnum.IGNORED);
        }

        if (ValidateUtils.isNull(killing)) {
            killing = new ArrayList<>();
        }
        for (String hostname: killing) {
            hostnameStatusMap.put(hostname, ClusterTaskSubStateEnum.KILLING);
        }

        if (ValidateUtils.isNull(kill_failed)) {
            kill_failed = new ArrayList<>();
        }
        for (String hostname: kill_failed) {
            hostnameStatusMap.put(hostname, ClusterTaskSubStateEnum.KILL_FAILED);
        }
        return hostnameStatusMap;
    }
}