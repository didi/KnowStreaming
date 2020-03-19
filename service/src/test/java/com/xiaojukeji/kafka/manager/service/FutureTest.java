package com.xiaojukeji.kafka.manager.service;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.junit.Test;

public class FutureTest {

    @Test
    public void test() throws InterruptedException, ExecutionException {

        FutureTask<Integer> f1 = new FutureTask<Integer>(new Callable<Integer>() {

            @Override
            public Integer call() throws InterruptedException {
                Thread.sleep(1000L);
                return 1;
            }

        });

        FutureTask<Integer> f2 = new FutureTask<Integer>(new Callable<Integer>() {

            @Override
            public Integer call() throws InterruptedException {
                Thread.sleep(1000L);
                return 2;
            }

        });

        ExecutorService threadPool = Executors.newCachedThreadPool();

        long ct = System.currentTimeMillis();

        threadPool.submit(f1);
        threadPool.submit(f2);
        threadPool.shutdown();

        System.out.println(f1.get() + " : " + f2.get() + " use:"
                           + (System.currentTimeMillis() - ct));
    }
}
