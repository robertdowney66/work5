package com.yuyu.service;

import com.yuyu.pojo.DO.*;
import com.yuyu.pojo.Result;

import java.util.List;
import java.util.Set;

public interface DialogService {

    /**
     * 实现了创建组的功能
     * @param groupName 创建的组名
     * @return 返回创建信息
     */
    Result<Group> build(String groupName);

    /**
     * 实现了加入组的功能
     *
     * @param groupId 加入组的id
     * @return 返回加入信息
     */
    Result<GroupUser> attain(String groupId);

    /**
     * 实现了屏蔽的功能
     * @param ignoreId 加入屏蔽人的id
     * @return 返回屏蔽信息
     */
    Result<Ignore> ignore(String ignoreId);

    /**
     * 获取历史记录
     * @param toId 被查人的id
     * @param groupId 组别序号
     * @return 查询结果
     */
    Result<List<History>> history(String toId, String groupId);

    /**
     * 获取会话列表
     * @param pageNum 查询开始数
     * @param pageSize 查询页面大小
     * @return 返回会话的列表
     */
    Result<Set<Dialogue>> dialogue(Integer pageNum, Integer pageSize);

    /**
     * 创建会话的操作
     * @param toId 要建立会话的人
     * @param groupId 要建立会话的组
     * @return 操作结果
     */
    Result create(String toId,String groupId);
}
