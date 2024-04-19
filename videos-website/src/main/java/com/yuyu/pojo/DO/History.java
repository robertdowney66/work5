package com.yuyu.pojo.DO;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("history")
public class History implements Serializable {
    private static final long serialVersionUID = -54946666664113896L;
    private String fromId;
    private String toId;
    private String groupId;
    private String message;
    private LocalDateTime createAt;

    public History() {
    }

    public History(String fromId, String toId, String message, LocalDateTime createAt) {
        this.fromId = fromId;
        this.toId = toId;
        this.message = message;
        this.createAt = createAt;
    }
}
