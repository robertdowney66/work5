package com.yuyu.pojo.DO;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("`ignore`")
public class Ignore implements Serializable {
    private static final long serialVersionUID = -54979046664113111L;
    String userId;
    String ignoreId;
}
