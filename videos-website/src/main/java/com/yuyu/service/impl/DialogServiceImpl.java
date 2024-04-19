package com.yuyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuyu.dao.*;
import com.yuyu.exception.BussinessException;
import com.yuyu.exception.Code;
import com.yuyu.pojo.DO.*;
import com.yuyu.pojo.LoginUser;
import com.yuyu.pojo.Result;
import com.yuyu.service.DialogService;
import com.yuyu.utils.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.awt.*;
import java.security.DigestOutputStream;
import java.util.*;
import java.util.List;

@Service
@Slf4j
public class DialogServiceImpl implements DialogService {
    private static final Integer PAGE_NUM=0;
    private static final Integer PAGE_SIZE=5;
    @Autowired
    GroupDao groupDao;
    @Autowired
    UserDao userDao;
    @Autowired
    IgnoreDao ignoreDao;
    @Autowired
    GroupUserDao groupUserDao;
    @Autowired
    HistoryDao historyDao;
    @Autowired
    RedisCache redisCache;
    @Autowired
    DialogueDao dialogueDao;

    @Override
    public Result<Group> build(String groupName) {
        // 获取创建人的信息
        LoginUser principal = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userDO = principal.getUserDO();

        // 进行组的创建
        Group group = new Group();
        group.setName(groupName);
        group.setBuilderId(userDO.getId().toString());
        groupDao.insert(group);
        log.info("存入成功");

        return new Result<Group>(Result.PROJECT_SUCCESS,"success",group);
    }

