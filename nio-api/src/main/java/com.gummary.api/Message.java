package com.gummary.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hepeng16
 * @date 2022/3/17 11:16 上午
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    /**
     * 发送消息的格式
     */
    private int length;

    /**
     * 消息内容
     */
    private byte[] content;
}
