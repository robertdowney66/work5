package com.yuyu.scheduled;

import com.yuyu.dao.CommentDao;
import com.yuyu.dao.UserDao;
import com.yuyu.dao.VideoDao;
import com.yuyu.pojo.DO.Comment;
import com.yuyu.pojo.DO.Video;
import com.yuyu.utils.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
/**
 * 用于实现项目中所需的定时功能
 */
public class TimeScheduled {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private UserDao userDao;

    @Autowired
    private VideoDao videoDao;

    @Autowired
    private CommentDao commentDao;

    @Scheduled(cron = " 0 10/10 * * * ?   ")
    /**
     * 实现定时将redis中的数量数据存入数据库中
     */
    public void updateVisitCount() throws SQLException {


        // 更新视频访问量
        Set<DefaultTypedTuple<String>> clickNum = redisCache.sortNum("click_num");
        for (DefaultTypedTuple<String> stringDefaultTypedTuple : clickNum) {
            String value = stringDefaultTypedTuple.getValue();
            Double score = stringDefaultTypedTuple.getScore();
            Video video = videoDao.selectById(value);
            video.setUpdatedAt(LocalDateTime.now());

            // 设置访问量
            video.setVisitCount(score.longValue());
            videoDao.updateById(video);
            log.info("数据:"+value+"访问量已更新至数据库中");
        }

        // 更新视频点赞量
        Map<String, Integer> videoLikeNum = redisCache.getCacheMap("video_like_num");
        Set<Map.Entry<String, Integer>> entries = videoLikeNum.entrySet();
        for (Map.Entry<String, Integer> entry : entries) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            Video video = videoDao.selectById(key);
            video.setUpdatedAt(LocalDateTime.now());

            // 设置点赞量
            video.setLikeCount(value.longValue());
            videoDao.updateById(video);
            log.info("数据:"+key+"点赞量已更新至数据库中");
        }

        // 更新视频评论量
        Map<String, Integer> videoCommentNum = redisCache.getCacheMap("video_comment_num");
        Set<Map.Entry<String, Integer>> entries2 = videoLikeNum.entrySet();
        for (Map.Entry<String, Integer> entry : entries2) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            Video video = videoDao.selectById(key);
            video.setUpdatedAt(LocalDateTime.now());

            // 设置点赞量
            video.setCommentCount(value.longValue());
            videoDao.updateById(video);
            log.info("数据:"+key+"评论量已更新至数据库中");
        }

        // 更新评论子评论数量
        Map<String, Integer> videoCommentNum1 = redisCache.getCacheMap("comment_child_num");
        Set<Map.Entry<String, Integer>> entries3 = videoCommentNum1.entrySet();
        for (Map.Entry<String, Integer> entry : entries3) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            Comment comment = commentDao.selectById(key);
            comment.setUpdatedAt(LocalDateTime.now());

            // 设置点赞量
            comment.setChildCount(value.longValue());
            commentDao.updateById(comment);
            log.info("评论:"+key+"子评论量已更新至数据库中");
        }

        // 更新评论点赞数量
        Map<String, Integer> videoCommentNum2 = redisCache.getCacheMap("comment_like_num");
        Set<Map.Entry<String, Integer>> entries4 = videoCommentNum2.entrySet();
        for (Map.Entry<String, Integer> entry : entries4) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            Comment comment = commentDao.selectById(key);
            comment.setUpdatedAt(LocalDateTime.now());

            // 设置点赞量
            comment.setLikeCount(value.longValue());
            commentDao.updateById(comment);
            log.info("评论:"+key+"点赞数量已更新至数据库中");
        }




    }




}
