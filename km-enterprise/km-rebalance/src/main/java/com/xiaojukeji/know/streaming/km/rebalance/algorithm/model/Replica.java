package com.xiaojukeji.know.streaming.km.rebalance.algorithm.model;

import org.apache.kafka.common.TopicPartition;

import java.util.Objects;

/**
 * @author leewei
 * @date 2022/4/29
 */
public class Replica {
    private final Load load;
    private final Replica original;
    private final TopicPartition topicPartition;
    private Broker broker;
    private String logDir;
    private boolean isLeader;
    private boolean isOffline;

    public Replica(Broker broker, String logDir, TopicPartition topicPartition, boolean isLeader, boolean isOffline) {
        this(broker, logDir, topicPartition, isLeader, isOffline, false);
    }

    private Replica(Broker broker, String logDir, TopicPartition topicPartition, boolean isLeader, boolean isOffline, boolean isOriginal) {
        if (isOriginal) {
            this.original = null;
        } else {
            this.original = new Replica(broker, logDir, topicPartition, isLeader, isOffline, true);
        }
        this.load = new Load();
        this.topicPartition = topicPartition;
        this.broker = broker;
        this.logDir = logDir;
        this.isLeader = isLeader;
        this.isOffline = isOffline;
    }

    public TopicPartition topicPartition() {
        return topicPartition;
    }

    public Replica original() {
        return original;
    }

    public Broker broker() {
        return broker;
    }

    public void setBroker(Broker broker) {
        checkOriginal();
        this.broker = broker;
    }

    public String logDir() {
        return logDir;
    }

    public void setLogDir(String logDir) {
        this.logDir = logDir;
    }

    public boolean isLeader() {
        return isLeader;
    }

    public Load load() {
        return load;
    }

    void setLoad(Load load) {
        checkOriginal();
        this.load.addLoad(load);
    }

    Load makeFollower() {
        checkOriginal();
        this.isLeader = false;
        // TODO cpu recal
        Load leaderLoadDelta = new Load();
        leaderLoadDelta.setLoad(Resource.NW_OUT, this.load.loadFor(Resource.NW_OUT));
        this.load.subtractLoad(leaderLoadDelta);
        return leaderLoadDelta;
    }

    void makeLeader(Load leaderLoadDelta) {
        checkOriginal();
        this.isLeader = true;
        this.load.addLoad(leaderLoadDelta);
    }

    public boolean isLeaderChanged() {
        checkOriginal();
        return this.original.isLeader != this.isLeader;
    }

    public boolean isChanged() {
        checkOriginal();
        return this.original.broker != this.broker || this.original.isLeader != this.isLeader;
    }

    private void checkOriginal() {
        if (this.original == null) {
            throw new IllegalStateException("This is a original replica, this operation is not supported.");
        }
    }

    @Override
    public boolean equals(Object o) {
        checkOriginal();
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Replica replica = (Replica) o;
        return topicPartition.equals(replica.topicPartition) && this.original.broker.equals(replica.original.broker);
    }

    @Override
    public int hashCode() {
        checkOriginal();
        return Objects.hash(topicPartition, this.original.broker);
    }

    @Override
    public String toString() {
        checkOriginal();
        return "Replica{" +
                "topicPartition=" + topicPartition +
                ", originalBroker=" + this.original.broker.id() +
                ", broker=" + broker.id() +
                ", originalIsLeader=" + this.original.isLeader +
                ", isLeader=" + isLeader +
                ", load=" + load +
                '}';
    }
    //todo:副本状态，待考虑
    public boolean isCurrentOffline() {
        return isOffline;
    }
}
