package com.yuyu.ws.pojo;

import lombok.Data;

import java.util.List;

@Data
public class Message {
    /**
     * 单发 - 1，群发 - 2
     */
    private String type;
    /**
     * 单发就是发送者的名字，群发就是群组的名字
     */
    private String toName;
    /**
     * 信息
     */
    private String message;
}
