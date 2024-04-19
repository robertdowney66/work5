package com.yuyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import com.yuyu.dao.UserDao;
import com.yuyu.dao.UserFanDao;
import com.yuyu.exception.BussinessException;
import com.yuyu.exception.Code;
import com.yuyu.pojo.*;
import com.yuyu.pojo.DO.User;
import com.yuyu.pojo.DO.UserFan;
import com.yuyu.pojo.dto.UserDTO;
import com.yuyu.service.SocialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
/**
 * 实现SocialService中的接口方法
 */
public class SocialServiceImpl extends MppServiceImpl<UserFanDao, UserFan> implements SocialService {
    /**
     * 这四个常量用于页码参数为空时的默认设置和点赞操作的类型
     */
    private static final Integer PAGE_NUM=0;
    private static final Integer PAGE_SIZE=5;
    private static final Integer LIKE_ACTION_TYPE=1;
    private static final Integer UNLIKE_ACTION_TYPE=0;

    @Autowired
    UserDao userDao;

    @Autowired
    UserFanDao userFanDao;

    @Override
    public Result action(Long userId, Integer type) {
        if ((!type.equals(LIKE_ACTION_TYPE)&&(!type.equals(UNLIKE_ACTION_TYPE)))){
            throw new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"点赞操作非法,请检查");
        }
        if (Objects.isNull(userId)){
            throw new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"用户id有误");
        }
        User user = userDao.selectById(userId);
        if (Objects.isNull(user)){
            throw new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"用户id有误");
        }

        LoginUser principal = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userDO = principal.getUserDO();

        if(type.equals(UNLIKE_ACTION_TYPE)){
            // 进行取关操作
            // 检查是否已经是取关状态
            LambdaQueryWrapper<UserFan> userFanLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userFanLambdaQueryWrapper.eq(UserFan::getUserId,userId);
            userFanLambdaQueryWrapper.eq(UserFan::getFanId,userDO.getId());
            UserFan userFan1 = userFanDao.selectOne(userFanLambdaQueryWrapper);
            if (Objects.isNull(userFan1)){
                return new Result<>(Result.PROJECT_SUCCESS,"已经处于取关状态");
            }

            UserFan userFan = new UserFan(userId, userDO.getId());
            int i = userFanDao.deleteByMultiId(userFan);
            if(i==0){
                log.error("系统繁忙，请稍后再试");
                throw new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"系统繁忙，请稍后再试");
            }else {
                log.info("删除数据库数据成功");
                return new Result<>(Result.PROJECT_SUCCESS,"success");
            }

        }else{
            // 进行关注操作
            // 检查是否已经是关注状态
            LambdaQueryWrapper<UserFan> userFanLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userFanLambdaQueryWrapper.eq(UserFan::getUserId,userId);
            userFanLambdaQueryWrapper.eq(UserFan::getFanId,userDO.getId());
            UserFan userFan1 = userFanDao.selectOne(userFanLambdaQueryWrapper);
            if (!Objects.isNull(userFan1)){
                return new Result<>(Result.PROJECT_SUCCESS,"已经处于关注状态");
            }

            UserFan userFan = new UserFan(userId, userDO.getId());
            int insert = userFanDao.insert(userFan);
            if(insert==0){
                log.error("系统繁忙，请稍后再试");
                throw new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"系统繁忙，请稍后再试");
            }else {
                log.info("存入数据库数据成功");
                return new Result<>(Result.PROJECT_SUCCESS,"success");
            }
        }
    }

    @Override
    public Result followingList(Long userId, Integer pageNum, Integer pageSize) {
        if (Objects.isNull(pageNum)){
            pageNum=PAGE_NUM;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = PAGE_SIZE;
        }

        // 查看用户是否存在
        User user1 = userDao.selectById(userId);
        if (Objects.isNull(user1)){
            throw new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"未找到该用户");
        }

        // 首先进行查询
        IPage page = new Page(pageNum,pageSize);
        LambdaQueryWrapper<UserFan> userFanLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userFanLambdaQueryWrapper.eq(UserFan::getFanId,userId);
        IPage page1 = userFanDao.selectPage(page, userFanLambdaQueryWrapper);
        log.info("从数据库中获得userFan数据");
        HashMap<String, Object> map = new HashMap<>(6);
        List<UserFan> records = page1.getRecords();
        if (records.isEmpty()){
            map.put("items",records);
            map.put("total",0);
            return new Result(Result.PROJECT_SUCCESS,"success",map);
        }else {
            ArrayList<UserDTO> followings = new ArrayList<>();
            for (UserFan record : records) {
                User user = userDao.selectById(record.getUserId());
                UserDTO userDTO = new UserDTO(user.getId(), user.getUserName(), user.getAvatarUrl());
                followings.add(userDTO);
            }
            map.put("items",followings);
            map.put("total",page1.getTotal());
            return new Result<>(Result.PROJECT_SUCCESS,"success",map);
        }
    }

    @Override
    public Result followerList(Long userId, Integer pageNum, Integer pageSize) {
        if (Objects.isNull(pageNum)){
            pageNum=PAGE_NUM;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = PAGE_SIZE;
        }

        // 查看用户是否存在
        User user1 = userDao.selectById(userId);
        if (Objects.isNull(user1)){
            throw new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"未找到该用户");
        }

        // 首先进行查询
        IPage page = new Page(pageNum,pageSize);

        LambdaQueryWrapper<UserFan> userFanLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userFanLambdaQueryWrapper.eq(UserFan::getUserId,userId);
        IPage page1 = userFanDao.selectPage(page, userFanLambdaQueryWrapper);
        log.info("查出数据库中userFan数据");

        HashMap<String, Object> map = new HashMap<>(6);
        List<UserFan> records = page1.getRecords();
        if (records.isEmpty()){
            map.put("items",records);
            map.put("total",0);
            return new Result(Result.PROJECT_SUCCESS,"success",map);
        }else {
            ArrayList<UserDTO> fans = new ArrayList<>();
            for (UserFan record : records) {
                User user = userDao.selectById(record.getFanId());
                UserDTO userDTO = new UserDTO(user.getId(), user.getUserName(), user.getAvatarUrl());
                fans.add(userDTO);
            }
            map.put("items",fans);
            map.put("total",page1.getTotal());
            return new Result(Result.PROJECT_SUCCESS,"success",map);
        }
    }

    @Override
    public Result fridendsList(Integer pageNum, Integer pageSize) {

        if (Objects.isNull(pageNum)){
            pageNum=PAGE_NUM;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = PAGE_SIZE;
        }

        LoginUser principal = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userDO = principal.getUserDO();

        // 获取此用户的粉丝表
        LambdaQueryWrapper<UserFan> lam = new LambdaQueryWrapper<>();
        lam.eq(UserFan::getUserId,userDO.getId());
        List<UserFan> userFans = userFanDao.selectList(lam);
        log.info("查出userFan数据");

        ArrayList<UserDTO> friends = new ArrayList<>();

        // 查看粉丝表中用户也被该用户关注的
        int flag = 0;
        int friendsNum = 0;
        for (UserFan userFan : userFans) {
            flag++;
            UserFan userFan1 = new UserFan(userFan.getFanId(), userDO.getId());
            if(!Objects.isNull(userFan1)){
                // 说明二者互为好友
                friendsNum++;
                if (flag>pageNum*pageSize&&flag<=pageNum+5){
                    User user = userDao.selectById(userFan.getFanId());
                    UserDTO userDTO = new UserDTO(user.getId(), user.getUserName(), user.getAvatarUrl());
                    friends.add(userDTO);
                }
            }
        }
        HashMap<String, Object> map = new HashMap<>(6);
        map.put("items",friends);
        map.put("total",friendsNum);
        return new Result(Result.PROJECT_SUCCESS,"success",map);
    }
}
