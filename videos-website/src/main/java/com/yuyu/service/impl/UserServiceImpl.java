package com.yuyu.service.impl;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuyu.dao.UserDao;
import com.yuyu.exception.BussinessException;
import com.yuyu.exception.Code;
import com.yuyu.pojo.LoginUser;
import com.yuyu.pojo.Result;
import com.yuyu.pojo.DO.User;
import com.yuyu.service.UserService;
import com.yuyu.utils.JwtUtil;
import com.yuyu.utils.RedisCache;
import com.yuyu.utils.UploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Objects;

@Service
@Slf4j
/**
 * 实现UserService中的接口方法，实现user相关业务层操作
 */
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisCache redisCache;

    @Value("${files.upload.location}")
    private String fileUploadLocation;

    @Override
    public Result login(User user) {
        // AuthenticationManger authenticate 进行用户认证
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        // 如果认证没通过，给出对应的提示
        if(Objects.isNull(authenticate)){
            log.info("authenticate为空");
            throw new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"用户信息有误，请检查");
        }

        // 如果认证通过了，使用userid生成一个jwt jwt存入Result返回
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userid = loginUser.getUserDO().getId().toString();
        String jwt = JwtUtil.createJWT(userid);
        HashMap map = new HashMap<>(6);
        map.put("token",jwt);
        map.put("user",loginUser.getUserDO());

        // 把完整的用户信息存入redis userid作为key
        redisCache.setCacheObject("login:"+userid,loginUser);
        log.info("用户:"+userid+":已存入redis");

        return new Result<>(Result.PROJECT_SUCCESS,"success",map);
    }

    @Override
    public Result logout() {
        // 获取SecurityContextHolder中的用户id
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long id = loginUser.getUserDO().getId();

        // 删除redis中的值
        redisCache.deleteObject("login:"+id);
        log.info("用户:"+id+":已从redis删除");

        return new Result<>(Result.PROJECT_SUCCESS,"success");
    }

    @Override
    public Result register(User userDO) {
        // 检查用户名是否存在
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<User>();
        lambdaQueryWrapper.eq(User::getUserName,userDO.getUserName());
        User user1 = userDao.selectOne(lambdaQueryWrapper);

        // 用户名已存在
        if(user1!=null){
            throw new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"用户已存在，请重新输入用户名");
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode(userDO.getPassword());
        userDO.setPassword(encode);
        LocalDateTime now = LocalDateTime.now();
        userDO.setCreatedAt(now);
        int insert = userDao.insert(userDO);
        if (insert==0){
            log.error("系统繁忙，请稍后再试");
            throw new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"系统繁忙，请稍后再试");
        }else{
            // 用户已存入数据库
            log.info("用户:"+userDO.getId()+":已存入数据库");
        }


        return new Result<>(Result.PROJECT_SUCCESS,"success");
    }

    @Override
    public Result info(Long id) {

        LoginUser principal = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user1 = principal.getUserDO();
        User user = userDao.selectById(id);
        log.info("已从数据库中查出数据");
        if (Objects.isNull(user)){
            return new Result(Result.PROJECT_SUCCESS,"success",user);
        }

        // 将搜索记录保存至reids中
        HashMap<String, String> stringLongHashMap = new HashMap<>(6);
        stringLongHashMap.put("from_id:"+user.getId(),"to_id:"+id);
        redisCache.setCacheMap("user_history",stringLongHashMap);
        log.info("已将收缩记录存入reids中");

        return new Result<>(Result.PROJECT_SUCCESS,"success",user);
    }

    @Override
    public Result upload(MultipartFile file) throws IOException {
        // 获取文件原始名
        String originalFilename = file.getOriginalFilename();
        // 获取文件类型
        String type = FileUtil.extName(originalFilename);
        log.info("文件类型是："+type);
        if("jpg".equals(type)||"jepg".equals(type)||"png".equals(type)){
            String url = UploadUtil.uploadImage(file);

            // 获取该用户
            LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User userDO = loginUser.getUserDO();
            userDO.setAvatarUrl(url);
            LocalDateTime localDateTime = LocalDateTime.now();
            userDO.setUpdatedAt(localDateTime);
            userDao.updateById(userDO);
            log.info("用户:"+userDO.getId()+":已存入数据库");

            loginUser.setUserDO(userDO);
            // 使用新的loginUser替换redis中旧的loginUser,以便于下次访问时候，更新SecurityContextHolder中信息
            redisCache.setCacheObject("login:"+userDO.getId(),loginUser);
            log.info("用户:"+userDO.getId()+"信息已更新至redis中");

            return new Result<>(Result.PROJECT_SUCCESS,"success",userDO);
        }else {
            throw new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"文件类型错误，请上传符合类型的文件");
        }


    }

    @Override
    public Result updateNickname(String nickName) {

        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userDO = loginUser.getUserDO();
        userDO.setNickName(nickName);
        LocalDateTime localDateTime = LocalDateTime.now();
        userDO.setUpdatedAt(localDateTime);
        int i = userDao.updateById(userDO);
        if (i==0){
            log.error("系统繁忙，请稍后再试");
            throw new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"系统繁忙，请稍后再试");
        }else{
            log.info("数据库数据已更改");
        }

        loginUser.setUserDO(userDO);
        // 使用新的loginUser替换redis中旧的loginUser,以便于下次访问时候，更新SecurityContextHolder中信息
        redisCache.setCacheObject("login:"+userDO.getId(),loginUser);
        log.info("reids数据已更改");
        return new Result(Result.PROJECT_SUCCESS,"success",userDO);
    }

    @Override
    public Result block(Long id) {
        User user = userDao.selectById(id);
        if(Objects.isNull(user)){
            throw  new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"传入id用户不存在");
        }

        int i = userDao.deleteById(user);
        log.info("用户"+id+"数据已修改");
        if(i==0){
            throw new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"操作失败，请稍后重试");
        }else {
            return new Result(Result.PROJECT_SUCCESS,"success");
        }
    }

    @Override
    public Result unblock(Long id) {
        User user = userDao.getUserDeletedById(id);
        if(Objects.isNull(user)){
            throw new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"该用户未被封禁");
        }

        int i = userDao.updateUserDeleteById(id);
        log.info("用户"+id+"数据已修改");
        if(i==0){
            throw new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"操作失败，请稍后重试");
        }else {

            return new Result(Result.PROJECT_SUCCESS,"success");
        }
    }


}
