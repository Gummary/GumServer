package com.gummary.blocking;

/**
 * @author hepeng16
 * @date 2022/3/15 2:38 下午
 */
public class Application {


    public static void main(String[] args) {
        Server server = new Server(9999);
        server.start();
    }
}
