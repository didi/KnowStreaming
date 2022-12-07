package com.xiaojukeji.know.streaming.km.common.bean.entity.kafka;

import org.apache.kafka.common.ConsumerGroupState;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.acl.AclOperation;
import org.apache.kafka.common.utils.Utils;

import java.util.*;

public class KSGroupDescription {
    private final String groupId;
    private final String protocolType;
    private final Collection<KSMemberDescription> members;
    private final String partitionAssignor;
    private final ConsumerGroupState state;
    private final Node coordinator;
    private final Set<AclOperation> authorizedOperations;

    public KSGroupDescription(String groupId,
                              String protocolType,
                              Collection<KSMemberDescription> members,
                              String partitionAssignor,
                              ConsumerGroupState state,
                              Node coordinator) {
        this(groupId, protocolType, members, partitionAssignor, state, coordinator, Collections.emptySet());
    }

    public KSGroupDescription(String groupId,
                              String protocolType,
                              Collection<KSMemberDescription> members,
                              String partitionAssignor,
                              ConsumerGroupState state,
                              Node coordinator,
                              Set<AclOperation> authorizedOperations) {
        this.groupId = groupId == null ? "" : groupId;
        this.protocolType = protocolType;
        this.members = members == null ? Collections.emptyList() :
                Collections.unmodifiableList(new ArrayList<>(members));
        this.partitionAssignor = partitionAssignor == null ? "" : partitionAssignor;
        this.state = state;
        this.coordinator = coordinator;
        this.authorizedOperations = authorizedOperations;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final KSGroupDescription that = (KSGroupDescription) o;
        return protocolType == that.protocolType &&
                Objects.equals(groupId, that.groupId) &&
                Objects.equals(members, that.members) &&
                Objects.equals(partitionAssignor, that.partitionAssignor) &&
                state == that.state &&
                Objects.equals(coordinator, that.coordinator) &&
                Objects.equals(authorizedOperations, that.authorizedOperations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, protocolType, members, partitionAssignor, state, coordinator, authorizedOperations);
    }

    /**
     * The id of the consumer group.
     */
    public String groupId() {
        return groupId;
    }

    /**
     * If consumer group is simple or not.
     */
    public String protocolType() {
        return protocolType;
    }

    /**
     * A list of the members of the consumer group.
     */
    public Collection<KSMemberDescription> members() {
        return members;
    }

    /**
     * The consumer group partition assignor.
     */
    public String partitionAssignor() {
        return partitionAssignor;
    }

    /**
     * The consumer group state, or UNKNOWN if the state is too new for us to parse.
     */
    public ConsumerGroupState state() {
        return state;
    }

    /**
     * The consumer group coordinator, or null if the coordinator is not known.
     */
    public Node coordinator() {
        return coordinator;
    }

    /**
     * authorizedOperations for this group, or null if that information is not known.
     */
    public  Set<AclOperation> authorizedOperations() {
        return authorizedOperations;
    }

    @Override
    public String toString() {
        return "(groupId=" + groupId +
                ", protocolType=" + protocolType +
                ", members=" + Utils.join(members, ",") +
                ", partitionAssignor=" + partitionAssignor +
                ", state=" + state +
                ", coordinator=" + coordinator +
                ", authorizedOperations=" + authorizedOperations +
                ")";
    }
}
