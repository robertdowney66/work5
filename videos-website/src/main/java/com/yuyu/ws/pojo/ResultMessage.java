package com.yuyu.ws.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultMessage {
    /**
     * 来者姓名
     */
    private String fromName;
    /**
     * 是否是系统
     */
    private boolean isSystem;
    /**
     * 组别的id
     */
    private String groupId;
    /**
     *
     */
    private Object message;
}
