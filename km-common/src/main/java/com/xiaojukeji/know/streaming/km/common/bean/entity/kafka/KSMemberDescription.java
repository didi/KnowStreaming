package com.xiaojukeji.know.streaming.km.common.bean.entity.kafka;

import java.util.Objects;
import java.util.Optional;

public class KSMemberDescription {
    private final String memberId;
    private final Optional<String> groupInstanceId;
    private final String clientId;
    private final String host;
    private final KSMemberBaseAssignment assignment;

    public KSMemberDescription(String memberId,
                               Optional<String> groupInstanceId,
                               String clientId,
                               String host,
                               KSMemberBaseAssignment assignment) {
        this.memberId = memberId == null ? "" : memberId;
        this.groupInstanceId = groupInstanceId;
        this.clientId = clientId == null ? "" : clientId;
        this.host = host == null ? "" : host;
        this.assignment = assignment == null ?
                new KSMemberBaseAssignment() : assignment;
    }

    public KSMemberDescription(String memberId,
                               String clientId,
                               String host,
                               KSMemberBaseAssignment assignment) {
        this(memberId, Optional.empty(), clientId, host, assignment);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KSMemberDescription that = (KSMemberDescription) o;
        return memberId.equals(that.memberId) &&
                groupInstanceId.equals(that.groupInstanceId) &&
                clientId.equals(that.clientId) &&
                host.equals(that.host) &&
                assignment.equals(that.assignment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId, groupInstanceId, clientId, host, assignment);
    }

    /**
     * The consumer id of the group member.
     */
    public String consumerId() {
        return memberId;
    }

    /**
     * The instance id of the group member.
     */
    public Optional<String> groupInstanceId() {
        return groupInstanceId;
    }

    /**
     * The client id of the group member.
     */
    public String clientId() {
        return clientId;
    }

    /**
     * The host where the group member is running.
     */
    public String host() {
        return host;
    }

    /**
     * The assignment of the group member.
     */
    public KSMemberBaseAssignment assignment() {
        return assignment;
    }

    @Override
    public String toString() {
        return "(memberId=" + memberId +
                ", groupInstanceId=" + groupInstanceId.orElse("null") +
                ", clientId=" + clientId +
                ", host=" + host +
                ", assignment=" + assignment + ")";
    }
}
