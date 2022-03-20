package com.gummary.blocking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author hepeng16
 * @date 2022/3/15 2:38 下午
 */
public class Server {

    private final int CONTENT_BUF_INIT_LIMIT = 1024;

    private final int port;
    private ServerSocketChannel serverSocketChannel;

    private final ByteBuffer msgLengthBuf;

    private ByteBuffer msgContentBuf;

    public Server(int port) {
        if (port < 0 || port > 65535) {
            throw new RuntimeException("Port is invalid");
        }
        this.port = port;
        msgLengthBuf = ByteBuffer.allocate(4);
        msgContentBuf = ByteBuffer.allocate(1024);
        openServerSocketChannel();
    }

    private void openServerSocketChannel() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
        } catch (IOException e) {
            throw new RuntimeException("打开服务器socket失败", e);
        }
    }


    public void start() {
        bindToPort(port);
        while (true) {
            SocketChannel socketChannel = acceptClientConnection();
            handleIncomingConnection(socketChannel);
            closeSocketChannel(socketChannel);
        }
    }

    private void bindToPort(int port) {
        try {
            serverSocketChannel.bind(new InetSocketAddress(this.port));
        } catch (IOException e) {
            throw new RuntimeException("监听端口失败", e);
        }
    }

    private SocketChannel acceptClientConnection() {
        SocketChannel socketChannel = null;
        try {
            socketChannel = serverSocketChannel.accept();
        } catch (IOException e) {
            throw new RuntimeException("获取客户端连接失败", e);
        }
        return socketChannel;
    }


    public void stop() throws IOException {
        serverSocketChannel.close();
    }

    private void handleIncomingConnection(SocketChannel socketChannel) {
        if (Objects.isNull(socketChannel)) {
            return;
        }
        // read message length
        readLengthFromSocketChannel(socketChannel);
        int msgLength = msgLengthBuf.getInt();
        msgLengthBuf.clear();

        // read content
        while (msgLength > msgContentBuf.capacity()) {
            msgContentBuf = ByteBuffer.allocate(msgContentBuf.capacity() * 2);
        }
        int readMsgLength = readContentFromSocketChannel(socketChannel, msgLength);
        byte[] msgArray = new byte[readMsgLength];
        msgContentBuf.get(msgArray, 0, msgLength);
        msgContentBuf.clear();

        String content = new String(msgArray, StandardCharsets.UTF_8);
        System.out.println(String.format("Message length: %d\nMessage Content: %s", msgLength, content));
    }

    private void readLengthFromSocketChannel(SocketChannel socketChannel) {
        while (msgLengthBuf.hasRemaining()) {
            try {
                int readSize = socketChannel.read(msgLengthBuf);
            } catch (IOException e) {
                throw new RuntimeException("读取数据失败", e);
            }
        }
        msgLengthBuf.flip();
    }

    private int readContentFromSocketChannel(SocketChannel socketChannel, int msgLength) {
        int readMsgLength = 0;
        while (readMsgLength < msgLength) {
            int readSize = 0;
            try {
                readSize = socketChannel.read(msgContentBuf);
            } catch (IOException e) {
                throw new RuntimeException("读取数据失败", e);
            }
            readMsgLength += readSize;
        }
        msgContentBuf.flip();
        return readMsgLength;
    }

    private void closeSocketChannel(SocketChannel socketChannel) {
        if (Objects.isNull(socketChannel)) {
            return;
        }
        try {
            socketChannel.close();
        } catch (IOException e) {
            throw new RuntimeException("关闭");
        }
    }


}
