package com.yuyu.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuyu.pojo.DO.Like;
import org.apache.ibatis.annotations.Mapper;

@Mapper
/**
 * 用于like的crud操作
 */
public interface LikeDao extends BaseMapper<Like> {
}
