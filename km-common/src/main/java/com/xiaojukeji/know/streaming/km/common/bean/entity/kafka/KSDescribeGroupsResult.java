package com.xiaojukeji.know.streaming.km.common.bean.entity.kafka;

import org.apache.kafka.common.KafkaFuture;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class KSDescribeGroupsResult {
    private final Map<String, KafkaFuture<KSGroupDescription>> futures;

    public KSDescribeGroupsResult(final Map<String, KafkaFuture<KSGroupDescription>> futures) {
        this.futures = futures;
    }

    /**
     * Return a map from group id to futures which yield group descriptions.
     */
    public Map<String, KafkaFuture<KSGroupDescription>> describedGroups() {
        return futures;
    }

    /**
     * Return a future which yields all ConsumerGroupDescription objects, if all the describes succeed.
     */
    public KafkaFuture<Map<String, KSGroupDescription>> all() {
        return KafkaFuture.allOf(futures.values().toArray(new KafkaFuture[0])).thenApply(
                new KafkaFuture.BaseFunction<Void, Map<String, KSGroupDescription>>() {
                    @Override
                    public Map<String, KSGroupDescription> apply(Void v) {
                        try {
                            Map<String, KSGroupDescription> descriptions = new HashMap<>(futures.size());
                            for (Map.Entry<String, KafkaFuture<KSGroupDescription>> entry : futures.entrySet()) {
                                descriptions.put(entry.getKey(), entry.getValue().get());
                            }
                            return descriptions;
                        } catch (InterruptedException | ExecutionException e) {
                            // This should be unreachable, since the KafkaFuture#allOf already ensured
                            // that all of the futures completed successfully.
                            throw new RuntimeException(e);
                        }
                    }
                });
    }
}
