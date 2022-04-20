package com.gummary.api;

import java.nio.charset.StandardCharsets;

/**
 * @author hepeng16
 * @date 2022/3/17 11:16 上午
 */
public class Message {

    /**
     * 消息类型
     */
    final byte typeCode;

    /**
     * 发送消息的格式
     */
    final int length;

    /**
     * 消息内容
     */
    byte[] contentArray;

    String content;

    public Message(String content) {
        this.contentArray = content.getBytes(StandardCharsets.UTF_8);
        this.length = this.contentArray.length;
        this.typeCode = MessageType.COMMON_MESSAGE.code;
    }

    public String getContent() {
        return content;
    }
}
