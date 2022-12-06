package com.xiaojukeji.know.streaming.km.common.bean.entity.kafka;

import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.utils.Utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class KSMemberConsumerAssignment extends KSMemberBaseAssignment {
    private final Set<TopicPartition> topicPartitions;

    /**
     * Creates an instance with the specified parameters.
     *
     * @param topicPartitions List of topic partitions
     */
    public KSMemberConsumerAssignment(Set<TopicPartition> topicPartitions) {
        this.topicPartitions = topicPartitions == null ? Collections.<TopicPartition>emptySet() :
                Collections.unmodifiableSet(new HashSet<>(topicPartitions));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KSMemberConsumerAssignment that = (KSMemberConsumerAssignment) o;

        return Objects.equals(topicPartitions, that.topicPartitions);
    }

    @Override
    public int hashCode() {
        return topicPartitions != null ? topicPartitions.hashCode() : 0;
    }

    /**
     * The topic partitions assigned to a group member.
     */
    public Set<TopicPartition> topicPartitions() {
        return topicPartitions;
    }

    @Override
    public String toString() {
        return "(topicPartitions=" + Utils.join(topicPartitions, ",") + ")";
    }
}
