package com.xiaojukeji.know.streaming.km.common.bean.entity.kafka;

import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.internals.KafkaFutureImpl;

import java.util.ArrayList;
import java.util.Collection;

public class KSListGroupsResult {
    private final KafkaFutureImpl<Collection<ConsumerGroupListing>> all;
    private final KafkaFutureImpl<Collection<ConsumerGroupListing>> valid;
    private final KafkaFutureImpl<Collection<Throwable>> errors;

    public KSListGroupsResult(KafkaFutureImpl<Collection<Object>> future) {
        this.all = new KafkaFutureImpl<>();
        this.valid = new KafkaFutureImpl<>();
        this.errors = new KafkaFutureImpl<>();
        future.thenApply(new KafkaFuture.BaseFunction<Collection<Object>, Void>() {
            @Override
            public Void apply(Collection<Object> results) {
                ArrayList<Throwable> curErrors = new ArrayList<>();
                ArrayList<ConsumerGroupListing> curValid = new ArrayList<>();
                for (Object resultObject : results) {
                    if (resultObject instanceof Throwable) {
                        curErrors.add((Throwable) resultObject);
                    } else {
                        curValid.add((ConsumerGroupListing) resultObject);
                    }
                }
                if (!curErrors.isEmpty()) {
                    all.completeExceptionally(curErrors.get(0));
                } else {
                    all.complete(curValid);
                }
                valid.complete(curValid);
                errors.complete(curErrors);
                return null;
            }
        });
    }

    /**
     * Returns a future that yields either an exception, or the full set of consumer group
     * listings.
     *
     * In the event of a failure, the future yields nothing but the first exception which
     * occurred.
     */
    public KafkaFuture<Collection<ConsumerGroupListing>> all() {
        return all;
    }

    /**
     * Returns a future which yields just the valid listings.
     *
     * This future never fails with an error, no matter what happens.  Errors are completely
     * ignored.  If nothing can be fetched, an empty collection is yielded.
     * If there is an error, but some results can be returned, this future will yield
     * those partial results.  When using this future, it is a good idea to also check
     * the errors future so that errors can be displayed and handled.
     */
    public KafkaFuture<Collection<ConsumerGroupListing>> valid() {
        return valid;
    }

    /**
     * Returns a future which yields just the errors which occurred.
     *
     * If this future yields a non-empty collection, it is very likely that elements are
     * missing from the valid() set.
     *
     * This future itself never fails with an error.  In the event of an error, this future
     * will successfully yield a collection containing at least one exception.
     */
    public KafkaFuture<Collection<Throwable>> errors() {
        return errors;
    }
}
