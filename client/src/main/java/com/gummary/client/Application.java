package com.gummary.client;

import com.gummary.api.Message;

import java.nio.charset.StandardCharsets;

/**
 * @author hepeng16
 * @date 2022/3/17 7:51 下午
 */
public class Application {

    public static void main(String[] args) {
        Client client = new Client();
        String message = "test";
        client.connectToServer("127.0.0.1", 9999);
        client.sendMessage(message.getBytes(StandardCharsets.UTF_8));
        Message receivedMsg = client.readMessage();
        String content = new String(receivedMsg.getContent(), StandardCharsets.UTF_8);
        System.out.printf("Client received message length: %d\nClient received message content: %s", receivedMsg.getLength(), content);
        client.closeSocketChannel();
    }


}
