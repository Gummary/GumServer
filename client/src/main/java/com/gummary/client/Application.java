package com.gummary.client;

/**
 * @author hepeng16
 * @date 2022/3/17 7:51 下午
 */
public class Application {

    public static void main(String[] args) {
        Client client = new Client();
        String message = "test";
        client.start("127.0.0.1", 9999);
        client.sendMessage(message);
    }


}
