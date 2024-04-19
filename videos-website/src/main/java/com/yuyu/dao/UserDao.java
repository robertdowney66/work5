package com.yuyu.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuyu.pojo.DO.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
/**
 * 用于user的crud操作
 */
public interface UserDao extends BaseMapper<User> {

    /**
     * 通过id查询user所对应的角色
     * @param userid
     * @return
     */
    Integer getRidByUserId(Long userid);

    /**
     * 使用id查询被封禁用户
     * @param userid
     * @return
     */
    User getUserDeletedById(Long userid);

    /**
     * 使用名字查询封禁用户
     * @param userName
     * @return
     */
    User getUserDeletedByName(String userName);
    /**
     * 将用户解封
     * @param userid
     * @return
     */
    Integer updateUserDeleteById(Long userid);
}
