package com.gummary.client;

import com.gummary.api.Message;

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
public class Client {

    private SocketChannel socketChannel = null;

    private ByteBuffer contentBuf = null;
    private ByteBuffer lengthBuf = ByteBuffer.allocate(4);

    public void start(String address, int port) {
        socketChannel = openSocketChannel();
        connectToServer(address, port);
    }

    private void connectToServer(String address, int port) {
        try {
            socketChannel.connect(new InetSocketAddress(address, port));
        } catch (IOException e) {
            System.out.println("连接远程服务器失败");
            throw new RuntimeException("连接远程服务器失败", e);
        }
    }


    private SocketChannel openSocketChannel() {
        SocketChannel socketChannel = null;
        try {
            socketChannel = SocketChannel.open();
        } catch (IOException e) {
            System.out.println("打开socket连接失败");
            throw new RuntimeException("打开socket连接失败", e);
        }
        return socketChannel;
    }

    public void sendMessage(String content) {
        Message message = new Message();
        byte[] contentArray = content.getBytes(StandardCharsets.UTF_8);
        message.setLength(contentArray.length);
        message.setContent(contentArray);

        lengthBuf.putInt(message.getLength());
        lengthBuf.flip();
        writeLengthBuf(socketChannel);
        lengthBuf.clear();


        if (Objects.isNull(contentBuf) || contentBuf.capacity() < message.getLength()) {
            contentBuf = ByteBuffer.allocate(message.getLength());
        }
        contentBuf.put(contentArray);
        contentBuf.flip();
        writeContentBuf(socketChannel);
        contentBuf.clear();

        closeSocketChannel(socketChannel);

    }

    private void writeLengthBuf(SocketChannel socketChannel) {
        while (lengthBuf.hasRemaining()) {
            try {
                socketChannel.write(lengthBuf);
            } catch (IOException e) {
                System.out.println("写入socket失败");
                throw new RuntimeException("写入socket失败");
            }
        }
    }

    private void writeContentBuf(SocketChannel socketChannel) {
        while (contentBuf.hasRemaining()) {
            try {
                socketChannel.write(contentBuf);
            } catch (IOException e) {
                System.out.println("写入socket失败");
                throw new RuntimeException("写入socket失败", e);
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


}
