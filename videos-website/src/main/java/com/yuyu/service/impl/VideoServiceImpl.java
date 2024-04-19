package com.yuyu.service.impl;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuyu.dao.UserDao;
import com.yuyu.dao.VideoDao;
import com.yuyu.exception.BussinessException;
import com.yuyu.exception.Code;
import com.yuyu.pojo.LoginUser;
import com.yuyu.pojo.Result;
import com.yuyu.pojo.DO.User;
import com.yuyu.pojo.DO.Video;
import com.yuyu.service.VideoService;
import com.yuyu.utils.RedisCache;
import com.yuyu.utils.UploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
@Slf4j

/**
 * 实现VideoService中的接口方法
 */
public class VideoServiceImpl implements VideoService {
    /**
     * 这俩个常数用于页码参数为空时的默认设置
     */
    private static final Integer PAGE_NUM=0;
    private static final Integer PAGE_SIZE=5;

    @Autowired
    private RedisCache redisCache;

    @Value("${files.upload.location-video}")
    private String fileUploadLocation;

    @Autowired
    private VideoDao videoDao;

    @Autowired
    private UserDao userDao;

    @Override
    public Result publish(MultipartFile file,String title,String description) throws IOException {
        // 获取文件原始名
        String originalFilename = file.getOriginalFilename();
        // 获取文件类型
        String type = FileUtil.extName(originalFilename);
        log.info("文件类型是："+type);



        String url = UploadUtil.uploadImage(file);
        // 获取该用户,获得用户id
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userDO = loginUser.getUserDO();

        Video video = new Video();
        video.setVideoUrl(url);
        video.setTitle(title);
        video.setDescription(description);
        video.setUserId(userDO.getId());
        video.setCreatedAt(LocalDateTime.now());
        int insert = videoDao.insert(video);
        if (insert==0){
            log.error("系统繁忙，请稍后再试");
            throw new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"系统繁忙，请稍后再试");
        }else{
            log.info("video成功传入数据库");
        }

        // 获取video传入数据库后获得的id,用于redis标记
        LambdaQueryWrapper<Video> videoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        videoLambdaQueryWrapper.eq(Video::getVideoUrl,url);
        Video video1 = videoDao.selectOne(videoLambdaQueryWrapper);

