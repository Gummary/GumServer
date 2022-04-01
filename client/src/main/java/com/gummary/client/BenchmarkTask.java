package com.gummary.client;

import com.gummary.api.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

import java.nio.charset.StandardCharsets;

/**
 * @author hepeng16
 * @date 2022/3/31 10:12 上午
 */
@Slf4j
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
            String content = generateRandomString();
            Message message = new Message(content.length(), content.getBytes(StandardCharsets.UTF_8));
            client.sendMessage(message);
            Message receivedMessage = client.readMessage();
            log.info("Received message: {}", new String(receivedMessage.getContent(), StandardCharsets.UTF_8));
        }
    }

    private String generateRandomString() {
        return RandomStringUtils.random(10, true, true);
    }
}
