package com.gummary.client;

import com.gummary.api.Message;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author hepeng16
 * @date 2022/3/17 8:01 下午
 */
@Slf4j
public class Client {

    private SocketChannel socketChannel = null;

    private ByteBuffer msgContentBuf = null;
    private final ByteBuffer msgLengthBuf = ByteBuffer.allocate(4);

    public Client() {
        msgContentBuf = ByteBuffer.allocate(1024);
        socketChannel = openSocketChannel();
    }

    public void connectToServer(String address, int port) {
        try {
            socketChannel.connect(new InetSocketAddress(address, port));
        } catch (IOException e) {
            log.error("连接远程服务器失败");
            throw new RuntimeException("连接远程服务器失败", e);
        }
    }


    private SocketChannel openSocketChannel() {
        SocketChannel socketChannel = null;
        try {
            socketChannel = SocketChannel.open();
        } catch (IOException e) {
            log.error("打开socket连接失败");
            throw new RuntimeException("打开socket连接失败", e);
        }
        return socketChannel;
    }

    public void sendMessage(byte[] content) {
        Message message = new Message(content.length, content);
        writeMessage(message, socketChannel);
    }

    public Message readMessage() {
        return readMessage(socketChannel);
    }

    private Message readMessage(SocketChannel socketChannel) {
        // read message length
        int msgLength = readLengthFrom(socketChannel);

        // read content
        while (msgLength > msgContentBuf.capacity()) {
            msgContentBuf = ByteBuffer.allocate(msgContentBuf.capacity() * 2);
        }
        byte[] msgBytes = readContentFrom(socketChannel, msgLength);

        String content = new String(msgBytes, StandardCharsets.UTF_8);
        log.info("Message length: {}\nMessage Content: {}", msgLength, content);

        return new Message(msgLength, msgBytes);
    }

    private int readLengthFrom(SocketChannel socketChannel) {
        msgLengthBuf.clear();
        while (msgLengthBuf.hasRemaining()) {
            try {
                socketChannel.read(msgLengthBuf);
            } catch (IOException e) {
                log.error("read message length from socket error", e);
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
                log.error("read message content from socket error", e);
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
                log.error("Write to socket channel error", e);
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
                log.error("Write to socket channel error", e);
                throw new RuntimeException("Write to socket channel error", e);
            }
        }
    }

    public void closeSocketChannel() {
        if (Objects.isNull(socketChannel)) {
            return;
        }
        try {
            socketChannel.close();
        } catch (IOException e) {
            log.error("Close socket error", e);
            throw new RuntimeException("Close socket error", e);
        }
    }


}
