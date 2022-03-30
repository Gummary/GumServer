package com.gummary.blocking;

import com.gummary.api.Message;

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
        bindToPort();
        while (true) {
            SocketChannel socketChannel = acceptClientConnection();
            System.out.println("Accept new connection");
            handleIncomingConnection(socketChannel);
            closeSocketChannel(socketChannel);
        }
    }

    private void bindToPort() {
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


    private void handleIncomingConnection(SocketChannel socketChannel) {
        if (Objects.isNull(socketChannel)) {
            return;
        }
        Message message = readMessage(socketChannel);
        writeMessage(message, socketChannel);
    }


    private Message readMessage(SocketChannel socketChannel) {
        // read content length
        int msgLength = readLengthFrom(socketChannel);

        // read content
        while (msgLength > msgContentBuf.capacity()) {
            msgContentBuf = ByteBuffer.allocate(msgContentBuf.capacity() * 2);
        }
        byte[] msgBytes = readContentFrom(socketChannel, msgLength);

        String content = new String(msgBytes, StandardCharsets.UTF_8);
        System.out.printf("Message length: %d\nMessage Content: %s%n", msgLength, content);

        return new Message(msgLength, msgBytes);
    }

    private int readLengthFrom(SocketChannel socketChannel) {
        msgLengthBuf.clear();
        while (msgLengthBuf.hasRemaining()) {
            try {
                socketChannel.read(msgLengthBuf);
            } catch (IOException e) {
                throw new RuntimeException("read message length from socket error", e);
            }
        }

        msgLengthBuf.flip();
        return msgLengthBuf.getInt();
    }

    private byte[] readContentFrom(SocketChannel socketChannel, int msgLength) {
        msgContentBuf.clear();
        msgContentBuf.limit(msgLength);
        while (msgContentBuf.hasRemaining()) {
            try {
                socketChannel.read(msgContentBuf);
            } catch (IOException e) {
                throw new RuntimeException("read message content from socket error", e);
            }
        }

        byte[] msgArray = new byte[msgLength];
        msgContentBuf.flip();
        msgContentBuf.get(msgArray, 0, msgLength);

        return msgArray;
    }

    private void writeMessage(Message message, SocketChannel socketChannel) {
        int length = message.getLength();
        writeLengthTo(socketChannel, length);

        while (msgContentBuf.capacity() < message.getLength()) {
            msgContentBuf = ByteBuffer.allocate(msgContentBuf.capacity() * 2);
        }

        byte[] content = message.getContent();
        writeContentTo(socketChannel, content);
    }

    private void writeLengthTo(SocketChannel socketChannel, int length) {
        msgLengthBuf.clear();
        msgLengthBuf.putInt(length);

        msgLengthBuf.flip();
        while (msgLengthBuf.hasRemaining()) {
            try {
                socketChannel.write(msgLengthBuf);
            } catch (IOException e) {
                throw new RuntimeException("Write to socket channel error", e);
            }
        }
    }

    private void writeContentTo(SocketChannel socketChannel, byte[] content) {
        msgContentBuf.clear();
        msgContentBuf.put(content);

        msgContentBuf.flip();
        while (msgContentBuf.hasRemaining()) {
            try {
                socketChannel.write(msgContentBuf);
            } catch (IOException e) {
                throw new RuntimeException("Write to socket channel error", e);
            }
        }
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

    public void stop() {
        if (serverSocketChannel.isOpen()) {
            try {
                serverSocketChannel.close();
            } catch (IOException e) {
                throw new RuntimeException("Close Server Socket Channel Error", e);
            }
        }
    }

}
