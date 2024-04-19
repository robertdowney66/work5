package com.yuyu.pojo.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("comment")
/**
 * 对应数据库中的comment表，进行comment表中数据的传输和封装
 */
public class Comment implements Serializable {
    private static final long serialVersionUID = -54979046664113736L;

    @TableId(type = IdType.AUTO)
    private Long commentId;

    private Long userId;
    private Long videoId;
    private Long parentId;
    private String content;
    private Long likeCount;
    private Long childCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
