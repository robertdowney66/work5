package com.yuyu.dao;

import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;
import com.yuyu.pojo.DO.UserFan;
import org.apache.ibatis.annotations.Mapper;

@Mapper
/**
 * 用于userFan表的crud操作
 */
public interface UserFanDao extends MppBaseMapper<UserFan> {
}
