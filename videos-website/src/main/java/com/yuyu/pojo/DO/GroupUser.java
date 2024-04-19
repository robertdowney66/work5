package com.yuyu.pojo.DO;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("group_user")
public class GroupUser implements Serializable {
    private static final long serialVersionUID = -54945646664113896L;
    private String groupId;
    private String memberId;
}
