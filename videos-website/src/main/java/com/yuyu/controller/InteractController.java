package com.yuyu.controller;

import com.yuyu.pojo.Result;
import com.yuyu.service.InteractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
/**
 * 实现互动相关功能的展示层操作
 */
public class InteractController {

    @Autowired
    private InteractService interactService;

    /**
     * 获取发表评论的参数，传入service层
     * @param videoId 视频id
     * @param commentId 评论id
     * @param content 评论内容
     * @return 操作结果
     */
    @PostMapping("/comment/publish")
    @PreAuthorize("hasAnyAuthority('normal:interact:publish')")
    public Result publish(@RequestParam(value = "video_id",required = false) Long videoId,
                          @RequestParam(value = "comment_id",required = false) Long commentId,@RequestParam("content") String content){

        return interactService.publish(videoId,commentId,content);
    }

    /**
     * 获取获得评论列表的参数，传入service层
     * @param videoId 视频id
     * @param commentId 评论id
     * @param pageNum 页面页码
     * @param pageSize 页面数据条数
     * @return 操作结果
     */
    @GetMapping("/comment/list")
    @PreAuthorize("hasAnyAuthority('normal:interact:comment_list')")
    public Result commentList(@RequestParam(value = "video_id",required = false) Long videoId,
                          @RequestParam(value = "comment_id",required = false) Long commentId,@RequestParam(value = "page_num",required = false) Integer pageNum,
                              @RequestParam(value = "page_size",required = false) Integer pageSize){

        return interactService.commentList(videoId,commentId,pageNum,pageSize);
    }

    /**
     * 获取删除评论的参数，传入service层
     * @param videoId 视频id
     * @param commentId 评论id
     * @return  操作结果
     */
    @DeleteMapping ("/comment/delete")
    @PreAuthorize("hasAnyAuthority('normal:interact:delete')")
    public Result commentDelete(@RequestParam(value = "video_id",required = false) Long videoId,
            @RequestParam(value = "comment_id",required = false) Long commentId){
        return interactService.commentDelete(videoId,commentId);
    }

    /**
     * 获取点赞操作的参数，传入service层
     * @param videoId 视频id
     * @param commentId 评论id
     * @param type 操作类型 点赞-1 不点赞-2
     * @return 操作结果
     */
    @PostMapping("/like/action")
    @PreAuthorize("hasAnyAuthority('normal:interact:action')")
    public Result action(@RequestParam(value = "video_id",required = false) Long videoId,
                         @RequestParam(value = "comment_id",required = false) Long commentId,
                         @RequestParam("action_type") String type){
        return interactService.action(videoId,commentId,type);
    }

    /**
     * 获取点赞了列表的参数，传入service层
     * @param userid 用户id
     * @param pageSize 页面数据条数
     * @param pageNum 页面页码
     * @return 操作结果
     */
    @GetMapping("/like/list")
    @PreAuthorize("hasAnyAuthority('normal:interact:like_list')")
    public Result likeList(@RequestParam(value = "user_id",required = false) Long userid,@RequestParam(value = "page_size",required = false) Integer pageSize,
                           @RequestParam(value = "page_num",required = false) Integer pageNum){
        return interactService.likeList(userid,pageNum,pageSize);
    }
}
