package com.yuyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuyu.dao.CommentDao;
import com.yuyu.dao.LikeDao;
import com.yuyu.dao.UserDao;
import com.yuyu.dao.VideoDao;
import com.yuyu.exception.BussinessException;
import com.yuyu.exception.Code;
import com.yuyu.pojo.DO.Comment;
import com.yuyu.pojo.DO.Like;
import com.yuyu.pojo.DO.User;
import com.yuyu.pojo.DO.Video;
import com.yuyu.pojo.LoginUser;
import com.yuyu.pojo.Result;
import com.yuyu.service.InteractService;
import com.yuyu.utils.RedisCache;
import com.yuyu.utils.ServiceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
/**
 * 实现了InteractService中的接口方法
 */
public class InteractServiceImpl implements InteractService {
    /**
     * 这四个常量用于页码参数为空时的默认设置和点赞操作的类型
     */
    private static final Integer PAGE_NUM=0;
    private static final Integer PAGE_SIZE=5;
    private static final String LIKE_ACTION_TYPE="1";
    private static final String UNLIKE_ACTION_TYPE="2";

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private LikeDao likeDao;

    @Autowired
    private VideoDao videoDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ServiceUtils serviceUtils;

    @Autowired
    private RedisCache redisCache;

    @Override
    public Result commentList(Long videoId, Long commentId, Integer pageNum, Integer pageSize) {
        if (Objects.isNull(pageNum)){
            pageNum=PAGE_NUM;
        }
        if (Objects.isNull(pageSize)){
            pageSize=PAGE_SIZE;
        }
        if (Objects.isNull(videoId)&&Objects.isNull(commentId)){
            log.error("未传输id");
            throw new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"请传入视频或者评论id");
        }

        IPage page = new Page(pageNum,pageSize);
        LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = new LambdaQueryWrapper<>();

