package com.yuyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuyu.dao.MenuDao;
import com.yuyu.dao.UserDao;
import com.yuyu.exception.BussinessException;
import com.yuyu.exception.Code;
import com.yuyu.pojo.LoginUser;
import com.yuyu.pojo.DO.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 用于用户登录所需的校验
 */
@Service
@Slf4j
public class UserDetialsSeviceImpl implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private MenuDao menuDao;


    /**
     * 用于获取userDetails对象
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 查询用户信息
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getUserName,username);
        User userDO = userDao.selectOne(userLambdaQueryWrapper);

        // 查看是否是封禁用户
        User userDeletedByName = userDao.getUserDeletedByName(username);
        if(!Objects.isNull(userDeletedByName)){
            throw new BussinessException(Code.PROJECT_BUSSINESS_ERROR,"您的账号已经被封禁");
        }
        // 如果没有查询到用户，就抛出异常
        if (Objects.isNull(userDO)){
            throw new RuntimeException("用户名或者密码错误");
        }


        // TODO 查询对应的权限信息
        List<String> list = menuDao.getPermsByRoleId(userDO.getRid());
        return new LoginUser(userDO,list);
    }
}
