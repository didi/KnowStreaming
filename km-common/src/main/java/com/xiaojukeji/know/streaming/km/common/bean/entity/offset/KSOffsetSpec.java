package com.xiaojukeji.know.streaming.km.common.bean.entity.offset;

import org.apache.kafka.clients.admin.OffsetSpec;

/**
 * @see OffsetSpec
 */
public class KSOffsetSpec {
    public static class KSEarliestSpec extends KSOffsetSpec { }

    public static class KSLatestSpec extends KSOffsetSpec { }

    public static class KSTimestampSpec extends KSOffsetSpec {
        private final long timestamp;

        public KSTimestampSpec(long timestamp) {
            this.timestamp = timestamp;
        }

        public long timestamp() {
            return timestamp;
        }
    }

    /**
     * Used to retrieve the latest offset of a partition
     */
    public static KSOffsetSpec latest() {
        return new KSOffsetSpec.KSLatestSpec();
    }

    /**
     * Used to retrieve the earliest offset of a partition
     */
    public static KSOffsetSpec earliest() {
        return new KSOffsetSpec.KSEarliestSpec();
    }

    /**
     * Used to retrieve the earliest offset whose timestamp is greater than
     * or equal to the given timestamp in the corresponding partition
     * @param timestamp in milliseconds
     */
    public static KSOffsetSpec forTimestamp(long timestamp) {
        return new KSOffsetSpec.KSTimestampSpec(timestamp);
    }

    private KSOffsetSpec() {
    }
}
