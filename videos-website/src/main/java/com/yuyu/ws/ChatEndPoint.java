package com.yuyu.ws;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuyu.config.WebsocketConfig;
import com.yuyu.dao.*;
import com.yuyu.exception.BussinessException;
import com.yuyu.exception.Code;
import com.yuyu.pojo.DO.*;
import com.yuyu.utils.MessageUtils;
import com.yuyu.utils.RedisCache;
import com.yuyu.utils.UploadUtil;
import com.yuyu.ws.pojo.Message;
import com.yuyu.ws.pojo.ResultMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/chat",configurator = WebsocketConfig.class)
@Component
@Slf4j
public class ChatEndPoint {
    /**
     * 创建一个静态的集合用于存储所有在线session
     */
    private static final Map<String, Session> ONLINE_USERS = new ConcurrentHashMap<>();

    /**
     * 由于websocket没办法自动装配，所以需要该类
     */
    private static ApplicationContext applicationContext;

    /**
     * 防止注入下列类时反复调用initial
     */
    private static boolean flag = true;

    private static RedisCache redisCache;

    private static UserDao userDao;

    private static IgnoreDao ignoreDao;

    private static UserFanDao userFanDao;

    private static GroupUserDao groupUserDao;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        ChatEndPoint.applicationContext = applicationContext;
    }

    /**
     * 便于调用该次session
     */
    private Session session;

    /**
     * 由于此类无法注入，所以只能手动初始化
     */
    public void initial(){
        if(flag){
            redisCache = applicationContext.getBean(RedisCache.class);
            userDao = applicationContext.getBean(UserDao.class);
            ignoreDao = applicationContext.getBean(IgnoreDao.class);
            userFanDao = applicationContext.getBean(UserFanDao.class);
            groupUserDao = applicationContext.getBean(GroupUserDao.class);
            flag = false;
        }

    }

    /**
     * 建立websocket连接后，被调用
     * @param session 本实例对应的会话
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config){
        log.info("已连接");
        // 优化工具类初始化
        initial();

        // 保存session
        this.session = session;
        String id = session.getUserProperties().get("id").toString();
        log.info("用户:"+id+"已创建连接");

        // 将session存储到map集合中
        ONLINE_USERS.put(id,session);
        UserDao userDao = applicationContext.getBean(UserDao.class);
        User user = userDao.selectById(id);

        // 从数据库查出他的屏蔽人，存入redis中
        Set<String> badMan = getBadMan(id);
        badManInRedis(id,badMan);

        // 上线时将未读信息发送给该用户
        getUnreadMessage();

        // 广播消息，将用户登录推送给除了自己外，所有好友
        String message = MessageUtils.getMessage(true,null,user.getNickName()+"已上线",null);
        broadcastAllUsers(message);

        log.info("信息已发送");
    }

    /**
     * 建立websocket连接后，发消息调用
     * @param message 发送的信息或者参数
     */
    @OnMessage(maxMessageSize = 500000)
    public void onMessage(String message) {
        log.info("进入onMessage");
        String fromId = session.getUserProperties().get("id").toString();

        // 解析信息
        Message msg = JSON.parseObject(message,Message.class);
        // 获取 消息接收方的名称
        String type = msg.getType();
        String toName = msg.getToName();
        String mess = msg.getMessage();

        if("HeartBeat".equals(mess)){
            // 说明为心跳，返回即可
            sendHeartBeatToOne();
            log.info("已返回心跳数据");
            return;
        }else if("ReturnHeartBeat".equals(mess)){
            // 说明为返回心跳
            return ;
        }

        // 检查发送的是否是图片二进制
        try {
            String s = changeImage(mess);
            if (StringUtils.hasText(s)){
                // 转化为url
                mess = s;
            }
        } catch (IOException e) {
            // 由于错误信息调用方法时已经打印，这里不做处理了
        }

        if("1".equals(type)){
            sendToOne(toName,mess,1,null);
        }else if ("2".equals(type)){
            sendToGroup(toName,mess);
        }
    }

    /**
     * 创建会话
     * @param toName 要开启会话的人或者群组
     * @param mess 消息
     * @param type 是人还是群组
     */
    public void createDialogue(String toName, String mess,int type){
        String id = session.getUserProperties().get("id").toString();
        if(type==1){
            // 说明是人
            // 从redis中查找有无会话消息
            Set<Dialogue> cacheSet = redisCache.getCacheSet("dialogue:" + id);
            if(cacheSet.isEmpty()){
                // 说明没有会话，那创建一个即可
                cacheSet = new HashSet<Dialogue>();
                Dialogue dialogue = new Dialogue(id, toName,null,mess);
                cacheSet.add(dialogue);
                redisCache.setCacheSet("dialogue:"+id,cacheSet);
                log.info("dialogue:"+id+"已存入redis");
            }else {
                // 说明已经含有会话消息
                // 查看该人是否存在其中
                Dialogue dialogue = new Dialogue(id, toName,null,mess);
                for (Dialogue dialogue1 : cacheSet) {
                    // 查找会话id一致的
                    if (dialogue1.getToId().equals(toName)){
                        // 说明已经存在，那么只需要更改最新消息
                        redisCache.replaceCacheSet("dialogue:"+id,dialogue,dialogue1);
                        log.info("dialogue:"+id+"已存入redis,且旧信息已经修改");
                        return;
                    }
                }
                // 说明不存在，加入原来set
                redisCache.addCacheSet("dialogue:"+id,dialogue);
                log.info("dialogue:"+id+"已存入redis");
            }
        }else {
            // 说明是组
            // 从redis中查找有无会话消息
            Set<Dialogue> cacheSet = redisCache.getCacheSet("dialogue:group:" + id);
            if(cacheSet.isEmpty()){
                // 说明没有会话，那创建一个即可
                cacheSet = new HashSet<>();
                Dialogue dialogue = new Dialogue(id, null,toName,mess);
                cacheSet.add(dialogue);
                redisCache.setCacheSet("dialogue:group:"+id,cacheSet);
                log.info("dialogue:group:"+id+"已存入redis");
            }else {
                // 说明已经含有会话消息
                // 查看该组是否存在其中
                Dialogue dialogue = new Dialogue(id, null,toName,mess);
                for (Dialogue dialogue1 : cacheSet) {
                    // 查找会话id一致的
                    if (dialogue1.getGroupId().equals(toName)){
                        // 说明已经存在，那么只需要更改最新消息
                        redisCache.replaceCacheSet("dialogue:group:"+id,dialogue,dialogue1);
                        log.info("dialogue:group:"+id+"已存入redis,且旧信息已经修改");
                        return;
                    }
                }
                // 说明不存在，加入原来set
                redisCache.addCacheSet("dialogue:group:"+id,dialogue);
                log.info("dialogue:group:"+id+"已存入redis");
            }
        }

    }
    /**
     * 获取未读信息
     */
    public void getUnreadMessage() {
        // 先从redis中查找一波
        String toName = session.getUserProperties().get("id").toString();
        List<String> unreadMessages= redisCache.getCacheList("unread:" + toName);
        if(unreadMessages.isEmpty()){
            // 说明是空的，说明没有消息
            log.info(toName+"的未读消息为空");
            return ;
        }else {
            try {
                for (int i = 1; i <= unreadMessages.size(); i++) {
                    // 直接发送即可
                    session.getBasicRemote().sendText(unreadMessages.get(i));
                    log.info("消息发送成功");

                    // 发消息后要进行会话的建立
                    // 先进行消息的解析
                    ResultMessage resultMessage = JSON.parseObject(unreadMessages.get(i), ResultMessage.class);
                    if (resultMessage.getGroupId()!=null){
                        createDialogue(resultMessage.getGroupId(),resultMessage.getMessage().toString(),2);
                    }else {
                        createDialogue(resultMessage.getFromName(),resultMessage.getMessage().toString(),1);
                    }
                    unreadMessages.remove(i);
                }
            } catch (IOException e) {
                log.error(e.getMessage());
                // 将没处理的继续存入redis
                redisCache.deleteObject("unread"+toName);
                redisCache.setCacheList("unread"+toName,unreadMessages);
            }
        }
        log.info("未读消息处理完毕");
        // 发送完就删除redis缓存
        redisCache.deleteObject("unread:"+toName);
    }

    /**
     * 存屏蔽名单
     * @param id 发起屏蔽的人的id
     * @param badMan 屏蔽人的集合
     */
    public void badManInRedis(String id,Set<String> badMan){
        redisCache.setCacheSet("badMan:"+id,badMan);
        log.info("badMan:"+id+" 已经存入redis");
    }

    /**
     * 取屏蔽名单
     * @param id 发起屏蔽的人的id
     * @return 屏蔽人的集合
     */
    public Set<String> badManOutRedis(String id){
        return redisCache.getCacheSet("badMan:"+id);
    }

    /**
     * 获取屏蔽人，从mysql调取是为了防止redis中数据存在偏差，不能及时反馈，所以创立连接时候核对一遍
     * @return 屏蔽人id的集合
     */
    public Set<String> getBadMan(String userId){
        LambdaQueryWrapper<Ignore> ignoreLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ignoreLambdaQueryWrapper.eq(Ignore::getUserId,userId);
        List<Ignore> ignores = ignoreDao.selectList(ignoreLambdaQueryWrapper);
        log.info("已从mysql已查出屏蔽人名单");

        Set<String> badMans = new HashSet<>();
        for (Ignore ignore : ignores){
            badMans.add(ignore.getIgnoreId());
        }
        return badMans;
    }

    /**
     * 获取全部的好友的session
     * @return 好友的集合
     */
    public Set<Session> getFriends(){
        Set<Session> friends = new HashSet<>();
        // 获取全部的好友
        String id = session.getUserProperties().get("id").toString();

        // 获取此用户的粉丝表
        LambdaQueryWrapper<UserFan> lam = new LambdaQueryWrapper<>();
        lam.eq(UserFan::getUserId,id);

        List<UserFan> userFans = userFanDao.selectList(lam);
        log.info("已从mysql查出userFan数据");

        // 查看粉丝表中用户也被该用户关注的
        for (UserFan userFan : userFans) {
            LambdaQueryWrapper<UserFan> lam1 = new LambdaQueryWrapper<>();
            lam1.eq(UserFan::getUserId,userFan.getFanId());
            lam1.eq(UserFan::getFanId,userFan.getUserId());
            UserFan fanUser = userFanDao.selectOne(lam1);
            log.info("已从mysql查出好友数据");
            if(!Objects.isNull(fanUser)){
                // 说明二者互为好友
                // 查看是否在线
                Session session1 = ONLINE_USERS.get(userFan.getFanId().toString());
                if(!Objects.isNull(session1)){
                    friends.add(session1);
                    log.info("好友"+userFan.getFanId()+"在线");
                }
            }
        }
        return friends;
    }

    /**
     * 向所有session发送心跳
     * @param message 发送信息
     */
    public static void sendHeartBeat(String message){
        Collection<Session> values = ONLINE_USERS.values();
        for (Session session1 : values) {
            try {
                session1.getBasicRemote().sendText(message);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        log.info("已向所有客户端发送数据");
    }

    /**
     * 向某个session回复心跳
     */
    public void sendHeartBeatToOne(){
        ResultMessage resultMessage = new ResultMessage(null,true,null,"ReturnHeartBeat");
        String jsonString = JSON.toJSONString(resultMessage);
        try {
            session.getBasicRemote().sendText(jsonString);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    /**
     * 给全部好友发送在线信息
     * @param message 发送信息的内容
     */
    public void broadcastAllUsers(String message) {
        Set<Session> friends = getFriends();
        for (Session friend : friends) {
            try {
                String myId = session.getUserProperties().get("id").toString();
                String friendId = friend.getUserProperties().get("id").toString();
                // 发送消息,剔除已经屏蔽的人
                Set<String> badMans = badManOutRedis(myId);
                if(!badMans.contains(friendId)){
                    // 发送消息,剔除把自己屏蔽的人
                    Set<String> beBadMans = badManOutRedis(friendId);
                    if(!beBadMans.contains(myId)){
                        friend.getBasicRemote().sendText(message);
                        log.info("已向好友发送信息");
                    }else {
                        log.info("已被好友屏蔽");
                    }
                }else {
                    log.info("好友已被屏蔽");
                }

            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    /**
     * 保存用户发送的历史
     * @param type 1-是与单人间，2-是组之间
     * @param toName 发送的组或者人的id
     * @param mess 信息
     */
    public void saveHistory(int type,String toName,String mess){
        // 拿到发送者id
        String fromId = session.getUserProperties().get("id").toString();
        if (type==1){
            // 说明单人
            // 先保存到redis
            History history = new History();
            history.setFromId(fromId);
            history.setToId(toName);
            history.setMessage(mess);
            history.setCreateAt(LocalDateTime.now());
            // 先看看俩个人是否已经有了历史
            List<Object> list1 = redisCache.getCacheList("history:" + fromId + " to " + toName);
            if(list1.isEmpty()){
                // 说明俩个人没有聊天记录，创建就好了
                list1 = new ArrayList<>();
                list1.add(history);
                redisCache.setCacheList("history:" + fromId + " to " + toName,list1);
                log.info(fromId+" to "+toName+"已创建并存入redis");
            }else {
                // 俩个人已经有了
                redisCache.addCacheList("history:" + fromId + " to " + toName,history);
                log.info(fromId+":"+toName+"已存入redis");
            }
        }else if (type==2){
            // 说明多人
            // 先保存到redis
            History history = new History();
            history.setFromId(fromId);
            history.setGroupId(toName);
            history.setMessage(mess);
            history.setCreateAt(LocalDateTime.now());
            // 先看看组里是否有历史
            List<Object> list1 = redisCache.getCacheList("history:"+toName);
            if(list1.isEmpty()){
                // 说明俩个人没有聊天记录，创建就好了
                list1 = new ArrayList<>();
                list1.add(history);
                redisCache.setCacheList("history:" + toName,list1);
                log.info(fromId+":group"+":"+toName+"已创建并存入redis");
            }else {
                // 俩个人已经有了
                redisCache.addCacheList("history:" + toName,history);
                log.info(fromId+":group"+":"+toName+"已存入redis");
            }
        }
    }

    /**
     * 向单个用户发信息
     * @param toName 用户id
     * @param mess 信息
     * @param type 是否需要保留历史记录 1-需要 2-不需要
     */
    public void sendToOne(String toName,String mess,int type,String groupId){
        // 说明是单发
        // 判断是否被对方屏蔽
        Set<String> cacheSet = redisCache.getCacheSet("badMan:" + toName);
        if (cacheSet.isEmpty()){
            LambdaQueryWrapper<Ignore> ignoreLambdaQueryWrapper = new LambdaQueryWrapper<>();
            ignoreLambdaQueryWrapper.eq(Ignore::getUserId,toName);
            List<Ignore> ignores = ignoreDao.selectList(ignoreLambdaQueryWrapper);
            String fromId1 = session.getUserProperties().get("id").toString();
            if(ignores.contains(fromId1)){
                // 说明已经被屏蔽了，不需要操作了
                log.info(fromId1+":已经被"+toName+"屏蔽了");
                return ;
            }
        }else {
            // 说明有数据，获取发送者的名字
            String fromId1 = session.getUserProperties().get("id").toString();
            if(cacheSet.contains(fromId1)){
                // 说明已经被屏蔽了，不需要操作了
                log.info(fromId1+":已经被"+toName+"屏蔽了");
                return ;
            }
        }
        Session toSession = ONLINE_USERS.get(toName);
        // 判断是否在线
        if(Objects.isNull(toSession)){
            // 说明离线
            // 检查是否存在该用户
            UserDao userDao = applicationContext.getBean(UserDao.class);
            User user = userDao.selectById(toName);
            if(Objects.isNull(user)){
                // 用户不存在
                log.error("用户不存在");
                throw new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"用户不存在");
            }

            // 将消息存入未读信息中,待用户上线，直接传递给用户
            // 判断redis中有没有未读消息
            List<String> unreadMessage = redisCache.getCacheList("unread:" + user.getId().toString());
            if(unreadMessage.isEmpty()){
                // 那就创建一个
                String fromId2 = session.getUserProperties().get("id").toString();
                ResultMessage resultMessage;
                if (type==2){
                    // 说明是组内消息，带上组id
                    resultMessage = new ResultMessage(fromId2, false, groupId,mess);
                }else {
                    resultMessage = new ResultMessage(fromId2, false, null,mess);
                }
                String jsonString = JSON.toJSONString(resultMessage);

                // 创建一个新的
                unreadMessage = new ArrayList<>();
                unreadMessage.add(jsonString);
                redisCache.setCacheList("unread:"+user.getId().toString(),unreadMessage);
                log.info("未读消息存入redis");
                if(type==1){
                    // 此时加入历史消息，后面上线接收时候就不加了
                    saveHistory(1,toName,mess);
                }
            }else {
                // 说明存在,那就加入就好
                String fromId1 = session.getUserProperties().get("id").toString();
                ResultMessage resultMessage;
                if (type==2){
                    // 说明是组内消息，带上组id
                    resultMessage = new ResultMessage(fromId1, false, groupId,mess);
                }else {
                    resultMessage = new ResultMessage(fromId1, false, null,mess);
                }
                String jsonString = JSON.toJSONString(resultMessage);
                redisCache.addCacheList("unread:"+user.getId().toString(),jsonString);
                log.info("未读消息存入redis");
                if(type==1){
                    // 此时加入历史消息，后面上线接收时候就不加了
                    saveHistory(1,toName,mess);
                }
            }
        }else {
            // 说明在线,获取发送方id
            String id = session.getUserProperties().get("id").toString();
            String msg1;
            if (type==2){
                // 说明是组内消息，带上组id
                msg1 = MessageUtils.getMessage(false, id, mess,groupId);
            }else {
                msg1 = MessageUtils.getMessage(false, id, mess,null);
            }

            try {
                toSession.getBasicRemote().sendText(msg1);
                if(type==1){
                    log.info("检验会话");
                    // 此时加入历史消息，后面上线接收时候就不加了
                    saveHistory(1,toName,mess);
                    // 同时开启与对方对话(如果不存在对话)
                    Set<Dialogue> cacheSet1 = redisCache.getCacheSet("dialogue:" + toName);
                    if(cacheSet1.isEmpty()){
                        //创建并加入
                        cacheSet1 = new HashSet<>();
                        Dialogue dialogue = new Dialogue(toName,id,null,mess);
                        cacheSet1.add(dialogue);
                        redisCache.setCacheSet("dialogue:"+ toName, cacheSet1);
                        log.info("dialogue:"+ toName+"已创建");
                    }else {
                        // 存在就查找是否已有对话
                        for (Dialogue dialogue : cacheSet1) {
                            if (dialogue.getToId().equals(id)){
                                // 说明已经存在，只需更换信息
                                Dialogue dialogue1 = new Dialogue();
                                dialogue1.setMessage(mess);
                                redisCache.replaceCacheSet("dialogue:"+ toName,dialogue1,dialogue);
                                log.info("dialogue:"+ toName+"已修改");
                                return;
                            }
                        }
                        // 不存在就加入
                        Dialogue dialogue = new Dialogue(toName,id,null,mess);
                        redisCache.addCacheSet("dialogue:"+ toName,dialogue);
                        log.info("dialogue:"+ toName+"已加入");
                    }
                }else if(type==2){
                    log.info("开始检验对话");
                    // 检测是否开启群聊对话
                    Set<Dialogue> cacheSet1 = redisCache.getCacheSet("dialogue:group:" + toName);
                    if(cacheSet1.isEmpty()){
                        //创建并加入
                        cacheSet1 = new HashSet<>();
                        Dialogue dialogue = new Dialogue(toName,null,groupId,mess);
                        cacheSet1.add(dialogue);
                        redisCache.setCacheSet("dialogue:group:"+ toName, cacheSet1);
                        log.info("dialogue:group:"+ toName+"已开启");
                    }else {
                        // 存在就查找是否已有对话
                        for (Dialogue dialogue : cacheSet1) {
                            if (dialogue.getGroupId().equals(groupId)){
                                // 说明已经存在，只需更换信息
                                Dialogue dialogue1 = new Dialogue();
                                dialogue1 = dialogue;
                                dialogue1.setMessage(mess);
                                redisCache.replaceCacheSet("dialogue:group:"+ toName,dialogue1,dialogue);
                                log.info("dialogue:group:"+ toName+"已修改");
                                return;
                            }
                        }
                        // 不存在就加入
                        Dialogue dialogue = new Dialogue(toName,null,groupId,mess);
                        redisCache.addCacheSet("dialogue:group:"+ toName,dialogue);
                        log.info("dialogue:group:"+ toName+"已加入");
                    }
                }

            } catch (IOException e) {
                log.error(e.getMessage());
            }
            log.info("mess消息已发送");
        }
    }

    /**
     * 向组中所有成员发信息
     * @param toName 组名
     * @param mess 信息
     */
    public void sendToGroup(String toName,String mess){
        // 用于标记是否存入
        boolean flag = true;
        String fromId = session.getUserProperties().get("id").toString();
        // 说明是群发
        // 先从redis中看看有没群组信息
        List<GroupUser> cacheList = redisCache.getCacheList("group:" + toName);
        if (cacheList.isEmpty()){
            // 从数据库取
            // 把组的成员查出来
            LambdaQueryWrapper<GroupUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(GroupUser::getGroupId,toName);
            List<GroupUser> groupUsers = groupUserDao.selectList(lambdaQueryWrapper);
            log.info("group:"+toName+"已查出");
            if (groupUsers.isEmpty()){
                throw new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"组号存在错误");
            }else {
                // 发送信息,调用sendToOne
                for (GroupUser groupUser : groupUsers) {
                    if (!groupUser.getMemberId().equals(fromId.toString())){
                        sendToOne(groupUser.getMemberId(),mess,2,toName);
                        if (flag){
                            // 记录群组的历史记录
                            saveHistory(2,toName,mess);
                            flag=false;
                        }
                    }
                }
                // 将数据存入redis
                redisCache.setCacheList("group:"+toName,groupUsers);
                log.info("gruop:"+toName+"已存入redis");
            }

        }else {
            // 说明redis中有
            for (GroupUser groupUser : cacheList) {
                // 发送信息,调用sendToOne
                if (!groupUser.getMemberId().equals(fromId.toString())){
                    sendToOne(groupUser.getMemberId(),mess,2,toName);
                    if (flag){
                        // 记录群组的历史记录
                        saveHistory(2,toName,mess);
                        flag=false;
                    }
                }
            }
        }
    }

    /**
     * 将二进制解码成图片
     * @param s 二进制码
     * @return 返回的url
     * @throws IOException 抛出的异常
     */
    public String changeImage(String s) throws IOException {
        String fromId = session.getUserProperties().get("id").toString();
        log.info("进来了");
        // 获取图片的url
        String url = UploadUtil.uploadImageByBytes(s,fromId);
        if(StringUtils.hasText(url)){
            // 说明已经存入，返回url即可
            log.info("返回url");
            return url;
        }else {
            // 说明没存入，返回空string即可
            return null;
        }
    }

    /**
     * 断开 websocket 连接时被调用
     * @param session 本会话的session
     */
    @OnClose
    public void onClose(Session session){
        log.info(session.toString());
//         1.从onLineUsers 中剔除当前用户的session对象
        String user = session.getUserProperties().get("id").toString();
        ONLINE_USERS.remove(user);
//         2.通知其他所有的用户，当前用户下线了
        String message = MessageUtils.getMessage(true,null,user+"下线了",null);
        // 向群组，或者好友发送
        broadcastAllUsers(message);
        log.info("会话关闭");
    }

    @OnError
    public void onError(Session session,Throwable error){
        log.error("发生错误");
    }
}