        redisCache.redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {

                operations.multi();
                // 同时在redis使用zset初始化该视频的浏览量
                redisCache.setCacheZset("click_num", video1.getId(),0.0);
                log.info("视频:"+video1.getId()+"点击量已存入redis");

                // 同时在reids使用map初始化该视频的点赞量
                HashMap<String, Integer> map= new HashMap<>(6);
                map.put(video1.getId().toString(),0);
                redisCache.setCacheMap("video_like_num",map);
                log.info("视频:"+video1.getId()+"点赞量已存入redis");

                // 评论量
                HashMap<String, Integer> map1 = new HashMap<>(6);
                map.put(video1.getId().toString(),0);
                redisCache.setCacheMap("video_comment_num",map);
                log.info("视频:"+video1.getId()+"评论量已存入redis");
                return redisCache.redisTemplate.exec();
            }
        });



        return new Result<>(Result.PROJECT_SUCCESS,"success");
    }


    @Override
    public Result click(Long id) {
        // TODO 完善热搜排行榜
        LoginUser principal = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userid = principal.getUserDO().getId();

        // 查看该用户是否第一次点入该视频,通过context获取该用户

        if (redisCache.getCacheSet("video:"+id+":view_members").contains(userid)){
            // 如果为true，说明该用户已经访问过该视频，不计入
            return new Result(Result.PROJECT_SUCCESS,"success");
        }else {
            // false向redis中用户set集合中加入该用户
            HashSet<Long> longs = new HashSet<>();
            longs.add(userid);
            redisCache.setCacheSet("video:"+id+":view_members",longs);
            log.info("用户:"+userid+"已存入视频:"+id+"浏览集合中");
        }
        // 通过id定位redis中的浏览量,让浏览量加1
        redisCache.increZsetNum("click_num",id.toString());
        log.info("video:"+id+":浏览量已经加一");

        return new Result(Result.PROJECT_SUCCESS,"sussess");
    }

    @Override
    public Result popular(Integer pageNum, Integer pageSize) {
        // 如果分页参数为空，设定默认参数
        if (Objects.isNull(pageNum)){
            pageNum=PAGE_NUM;
        }
        if (Objects.isNull(pageSize)){
            pageSize=PAGE_SIZE;
        }
        // 获取redis中所有视频浏览量排行榜
        Set<DefaultTypedTuple<String>> clickNum = redisCache.sortNum("click_num");
        // 遍历所有,寻找对应的video
        Long videoNum = 0L;
        ArrayList<Video> videos = new ArrayList<>();
        for (DefaultTypedTuple<String> defaultTypedTuple : clickNum) {
            videoNum++;
            if (videoNum>pageNum*pageSize&&videoNum<=(pageNum*pageSize)+pageSize){
                String value = defaultTypedTuple.getValue();

                Long l = Long.valueOf(value);
                Video video = videoDao.selectById(l);
                log.info("video:"+l+":已从数据库查出");
                videos.add(video);
            }
        }
        HashMap<String, List> map = new HashMap<>();
        map.put("items",videos);
        return new Result<>(Result.PROJECT_SUCCESS,"success",map);
    }

    @Override
    public Result list(Long userId, Integer pageNum, Integer pageSize) {
        // 如果分页参数为空，设定默认参数
        if (Objects.isNull(pageNum)){
            pageNum=PAGE_NUM;
        }
        if (Objects.isNull(pageSize)){
            pageSize=PAGE_SIZE;
        }

        LambdaQueryWrapper<Video> lam = new LambdaQueryWrapper<Video>();
        lam.eq(Video::getUserId,userId);
        IPage page = new Page(pageNum,pageSize);
        IPage page1 = videoDao.selectPage(page, lam);
        List records = page1.getRecords();
        log.info("已从数据库成功查出数据");

        Map map = new HashMap<>();
        map.put("items",records);
        map.put("total",page1.getTotal());
        return new Result(Result.PROJECT_SUCCESS,"success",map);
    }

    @Override
    public Result search(String keywords, Integer pageSize, Integer pageNum, Long fromDate, Long toDate, String userName) {

        // 如果分页参数为空，设定默认参数
        if (Objects.isNull(pageNum)){
            pageNum=PAGE_NUM;
        }
        if (Objects.isNull(pageSize)){
            pageSize=PAGE_SIZE;
        }

        // 验证有无关键字
        LambdaQueryWrapper<Video> lam = new LambdaQueryWrapper<>();
        if(!Objects.isNull(keywords)){
            lam.like(Video::getTitle,keywords);
        }
        IPage page = new Page(pageNum,pageSize);

        // 验证有无起始时间
        if(!Objects.isNull(fromDate)){
            LocalDateTime localDateTime = Instant.ofEpochMilli(fromDate).atZone(ZoneOffset.systemDefault()).toLocalDateTime();
            lam.gt(Video::getCreatedAt,localDateTime);
        }

        // 验证有无终止时间
        if(!Objects.isNull(toDate)){
            LocalDateTime localDateTime = Instant.ofEpochMilli(toDate).atZone(ZoneOffset.systemDefault()).toLocalDateTime();
            lam.lt(Video::getCreatedAt,localDateTime);
        }

        // 验证有无包含用户
        if(!Objects.isNull(userName)){
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getUserName,userName);
            User user = userDao.selectOne(userLambdaQueryWrapper);
            if (Objects.isNull(user)){
                Map map = new HashMap<>();
                map.put("items",null);
                map.put("total",0);
                return new Result(Result.PROJECT_SUCCESS,"success",map);
            }
            lam.eq(Video::getUserId,user.getId());
        }

        IPage page1 = videoDao.selectPage(page, lam);
        List records = page1.getRecords();
        log.info("成功从数据库查询数据");
        // 将搜索记录存入redis

        LoginUser principal = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userDO = principal.getUserDO();
        HashMap<String, Object> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("keywords",keywords);
        stringStringHashMap.put("from_date",fromDate);
        stringStringHashMap.put("to_date",toDate);
        stringStringHashMap.put("user_name",userName);
        stringStringHashMap.put("search_date",LocalDateTime.now());
        redisCache.setCacheMap("video_history:"+userDO.getId(),stringStringHashMap);

        Map map = new HashMap<>();
        map.put("items",records);
        map.put("total",page1.getTotal());
        return new Result(Result.PROJECT_SUCCESS,"success",map);
    }


}
