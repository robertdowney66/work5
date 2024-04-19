package com.yuyu.service;

import com.github.jeffreyning.mybatisplus.service.IMppService;
import com.yuyu.pojo.Result;
import com.yuyu.pojo.DO.UserFan;

/**
 * 定义了社交功能业务层所需的接口方法
 */
public interface SocialService extends IMppService<UserFan> {
    /**
     * 实现了关注的功能
     * @param userId
     * @param type
     * @return 操作结果
     */
    Result action(Long userId, Integer type);

    /**
     * 实现了获取粉丝列表的功能
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return 操作结果
     */
    Result followerList(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 实现了获取关注列表的功能
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return 操作结果
     */
    Result followingList(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 实现获取好友列表的功能
     * @param pageNum
     * @param pageSize
     * @return
     */
    Result fridendsList(Integer pageNum, Integer pageSize);
}
