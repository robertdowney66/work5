package com.yuyu.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.yuyu.dao.CommentDao;
import com.yuyu.pojo.DO.Comment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
/**
 * 用于service层中所需功能的工具类
 */
public class ServiceUtils {

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private RedisCache redisCache;


    public void cleanChildComment(Long commentId){
        LambdaQueryWrapper<Comment> lam = new LambdaQueryWrapper<>();
        lam.eq(Comment::getParentId,commentId);
        List<Comment> comments = commentDao.selectList(lam);
        if (comments.isEmpty()){

            // 删除redis中的相关字段,减少视频浏览量等
            Comment comment = commentDao.selectById(commentId);
            redisCache.delCacheMapValue("comment_like_num",commentId.toString());
            redisCache.delCacheMapValue("comment_child_num",commentId.toString());
            redisCache.decreMapNum("video_comment_num",comment.getVideoId().toString());
            log.info("redis数据更改成功");

            // 那么只需要删除这一条评论即可
            commentDao.deleteById(commentId);
            log.info("删除评论"+commentId);
        }else {
            // 说明还有子评论，继续递归,但是要先删除该评论
            Comment comment1 = commentDao.selectById(commentId);
            commentDao.deleteById(commentId);
            for (Comment comment : comments) {

                // 删除redis中的相关字段,减少视频浏览量等
                redisCache.delCacheMapValue("comment_like_num",commentId.toString());
                redisCache.delCacheMapValue("comment_child_num",commentId.toString());
                redisCache.decreMapNum("video_comment_num",comment1.getCommentId().toString());
                log.info("redis数据更改成功");

                // 让子评论继续递归,删除子评论的子评论
                cleanChildComment(comment.getCommentId());
            }
        }
    }
}
