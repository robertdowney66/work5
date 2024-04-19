package com.yuyu.pojo.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@TableName("dialogue")
public class Dialogue implements Serializable {
    private String fromId;
    private String toId;
    private String message;
    private String groupId;

    public Dialogue(String fromId, String toId, String groupId,String message) {
        this.fromId = fromId;
        this.toId = toId;
        this.message = message;
        this.groupId = groupId;
    }

    public Dialogue() {
    }
}
