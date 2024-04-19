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
@TableName("video")
/**
 * 对应数据库中video表，用于存储video表中数据
 */
public class Video implements Serializable {

    private static final long serialVersionUID = -5497904110411373666L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String videoUrl;

    private String coverUrl;

    private String title;

    private String description;

    private Long visitCount;

    private Long likeCount;

    private Long commentCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;
}
