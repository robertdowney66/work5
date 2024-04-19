package com.yuyu.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuyu.pojo.DO.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
/**
 * 用于menu的crud操作
 */
public interface MenuDao extends BaseMapper<Menu> {

    List<String> getPermsByRoleId(Long rid);

}
