package com.xiaojukeji.know.streaming.km.rebalance.algorithm.model;

import java.util.Objects;

public class ReplicaPlacementInfo {
    private final int _brokerId;
    private final String _logdir;

    public ReplicaPlacementInfo(int brokerId, String logdir) {
        _brokerId = brokerId;
        _logdir = logdir;
    }

    public ReplicaPlacementInfo(Integer brokerId) {
        this(brokerId, null);
    }

    public Integer brokerId() {
        return _brokerId;
    }

    public String logdir() {
        return _logdir;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ReplicaPlacementInfo)) {
            return false;
        }
        ReplicaPlacementInfo info = (ReplicaPlacementInfo) o;
        return _brokerId == info._brokerId && Objects.equals(_logdir, info._logdir);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_brokerId, _logdir);
    }

    @Override
    public String toString() {
        if (_logdir == null) {
            return String.format("{Broker: %d}", _brokerId);
        } else {
            return String.format("{Broker: %d, Logdir: %s}", _brokerId, _logdir);
        }
    }
}
