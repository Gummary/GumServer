package com.gummary.api;

import java.nio.ByteBuffer;

/**
 * @author hepeng16
 * @date 2022/3/21 11:59 上午
 */
public class MessageBuffer {

    private final static int DEFAULT_CONTENT_SIZE = 1024;

    private ByteBuffer lengthBuf;

    private ByteBuffer contentBuf;

    private boolean autoResize;

    public void MessageBuffer() {
        MessageBuffer(DEFAULT_CONTENT_SIZE, false);
    }

    public void MessageBuffer(boolean autoResize) {
        MessageBuffer(DEFAULT_CONTENT_SIZE, autoResize);
    }

    public void MessageBuffer(int lengthSize) {
        MessageBuffer(lengthSize, false);
    }

    public void MessageBuffer(int lengthSize, boolean autoResize) {
        lengthBuf = ByteBuffer.allocate(4);
        contentBuf = ByteBuffer.allocate(lengthSize);
        this.autoResize = autoResize;
    }


}
