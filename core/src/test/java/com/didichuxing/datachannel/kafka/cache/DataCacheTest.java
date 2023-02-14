package com.didichuxing.datachannel.kafka.cache;

import kafka.zk.ZooKeeperTestHarness;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import static org.junit.Assert.*;

public class DataCacheTest extends ZooKeeperTestHarness {


    private DataCache<String, Integer> cache = null;
    private DataCache<String, Integer> cache2 = null;
    private TestDataProvider dataProvider = null;
    private boolean checkSyncTime;

    public void setUp() {
        super.setUp();
        String node = System.getProperty("node");
        int nodeId = 0;
        if (node != null) {
            nodeId = Integer.parseInt(node);
        }
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(5);
        dataProvider = new TestDataProvider(true);
        cache = new DataCache<>("ice", nodeId, dataProvider, scheduledThreadPoolExecutor,
                new ZkUtil(zkClient().currentZooKeeper()), 3000, 10000);

        cache2 = new DataCache<>("ice", nodeId+1, dataProvider, scheduledThreadPoolExecutor,
                new ZkUtil(zkClient().currentZooKeeper()), 3000, 10000);
    }

    @Test
    public void testCache() throws Exception {
        for (int i=0; i< 600; i++) {
            String key = dataProvider.getRandomKey();
            long commitTimestamp = cache.getCommitTimestamp();

            TestDataProvider.Element element = dataProvider.getElement(key, commitTimestamp);
            Integer value = cache.get(key);
            Integer value2 = null;
            if (element != null) {
                value2 = element.value;
            }

            Thread.sleep(100);
            if (commitTimestamp < cache.getCommitTimestamp()) {
                continue;
            }

            if (element == null) {
                if (value != null) {
                    System.out.println("key: " + key + " ,map value: " + value + " ,element value: " +
                        value2 + ", timestamp: " + commitTimestamp);
                }
                assertNull(value);
            } else {
                if (value == null || element.value != value)  {
                    System.out.println("key: " + key + " ,map value: " + value + " ,element value: " +
                            value2 + ", timestamp: " + commitTimestamp);
                }
                assertNotNull(value);
                assertEquals(element.value, value.intValue());
            }

            long timestamp = dataProvider.getLastTimestamp();
            long timeSpan = (timestamp - commitTimestamp)/1000;
            if (timeSpan < 20) {
                checkSyncTime = true;
            }
            if (checkSyncTime) {
                if (timeSpan >= 20) {
                    System.out.println("now: " + timestamp + ", commit: " + commitTimestamp);
                }
                Assert.assertTrue(timeSpan < 20);
            }
        }
    }

    @Override
    public void tearDown() {
        if (cache != null) {
            cache.stop();
            cache2.stop();
        }
        super.tearDown();
    }
}
