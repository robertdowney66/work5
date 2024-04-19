package com.yuyu.service;


import com.yuyu.pojo.Result;

/**
 * 定义了互动功能业务层所需接口方法
 */
public interface InteractService {

    /**
     * 实现了发布评论的功能
     * @param videoId
     * @param commentId
     * @param content
     * @return 操作结果
     */
    Result publish(Long videoId, Long commentId, String content);

    /**
     * 实现了获取评论列表的功能
     * @param videoId
     * @param commentId
     * @param pageNum
     * @param pageSize
     * @return 操作结果
     */
    Result commentList(Long videoId, Long commentId, Integer pageNum, Integer pageSize);

    /**
     * 实现删除评论的功能
     * @param videoId
     * @param commentId
     * @return 操作结果
     */
    Result commentDelete(Long videoId, Long commentId);

    /**
     * 实现了点赞的功能
     * @param videoId
     * @param commentId
     * @param type
     * @return 操作结果
     */
    Result action(Long videoId, Long commentId, String type);

    /**
     * 实现了获取点赞列表的功能
     * @param userid
     * @param pageNum
     * @param pageSize
     * @return 操作结果
     */
    Result likeList(Long userid, Integer pageNum, Integer pageSize);
}
