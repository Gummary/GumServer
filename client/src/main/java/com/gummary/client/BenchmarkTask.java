package com.gummary.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author hepeng16
 * @date 2022/3/31 10:12 上午
 */
public class BenchmarkTask implements Runnable {

    private final long duration;

    private final Client client;

    private final String address;

    private final int port;

    public BenchmarkTask(long duration, String address, int port) {
        this.duration = duration;
        this.address = address;
        this.port = port;

        client = new Client();
    }

    @Override
    public void run() {
        long endTime = System.currentTimeMillis() + duration;
        while (System.currentTimeMillis() < endTime) {
            client.connectToServer(address, port);
        }
    }

    private String generateRandomString() {
        return RandomStringUtils.random(10, true, true);
    }
}
