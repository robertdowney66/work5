package com.yuyu.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuyu.pojo.DO.Video;
import org.apache.ibatis.annotations.Mapper;

@Mapper
/**
 * 用于video的crud操作
 */
public interface VideoDao extends BaseMapper<Video> {
}
