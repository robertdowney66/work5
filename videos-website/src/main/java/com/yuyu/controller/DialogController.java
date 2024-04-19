package com.yuyu.controller;

import com.yuyu.dao.GroupUserDao;
import com.yuyu.pojo.DO.*;
import com.yuyu.pojo.Result;
import com.yuyu.service.DialogService;
import com.yuyu.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/dialog")
public class DialogController {

    @Autowired
    DialogService dialogService;

    @Autowired
    GroupUserDao groupUserDao;

    @Autowired
    RedisCache redisCache;
    /**
     * 创建组
     * @param groupName 组的名字
     * @return 返回创建结果
     */
    @PostMapping("/build")
    public Result<Group> build(@RequestParam("group_name") String groupName){
        return dialogService.build(groupName);
    }

    /**
     * 加入组
     * @param groupId 组的姓名
     * @return 返回加入结果
     */
    @PostMapping("/attain")
    public Result<GroupUser> attain(@RequestParam("group_id") String groupId){
        return dialogService.attain(groupId);
    }

    /**
     * 屏蔽人
     * @param ignoreId 屏蔽人id
     * @return 返回屏蔽结果
     */
    @PostMapping("/ignore")
    public Result<Ignore> ignore(@RequestParam("to_id") String ignoreId){
        return dialogService.ignore(ignoreId);
    }

    /**
     * 返回浏览历史
     * @param toId 所选查询对象的id
     * @param groupId 查询组别的id
     * @return 查询结果
     */
    @GetMapping("/history")
    public Result<List<History>>history(@RequestParam(value = "to_id",required = false) String toId,
                                        @RequestParam(value = "group_id",required = false)String groupId){
        return dialogService.history(toId,groupId);
    }

    /**
     * 查询会话
     * @param pageNum 页面开始数字
     * @param pageSize 页面大小
     * @return 操作结果
     */
    @GetMapping("/dialogue")
    public Result<Set<Dialogue>>dialogue(@RequestParam(value = "page_num",required = false) Integer pageNum, @RequestParam(value = "page_size",required = false) Integer pageSize){
        return dialogService.dialogue(pageNum,pageSize);
    }

    /**
     * 开启会话的操作
     * @param toId 开启会话的人或者群组
     * @return 操作结果
     */
    @PostMapping("/create")
    public Result create(@RequestParam(value = "to_id",required = false) String toId,
                         @RequestParam(value = "group_id",required = false) String groupId){
        return dialogService.create(toId,groupId);
    }




}
