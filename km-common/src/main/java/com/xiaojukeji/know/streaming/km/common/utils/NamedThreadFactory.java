package com.xiaojukeji.know.streaming.km.common.utils;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A default {@link ThreadFactory} implementation that accepts the name prefix
 * of the created threads as a constructor argument. Otherwise, this factory
 * yields the same semantics as the thread factory returned by
 * {@link Executors#defaultThreadFactory()}.
 */
public class NamedThreadFactory implements ThreadFactory {
    private static final AtomicInteger threadPoolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private static final String NAME_PATTERN = "%s-%d-thread";
    private final String threadNamePrefix;

    /**
     * Creates a new {@link NamedThreadFactory} instance
     *
     * @param threadNamePrefix the name prefix assigned to each thread created.
     */
    public NamedThreadFactory(String threadNamePrefix) {
        final SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread()
                .getThreadGroup();
        this.threadNamePrefix = String.format(Locale.ROOT, NAME_PATTERN,
                checkPrefix(threadNamePrefix), threadPoolNumber.getAndIncrement());
    }

    private static String checkPrefix(String prefix) {
        return prefix == null || prefix.length() == 0 ? "Lucene" : prefix;
    }

    /**
     * Creates a new {@link Thread}
     *
     * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
     */
    @Override
    public Thread newThread(Runnable r) {
        final Thread t = new Thread(group, r, String.format(Locale.ROOT, "%s-%d",
                this.threadNamePrefix, threadNumber.getAndIncrement()), 0);
        t.setDaemon(false);
        t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }

}