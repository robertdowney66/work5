package com.yuyu.pojo.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("`like`")
/**
 * 对应数据库中的like表，用于like表数据的封装和传输
 */
public class Like implements Serializable {
    private static final long serialVersionUID = -54979046664113736L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long videoId;
    private Long commentId;
    private Long userId;
}
