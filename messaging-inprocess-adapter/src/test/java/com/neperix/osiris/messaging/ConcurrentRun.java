package com.neperix.osiris.messaging;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.AllArgsConstructor;

@AllArgsConstructor
abstract class ConcurrentRun<T> {

    private final int threads;

    void run() throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        AtomicBoolean running = new AtomicBoolean();
        AtomicInteger overlaps = new AtomicInteger();
        CountDownLatch latch = new CountDownLatch(1);

        List<Future<T>> futures = new ArrayList<>(threads);
        for (int i = 0; i < threads; i++) {
            final int counter = i;
            futures.add(pool.submit(() -> {
                latch.await();

                if (running.get()) {
                    overlaps.incrementAndGet();
                }

                running.set(true);
                T result = perform(counter);
                running.set(false);

                return result;
            }));
        }

        latch.countDown();

        for (Future<T> future : futures) {
            future.get();
        }

        assertThat(overlaps.get(), greaterThan(0));
    }

    abstract T perform(int counter);
}
