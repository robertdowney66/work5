package com.yuyu.service;

import com.yuyu.pojo.Result;
import com.yuyu.pojo.DO.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 定义user相关业务层操作的接口方法
 */
public interface UserService {

    /**
     * 实现登录的操作
     * @param userDO
     * @return 操作结果
     */
    Result login(User userDO);

    /**
     * 实现注销的操作
     * @return 操作结果
     */
    Result logout();

    /**
     * 实现注册的操作
     * @param userDO
     * @return 操作结果
     */
    Result register(User userDO);

    /**
     * 实现查询用户的操作
     * @param id
     * @return 操作结果
     */
    Result info(Long id);

    /**
     * 实现上传头像的操作
     * @param file
     * @return 操作结果
     * @throws IOException
     */
    Result upload(MultipartFile file) throws IOException;

    /**
     * 实现上传昵称的操作
     * @param nickName
     * @return 操作结果
     */
    Result updateNickname(String nickName);

    /**
     * 进行用户封禁操作
     * @param id
     * @return
     */
    Result block(Long id);

    /**
     * 进行用户解封操作
     * @param id
     * @return
     */
    Result unblock(Long id);
}
