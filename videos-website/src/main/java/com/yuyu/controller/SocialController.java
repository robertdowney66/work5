package com.yuyu.controller;

import com.yuyu.pojo.Result;
import com.yuyu.service.SocialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
/**
 * 实现社交的展示层功能
 */
public class SocialController {

    @Autowired
    private SocialService socialService;

    /**
     * 接收关注操作的参数，传入service层
     * @param userId 用户id
     * @param type 操作类型 点赞-1 取消点赞-2
     * @return 操作结果
     */
    @PostMapping("/relation/action")
    @PreAuthorize("hasAnyAuthority('noraml:relation:action')")
    public Result action(@RequestParam("to_user_id") Long userId,
                         @RequestParam("action_type") Integer type){
        return socialService.action(userId,type);
    }

    /**
     * 接收获取粉丝列表的参数，传入service层
     * @param userId 用户id
     * @param pageNum 页面页码
     * @param pageSize 页面数据数量
     * @return 操作结果
     */
    @GetMapping ("/follower/list")
    @PreAuthorize("hasAnyAuthority('noraml:relation:follower_list')")
    public Result followerList(@RequestParam("user_id") Long userId,@RequestParam(value = "page_num",required = false) Integer pageNum,
                       @RequestParam(value = "page_size",required = false) Integer pageSize){

        return socialService.followerList(userId,pageNum,pageSize);
    }

    /**
     * 接收获取关注列表的参数，传入service层
     * @param userId 用户id
     * @param pageNum 页面页码
     * @param pageSize 页面数据条数
     * @return 操作结果
     */
    @GetMapping ("/following/list")
    @PreAuthorize("hasAnyAuthority('noraml:relation:following_list')")
    public Result followingList(@RequestParam("user_id") Long userId,@RequestParam(value = "page_num",required = false) Integer pageNum,
                       @RequestParam(value = "page_size",required = false) Integer pageSize){

        return socialService.followingList(userId,pageNum,pageSize);
    }

    /**
     * 接收获取好友列表的参数，传入service层
     * @param pageNum 页面页码
     * @param pageSize 页面数据条数
     * @return 操作结果
     */
    @GetMapping("/friends/list")
    @PreAuthorize("hasAnyAuthority('noraml:relation:friends_list')")
    public Result friendsList(@RequestParam(value = "page_num",required = false) Integer pageNum,
                              @RequestParam(value = "page_size",required = false) Integer pageSize){

        return socialService.fridendsList(pageNum,pageSize);
    }

}