        if (Objects.isNull(videoId)){
            // 说明是评论的
            commentLambdaQueryWrapper.eq(Comment::getParentId,commentId);
            IPage commentIPage = commentDao.selectPage(page, commentLambdaQueryWrapper);
            log.info("已从数据库中获取comment数据");

            List<Comment> records = commentIPage.getRecords();
            HashMap<String, List> stringArrayListHashMap = new HashMap<>(6);
            stringArrayListHashMap.put("items",records);
            return new Result<>(Result.PROJECT_SUCCESS,"success",stringArrayListHashMap);
        }else{
            // 说明是视频的,查询时将为parent_id为0的剔除
            commentLambdaQueryWrapper.eq(Comment::getVideoId,videoId).eq(Comment::getParentId, 0L);
            IPage<Comment> commentIPage = commentDao.selectPage(page, commentLambdaQueryWrapper);
            log.info("已从数据库中获取有关video的comment数据");

            List<Comment> records = commentIPage.getRecords();
            HashMap<String, List> stringArrayListHashMap = new HashMap<>(6);
            stringArrayListHashMap.put("items",records);
            return new Result<>(Result.PROJECT_SUCCESS,"success",stringArrayListHashMap);
        }
    }

    @Override
    @Transactional
    /**
     * 涉及到删除多个数据，所以配置了事务，防止出现问题
     */
    public Result commentDelete(Long videoId, Long commentId) {
        if (Objects.isNull(videoId)&&Objects.isNull(commentId)){
            log.error("未传输id");
            throw new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"请传入视频或者评论id");
        }

        if (Objects.isNull(videoId)){
            // 说明是评论的，删除评论时，要先删除子评论
            // 调用工具类中递归方式，将子评论和子评论的各级别子评论删除
            serviceUtils.cleanChildComment(commentId);
            // 已经删除干净，直接返回即可
            return new Result<>(Result.PROJECT_SUCCESS,"success");
        }else{
            // 说明是视频的评论，删除逻辑同上
            LambdaQueryWrapper<Comment> lam = new LambdaQueryWrapper<>();
            lam.eq(Comment::getVideoId,videoId).eq(Comment::getParentId,0);
            List<Comment> comments = commentDao.selectList(lam);
            if (comments.isEmpty()){
                // 说明该视频没有评论，直接返回即可
                return new Result(Result.PROJECT_SUCCESS,"success");
            }else {
                // 说明含有评论，调用递归方法
                for (Comment comment : comments) {
                    serviceUtils.cleanChildComment(comment.getCommentId());
                }
                return new Result(Result.PROJECT_SUCCESS,"success");
            }
        }
    }

    @Override
    public Result action(Long videoId, Long commentId, String type) {
        if (Objects.isNull(videoId)&&Objects.isNull(commentId)){
            log.error("未传输id");
            throw new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"请传入视频或者评论id");
        }

        if ((!type.equals(LIKE_ACTION_TYPE)&&(!type.equals(UNLIKE_ACTION_TYPE)))){
            log.error("点赞类型参数有误");
            throw new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"点赞操作非法,请检查");
        }

        LoginUser principal = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userDO = principal.getUserDO();

        if (Objects.isNull(videoId)){
            // 说明目标为评论

            if (type.equals(LIKE_ACTION_TYPE)){
                // 说明为点赞,将redis点赞数加一

                // 检测是否已经处于点赞状态
                LambdaQueryWrapper<Like> likeLambdaQueryWrapper = new LambdaQueryWrapper<>();
                likeLambdaQueryWrapper.eq(Like::getUserId,userDO.getId()).eq(Like::getCommentId,commentId);
                Like like1 = likeDao.selectOne(likeLambdaQueryWrapper);
                if (!Objects.isNull(like1)){
                    return new Result<>(Result.PROJECT_SUCCESS, "该评论已点赞");
                }

                redisCache.increMapNum("comment_like_num",commentId.toString());
                log.info("视频:"+videoId+":点赞数已加一");

                Like like = new Like();
                like.setUserId(userDO.getId());
                like.setCommentId(commentId);
                int insert = likeDao.insert(like);
                if (insert==0){
                    log.error("系统繁忙，请稍后再试");
                    throw new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"系统繁忙，请稍后再试");
                }else {
                    log.info("like已存入数据库");
                    return new Result<>(Result.PROJECT_SUCCESS, "success");
                }
            }else {
                // 说明为取消点赞，将redis点赞数减一

                // 检测是否已经处于取消点赞状态
                LambdaQueryWrapper<Like> likeLambdaQueryWrapper = new LambdaQueryWrapper<>();
                likeLambdaQueryWrapper.eq(Like::getUserId,userDO.getId()).eq(Like::getCommentId,commentId);
                Like like1 = likeDao.selectOne(likeLambdaQueryWrapper);
                if (Objects.isNull(like1)){
                    return new Result<>(Result.PROJECT_SUCCESS, "该评论已取消点赞");
                }

                redisCache.decreMapNum("comment_like_num",commentId.toString());
                log.info("视频:"+videoId+":点赞数已减一");

                LambdaQueryWrapper<Like> lam = new LambdaQueryWrapper<>();
                lam.eq(Like::getUserId, userDO.getId());
                lam.eq(Like::getCommentId,commentId);
                int delete = likeDao.delete(lam);
                if (delete==0){
                    log.error("系统繁忙，请稍后再试");
                    throw new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"系统繁忙，请稍后再试");
                }else{
                    log.info("like已从数据库删除");
                    return new Result(Result.PROJECT_SUCCESS, "success");
                }
            }
        }else {
            // 说明为视频

            if (type.equals(LIKE_ACTION_TYPE)){

                // 检测是否已经处于点赞状态
                LambdaQueryWrapper<Like> likeLambdaQueryWrapper = new LambdaQueryWrapper<>();
                likeLambdaQueryWrapper.eq(Like::getUserId,userDO.getId()).eq(Like::getVideoId,videoId);
                Like like1 = likeDao.selectOne(likeLambdaQueryWrapper);
                if (!Objects.isNull(like1)){
                    return new Result<>(Result.PROJECT_SUCCESS, "该评论已点赞");
                }

                // 说明为点赞,将redis点赞数加一
                redisCache.increMapNum("video_like_num",videoId.toString());
                log.info("视频:"+videoId+":点赞数已加一");

                // 将like数据存入数据库
                Like like = new Like();
                like.setUserId(userDO.getId());
                like.setVideoId(videoId);
                int insert = likeDao.insert(like);
                if (insert==0){
                    log.error("系统繁忙，请稍后再试");
                    throw new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"系统繁忙，请稍后再试");
                }else{
                    log.info("like:"+userDO.getId()+","+videoId+"已加入数据库");
                    return new Result<>(Result.PROJECT_SUCCESS,"success");
                }
            }else {
                // 说明为取消点赞

                // 检测是否已经处于取消点赞状态
                LambdaQueryWrapper<Like> likeLambdaQueryWrapper = new LambdaQueryWrapper<>();
                likeLambdaQueryWrapper.eq(Like::getUserId,userDO.getId()).eq(Like::getVideoId,videoId);
                Like like1 = likeDao.selectOne(likeLambdaQueryWrapper);
                if (Objects.isNull(like1)){
                    return new Result<>(Result.PROJECT_SUCCESS, "该评论已取消点赞");
                }

                redisCache.decreMapNum("video_like_num",videoId.toString());
                log.info("视频:"+videoId+":点赞数已减一");

                // 将like数据删除
                LambdaQueryWrapper<Like> lam = new LambdaQueryWrapper<>();
                lam.eq(Like::getUserId, userDO.getId());
                lam.eq(Like::getVideoId,videoId);
                int delete = likeDao.delete(lam);
                if (delete==0){
                    log.error("系统繁忙，请稍后再试");
                    throw new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"系统繁忙，请稍后再试");
                }else {
                    log.info("like:"+userDO.getId()+","+videoId+"已从数据库删除");
                    return new Result(Result.PROJECT_SUCCESS, "success");
                }
            }
        }
    }

    @Override
    public Result likeList(Long userid, Integer pageNum, Integer pageSize) {

        // 如果没有页码大小就设置默认的
        if (Objects.isNull(pageNum)){
            pageNum=PAGE_NUM;
        }
        if(Objects.isNull(pageSize)){
            pageSize=PAGE_SIZE;
        }

        // 没有userid，默认查自己的
        if (Objects.isNull(userid)){
            LoginUser principal = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User userDO = principal.getUserDO();
            userid=userDO.getId();
        }

        // 检测userid是否存在
        User user = userDao.selectById(userid);
        if (Objects.isNull(user)){
            log.error("用户不在在于数据库中");
            throw new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"用户不存在");
        }

        IPage<Like> page = new Page<>(pageNum,pageSize);

        LambdaQueryWrapper<Like> lam = new LambdaQueryWrapper<>();
        lam.eq(Like::getUserId,userid);
        IPage<Like> page1 = likeDao.selectPage(page, lam);
        log.info("成功查出like数据");

        List<Like> records = page1.getRecords();
        ArrayList<Video> videos = new ArrayList<>();
        for (Like record : records) {
            Video video = videoDao.selectById(record.getVideoId());
            videos.add(video);
        }
        HashMap<String, List> map = new HashMap<>(6);
        map.put("items",videos);

        return new Result(Result.PROJECT_SUCCESS,"success",map);
    }

    @Override
    public Result publish(Long videoId, Long commentId, String content) {
        LoginUser principal = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userDO = principal.getUserDO();

        if (Objects.isNull(videoId)){
            // 说明为子评论，增加父评论的子评论数量
            redisCache.increMapNum("comment_child_num",commentId.toString());
            log.info("评论:"+commentId+":子评论数已加一");

            // 需要将video的评论数增加,要使用commentId来寻求videoId
            Comment comment = commentDao.selectById(commentId);
            Long videoId1 = comment.getVideoId();
            log.info("数据查询成功");

            redisCache.increMapNum("video_comment_num",videoId1.toString());
            log.info("视频:"+comment.getVideoId()+":评论数已加一");

            Comment newComment = new Comment();
            newComment.setVideoId(comment.getVideoId());
            newComment.setUserId(userDO.getId());
            newComment.setParentId(commentId);
            // 下面是为了解决存入数据库四舍五入问题导致无法查询

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String format = dateTimeFormatter.format(now);
            newComment.setCreatedAt(LocalDateTime.parse(format,dateTimeFormatter));

            newComment.setContent(content);
            int insert = commentDao.insert(newComment);
            if (insert==0){
                log.error("系统繁忙，请稍后再试");
                throw new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"系统繁忙，请稍后再试");
            }else {
                log.info("评论已存入数据库");
            }

            // reids中初始化评论点赞量和评论子评论量,首先得获取评论id
            LambdaQueryWrapper<Comment> lam = new LambdaQueryWrapper<>();
            lam.eq(Comment::getUserId,userDO.getId()).eq(Comment::getCreatedAt,format);
            Comment comment1 = commentDao.selectOne(lam);

            redisCache.redisTemplate.execute(new SessionCallback() {
                @Override
                public Object execute(RedisOperations operations) throws DataAccessException {

                    operations.multi();
                    // 初始化子评论量
                    HashMap<String, Integer> stringLongHashMap = new HashMap<>(6);
                    stringLongHashMap.put(comment1.getCommentId().toString(),0);
                    redisCache.setCacheMap("comment_child_num",stringLongHashMap);
                    log.info("视频:"+comment1.getCommentId()+":子评论量已设置");

                    // 初始化点赞量
                    HashMap<String, Integer> stringLongHashMap1 = new HashMap<>(6);
                    stringLongHashMap.put(comment1.getCommentId().toString(),0);
                    redisCache.setCacheMap("comment_like_num",stringLongHashMap);
                    log.info("视频:"+comment1.getCommentId()+":点赞量已设置");
                    return redisCache.redisTemplate.exec();
                }
            });


            return new Result(10000,"success");
        }else{
            // 说明为父评论，增加视频的评论数
            redisCache.increMapNum("video_comment_num",videoId.toString());
            log.info("视频:"+videoId+":评论数已加一");

            // 将comment存入数据库
            Comment newComment = new Comment();
            newComment.setUserId(userDO.getId());
            newComment.setVideoId(videoId);
            // 下面是为了解决存入数据库四舍五入问题导致无法查询

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String format = dateTimeFormatter.format(now);
            newComment.setCreatedAt(LocalDateTime.parse(format,dateTimeFormatter));

            // 将Parentid设置为0，方便查询
            newComment.setParentId(0L);
            newComment.setContent(content);
            int insert = commentDao.insert(newComment);
            if (insert==0){
                log.error("系统繁忙，请稍后再试");
                throw new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"系统繁忙，请稍后再试");
            }else{
                log.info("评论已存入数据库");
            }

            // reids中初始化评论点赞量和评论子评论量,首先得获取评论id
            LambdaQueryWrapper<Comment> lam = new LambdaQueryWrapper<>();
            lam.eq(Comment::getUserId,userDO.getId()).eq(Comment::getCreatedAt,format);
            Comment comment1 = commentDao.selectOne(lam);

            redisCache.redisTemplate.execute(new SessionCallback() {
                @Override
                public Object execute(RedisOperations operations) throws DataAccessException {

                    operations.multi();
                    // 子评论设置
                    HashMap<String, Integer> stringLongHashMap = new HashMap<>(6);
                    stringLongHashMap.put(comment1.getCommentId().toString(),0);
                    redisCache.setCacheMap("comment_child_num",stringLongHashMap);
                    log.info("视频:"+comment1.getCommentId()+":子评论量已设置");

                    // 点赞量设置
                    HashMap<String, Integer> stringLongHashMap1 = new HashMap<>();
                    stringLongHashMap.put(comment1.getCommentId().toString(),0);
                    redisCache.setCacheMap("comment_like_num",stringLongHashMap);
                    log.info("视频:"+comment1.getCommentId()+":点赞量已设置");
                    return redisCache.redisTemplate.exec();
                }
            });


            return new Result(Result.PROJECT_SUCCESS,"success");
        }
    }
}
