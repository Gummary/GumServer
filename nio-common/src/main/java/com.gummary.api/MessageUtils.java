package com.gummary.api;

/**
 * @author hepeng16
 * @date 2022/4/20 7:50 下午
 */
public class MessageUtils {

    private final static int INTEGER_LENGTH = 4;

    public static byte[] serializeMessage(Message message) {
        byte[] content = message.contentArray;

        // type(1) + length(4) + content
        byte[] result = new byte[5 + content.length];
        result[0] = message.typeCode;
        for (byte i = 0; i < INTEGER_LENGTH; i++) {
            result[i + 1] = (byte) (message.length >> i * 8);
        }
        System.arraycopy(content, 0, result, 5, content.length);
        return content;
    }
}