    @Override
    public Result<GroupUser> attain(String groupId) {
        // 获取创建人的信息
        LoginUser principal = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userDO = principal.getUserDO();

        // 查看组是否存在
        Group group = groupDao.selectById(groupId);
        if(Objects.isNull(group)){
            throw new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"该组别不存在");
        }
        // 检查是否已经加入群聊
        LambdaQueryWrapper<GroupUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GroupUser::getMemberId,userDO.getId());
        lambdaQueryWrapper.eq(GroupUser::getGroupId,groupId);
        GroupUser groupUser1 = groupUserDao.selectOne(lambdaQueryWrapper);
        if(!Objects.isNull(groupUser1)){
            throw new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"该用户已加入群聊");
        }

        // 连接二者关系
        GroupUser groupUser = new GroupUser(groupId,userDO.getId().toString());
        groupUserDao.insert(groupUser);
        log.info("存入成功");

        return new  Result<GroupUser>(Result.PROJECT_SUCCESS,"success",groupUser);
    }

    @Override
    public Result<Ignore> ignore(String ignoreId) {
        // 获取创建人的信息
        LoginUser principal = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userDO = principal.getUserDO();

        // 检查是否已经存在
        LambdaQueryWrapper<Ignore> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Ignore::getUserId,userDO.getId());
        lambdaQueryWrapper.eq(Ignore::getIgnoreId,ignoreId);
        Ignore ignore1 = ignoreDao.selectOne(lambdaQueryWrapper);
        if(!Objects.isNull(ignore1)){
            log.error(userDO.getId()+"已经屏蔽"+ignoreId);
            throw new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"已经屏蔽");
        }

        // 加入ignore中
        Ignore ignore = new Ignore();
        ignore.setIgnoreId(ignoreId);
        ignore.setUserId(userDO.getId().toString());
        ignoreDao.insert(ignore);
        return new Result<Ignore>(Result.PROJECT_SUCCESS,"success",ignore);
    }

    @Override
    public Result<List<History>> history(String toId, String groupId) {
        // 获取创建人的信息
        LoginUser principal = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userDO = principal.getUserDO();

        if (StringUtils.hasText(toId)){
            // 说明是查询与人之间的历史记录
            // 先查询redis中有无数据
            // 先查from到to
            List<History> cacheList = redisCache.getCacheList("history:" + userDO.getId() + " to " + toId);
            if (cacheList.isEmpty()){
                // 说明redis没有，那就去查数据库

                // 说明该俩个人记录为空
                cacheList = new ArrayList<>();
            }
            List<History> cacheList2 = redisCache.getCacheList("history:" + toId + " to " + userDO.getId());
            if (cacheList.isEmpty()){
                // 说明redis没有，那就去查数据库

                // 说明该俩个人记录为空
                cacheList = new ArrayList<>();
            }
            cacheList2.addAll(cacheList);
            //TODO 排序操作
            List<History> histories = cacheList2;
            return new Result<List<History>>(Result.PROJECT_SUCCESS,"success",histories);
        }else if (StringUtils.hasText(groupId)){
            // 说明是查询群组的聊天记录
            // 先查询redis中有无数据
            // 先查from到to
            List<History> cacheList = redisCache.getCacheList("history:"+groupId);
            if (cacheList.isEmpty()){
                // 说明redis没有，那就去查数据库 TODO

                // 说明该俩个人记录为空
                cacheList = new ArrayList<>();
            }
            return new Result<List<History>>(Result.PROJECT_SUCCESS,"success",cacheList);
        }else{
            // 参数都是空的，抛出异常
            throw new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"参数非法");
        }
    }

    @Override
    public Result<Set<Dialogue>> dialogue(Integer pageNum, Integer pageSize) {
        // 做分页哦  TODO
        if (Objects.isNull(pageNum)){
            pageNum=PAGE_NUM;
        }
        if (Objects.isNull(pageSize)){
            pageSize=PAGE_SIZE;
        }
        // 获取获取人的信息
        LoginUser principal = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userDO = principal.getUserDO();

        // 先从redis中获取数据,没有再去数据库拿
        Set<Dialogue> cacheSet = redisCache.getCacheSet("dialogue:" + userDO.getId());
        if (cacheSet.isEmpty()){
            // 从数据库直接拿
            LambdaQueryWrapper<Dialogue> lam = new LambdaQueryWrapper<>();
            lam.eq(Dialogue::getFromId,userDO.getId());
            List<Dialogue> dialogues = dialogueDao.selectList(lam);
            if(!dialogues.isEmpty()){
                // 有的话就存入redis
                Set<Dialogue> set = new HashSet<>();
                for (Dialogue dialogue : dialogues) {
                    // 判断是否是组的
                    if (StringUtils.hasText(dialogue.getGroupId())){
                        // 不是组的就加入
                        set.add(dialogue);
                    }
                }
                if (!set.isEmpty()){
                    redisCache.setCacheSet("dialogue:"+userDO.getId(),set);
                }
                cacheSet = set;
                // 然后开始找组的
                Set<Dialogue> cacheSet1 = redisCache.getCacheSet("dialogue:group:" + userDO.getId());
                if (cacheSet1.isEmpty()){
                    // 从数据库直接拿
                    // 上面已经查过了
                    Set<Dialogue> setGroup = new HashSet<>();
                    for (Dialogue dialogue : dialogues) {
                        // 判断是否是组的
                        if (!StringUtils.hasText(dialogue.getGroupId())){
                            // 是组的就加入
                            setGroup.add(dialogue);
                        }
                    }
                    if (!setGroup.isEmpty()){
                        redisCache.setCacheSet("dialogue:group:" + userDO.getId(),setGroup);
                    }
                    cacheSet1 = setGroup;
                    cacheSet.addAll(cacheSet1);
                }else {
                    // 说明存在，和上面合并就好
                    cacheSet.addAll(cacheSet1);
                }
            }
        }else {
            // 说明非空,接下来查组里的
            // 然后开始找组的
            Set<Dialogue> cacheSet1 = redisCache.getCacheSet("dialogue:group:" + userDO.getId());
            if (cacheSet1.isEmpty()){
                // 从数据库直接拿
                LambdaQueryWrapper<Dialogue> lam = new LambdaQueryWrapper<>();
                lam.eq(Dialogue::getFromId,userDO.getId());
                List<Dialogue> dialogues = dialogueDao.selectList(lam);
                Set<Dialogue> setGroup = new HashSet<>();
                for (Dialogue dialogue : dialogues) {
                    // 判断是否是组的
                    if (!StringUtils.hasText(dialogue.getGroupId())){
                        // 是组的就加入
                        setGroup.add(dialogue);
                    }
                }
                if (!setGroup.isEmpty()){
                    redisCache.setCacheSet("dialogue:group:" + userDO.getId(),setGroup);
                }
                cacheSet1 = setGroup;
                cacheSet.addAll(cacheSet1);
                return new Result<>(Result.PROJECT_SUCCESS,"success",cacheSet1);
            }else {
                // 说明存在，和上面合并就好
                cacheSet.addAll(cacheSet1);

            }
        }
        return new Result<>(Result.PROJECT_SUCCESS,"success",cacheSet);
    }


    @Override
    public Result create(String toId,String groupId) {
        // 获取获取人的信息
        LoginUser principal = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userDO = principal.getUserDO();

        if(StringUtils.hasText(toId)){
            // 说明是人
            // 检查是否已经创建会话
            // 从redis中查找有无会话消息
            Set<Dialogue> cacheSet = redisCache.getCacheSet("dialogue:" + userDO.getId());
            if(cacheSet.isEmpty()){
                // 说明没有总会话，那创建一个即可
                cacheSet = new HashSet<>();
                Dialogue dialogue = new Dialogue(userDO.getId().toString(),toId,null,null);
                cacheSet.add(dialogue);
                redisCache.setCacheSet("dialogue:"+userDO.getId(),cacheSet);
                log.info("dialogue:"+userDO.getId()+"已存入redis");
            }else {
                // 存在总会话，那就开始查询是否存在会话
                Dialogue dialogue = new Dialogue(userDO.getId().toString(),toId,null,null);
                for (Dialogue dialogue1 : cacheSet) {
                    if(dialogue1.getToId().equals(toId)){
                        // 说明存在
                        throw new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"会话已开启");
                    }
                }
                // 不存在，开启会话
                redisCache.addCacheSet("dialogue:"+userDO.getId(),dialogue);
                log.info("dialogue:"+userDO.getId()+"to"+toId+"已存入redis");
            }

            // 存入数据库中
            Dialogue dialogue = new Dialogue(userDO.getId().toString(), toId,null, null);
            // 由于新通话，所以不需要给最后的信息
            dialogueDao.insert(dialogue);
            log.info("dialogue"+"已存入数据库");
            return new Result<>(Result.PROJECT_SUCCESS,"success");
        }else if (StringUtils.hasText(groupId)){
            // 说明是组
            // 检查是否已经创建会话
            // 从redis中查找有无会话消息
            Set<Dialogue> cacheSet = redisCache.getCacheSet("dialogue:group:" + userDO.getId());
            if(cacheSet.isEmpty()){
                // 说明没有总会话，那创建一个即可
                cacheSet = new HashSet<>();
                Dialogue dialogue = new Dialogue(userDO.getId().toString(),null,groupId,null);
                cacheSet.add(dialogue);
                redisCache.setCacheSet("dialogue:group:"+userDO.getId(),cacheSet);
                log.info("dialogue:group:"+userDO.getId()+"已存入redis");
            }else {
                // 存在总会话，那就开始查询是否存在会话
                Dialogue dialogue = new Dialogue(userDO.getId().toString(),null,groupId,null);
                for (Dialogue dialogue1 : cacheSet) {
                    if(dialogue1.getGroupId().equals(toId)){
                        // 说明存在
                        throw new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"会话已开启");
                    }
                }
                // 不存在，开启会话
                cacheSet.add(dialogue);
                redisCache.setCacheSet("dialogue:group:"+userDO.getId(),cacheSet);
                log.info("dialogue:group:"+userDO.getId()+"to"+toId+"已存入redis");
            }

            // 存入数据库中
            Dialogue dialogue = new Dialogue(userDO.getId().toString(), null,groupId, null);
            // 由于新通话，所以不需要给最后的信息
            dialogueDao.insert(dialogue);
            log.info("dialogue:group:"+"已存入数据库");
            return new Result<>(Result.PROJECT_SUCCESS,"success");
        }else {
            throw new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"传入参数非法");
        }
    }
}
