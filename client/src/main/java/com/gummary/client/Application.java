package com.gummary.client;

import com.gummary.api.Message;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @author hepeng16
 * @date 2022/3/17 7:51 下午
 */
@Slf4j
public class Application {

    public static void main(String[] args) {
        Client client = new Client();
        String message = "test";
        client.connectToServer("127.0.0.1", 9999);
        client.sendMessage(message.getBytes(StandardCharsets.UTF_8));
        Message receivedMsg = client.readMessage();
        String content = new String(receivedMsg.getContent(), StandardCharsets.UTF_8);
        log.info("Client received message length: {}\nClient received message content: {}", receivedMsg.getLength(), content);
        client.closeSocketChannel();
    }
}
