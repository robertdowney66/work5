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
@TableName("`group`")
public class Group implements Serializable {
    private static final long serialVersionUID = -54945646664113736L;
    @TableId(type = IdType.AUTO)
    private String id;
    private String name;
    private String builderId;
}
