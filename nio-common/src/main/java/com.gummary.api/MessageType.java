package com.gummary.api;

/**
 * @author hepeng16
 * @date 2022/4/20 7:40 下午
 */
public enum MessageType {

    END_MESSAGE((byte) 0, "终止消息"),
    COMMON_MESSAGE((byte) 1, "普通消息");

    MessageType(byte code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    byte code;
    String desc;
}
