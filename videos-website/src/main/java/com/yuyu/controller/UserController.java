package com.yuyu.controller;

import com.yuyu.pojo.Result;
import com.yuyu.pojo.DO.User;
import com.yuyu.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/user")
@Slf4j
/**
 * 实现用户相关工作的展示层操作
 */
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 接受登录时参数，传输给service层
     * @param userName 用户账号
     * @param password 用户密码
     * @return 操作结果
     */
    @PostMapping("/login")
    public Result login(@RequestParam("username") String userName,@RequestParam("password") String password){
        User userDO = new User(userName,password);
        return userService.login(userDO);
    }

    /**
     * 接受注销时参数，传输给service层
     * @return 操作结果
     */
    @PostMapping("/logout")
    @PreAuthorize("hasAnyAuthority('normal:logout')")
    public Result logout(){
        return userService.logout();
    }

    /**
     * 接受注册时参数，传输给service层
     * @param userName 用户账号
     * @param password 密码密码
     * @return 操作结果
     */
    @PostMapping("/register")
    public Result register(@RequestParam("username")String userName,@RequestParam("password") String password){
        User userDO = new User(userName, password);
        return userService.register(userDO);
    }

    /**
     * 接受查询指定用户时参数，传输给service层
     * @param id 用户id
     * @return 操作结果
     */
    @GetMapping("/info")
    @PreAuthorize("hasAnyAuthority('normal:info')")
    public Result info(@RequestParam("user_id") Long id){
        return userService.info(id);
    }

    /**
     * 接受更新头像时参数，传输给service层
     * @param file 上传文件 应该为jpg/jepg/png
     * @return 操作结果
     * @throws IOException 操作结果
     */
    @PutMapping("/avatar/upload")
    @PreAuthorize("hasAnyAuthority('normal:upload:avatar')")
    public Result upload(@RequestParam MultipartFile file) throws IOException {
        return userService.upload(file);
    }

    /**
     * 接受修改昵称时的参数，传输给service层
     * @param nickName 用户昵称
     * @return 操作结果
     */
    @PutMapping("/nickname")
    @PreAuthorize("hasAnyAuthority('normal:update:nickname')")
    public Result updateNickname(@RequestParam("nickname") String nickName){
        return userService.updateNickname(nickName);
    }

    /**
     * 进行封禁用户的操作
     * @param id 用户id
     * @return 操作结果
     */
    @PostMapping("/block")
    @PreAuthorize("hasAnyAuthority('control:block:user')")
    public Result block(@RequestParam("user_id") Long id){
        return userService.block(id);
    }
    /**
     * 进行解封用户的操作
     * @param id 用户id
     * @return 操作结果
     */
    @PostMapping("/unblock")
    @PreAuthorize("hasAnyAuthority('control:unblock:user')")
    public Result unblock(@RequestParam("user_id") Long id){
        return userService.unblock(id);
    }




}
