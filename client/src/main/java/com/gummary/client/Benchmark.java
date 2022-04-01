package com.gummary.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Benchmark {

    public void startBenchmark(int threadNum, int duration) {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        for (int i = 0; i < threadNum; i++) {
            threadPool.submit(new BenchmarkTask(duration, "localhost", 9999));
        }
        threadPool.shutdown();
        while (threadPool.isTerminated()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



}
