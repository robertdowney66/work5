package com.yuyu.pojo.DO;


import com.baomidou.mybatisplus.annotation.TableName;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("user_fan")
/**
 * 对应数据库中的userFan表，用于userFan表中数据的传输和封装
 */
public class UserFan implements Serializable {
    private static final long serialVersionUID = -5497904110411373699L;


    @MppMultiId
    private Long userId;

    @MppMultiId
    private Long fanId;
}
