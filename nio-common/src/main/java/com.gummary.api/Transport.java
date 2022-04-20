package com.gummary.api;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author hepeng16
 * @date 2022/4/20 8:26 下午
 */
@Slf4j
public class Transport {

    private SocketChannel socketChannel;

    public Transport(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }


    public void send(Message message) {
        byte[] messageContent = MessageUtils.serializeMessage(message);

        ByteBuffer msgContentBuf = ByteBuffer.allocate(1024);
        msgContentBuf.clear();
        msgContentBuf.put(messageContent);

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

    public Message receive() {
        byte type = readMessageType();
        int length = readMessageLength();
        byte[] contentArray = readMessageContent(length);

        String content = new String(contentArray, StandardCharsets.UTF_8);
        return new Message(content);
    }

    private byte readMessageType() {
        ByteBuffer typeBuffer = ByteBuffer.allocate(1);
        typeBuffer.clear();
        while (typeBuffer.hasRemaining()) {
            try {
                socketChannel.read(typeBuffer);
            } catch (IOException e) {
                log.error("read message length from socket error", e);
                throw new RuntimeException("read message length from socket error", e);
            }
        }
        typeBuffer.flip();
        return typeBuffer.get();
    }

    private int readMessageLength() {
        ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
        lengthBuffer.clear();
        while (lengthBuffer.hasRemaining()) {
            try {
                socketChannel.read(lengthBuffer);
            } catch (IOException e) {
                log.error("read message length from socket error", e);
                throw new RuntimeException("read message length from socket error", e);
            }
        }
        lengthBuffer.flip();
        return lengthBuffer.getInt();
    }

    private byte[] readMessageContent(int msgLength) {
        ByteBuffer msgContentBuf = ByteBuffer.allocate(msgLength);
        msgContentBuf.clear();
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
        msgContentBuf.get(msgArray);
        return msgArray;
    }

}
