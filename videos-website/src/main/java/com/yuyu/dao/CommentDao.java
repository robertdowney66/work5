package com.yuyu.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuyu.pojo.DO.Comment;
import org.apache.ibatis.annotations.Mapper;


@Mapper
/**
 * 用于comment的crud操作
 */
public interface CommentDao extends BaseMapper<Comment> {
}
