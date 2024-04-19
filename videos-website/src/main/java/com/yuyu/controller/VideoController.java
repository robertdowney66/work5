package com.yuyu.controller;

import com.yuyu.pojo.Result;
import com.yuyu.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/video")
/**
 * 实现video展示层操作，接受参数，传递给service层
 */
public class VideoController {

    @Autowired
    private VideoService videoService;

    /**
     * 接收视频发布时参数，传递给service层
     * @param data 上传视频
     * @param title 视频标题
     * @param description 视频描述
     * @return 操作结果
     * @throws IOException
     */
    @PostMapping("/publish")
    @PreAuthorize("hasAnyAuthority('normal:video:publish')")
    public Result publish(@RequestParam MultipartFile data,String title,String description) throws IOException {
        return videoService.publish(data,title,description);
    }

    /**
     * 接收接受视频点击时参数，传递给service层
     * @param id 视频id
     * @return 操作结果
     */
    @PostMapping("/click")
    @PreAuthorize("hasAnyAuthority('normal:video:click')")
    public Result click(@RequestParam("video_id") Long id){
        return videoService.click(id);
    }

    /**
     * 用于接受热度排行所需参数，传递给service层
     * @param pageSize 页面数据条数
     * @param pageNum 页面页码
     * @return 操作结果
     */
    @GetMapping("/popular")
    @PreAuthorize("hasAnyAuthority('normal:video:popular')")
    public Result popular(@RequestParam(value = "page_size",required = false) Integer pageSize,
                          @RequestParam(value = "page_num",required = false) Integer pageNum){
        return videoService.popular(pageNum,pageSize);

    }


    /**
     * 用于接收查询视频列表所需参数，传递给servcie层
     * @param userId 用户id
     * @param pageNum 页面页码
     * @param pageSize 页面数据条数
     * @return 操作结果
     */
    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('normal:video:list')")
    public Result list(@RequestParam("user_id") Long userId,
                       @RequestParam(value = "page_num",required = false) Integer pageNum,
                       @RequestParam(value = "page_size",required = false) Integer pageSize){

        return videoService.list(userId,pageNum,pageSize);
    }

    /**
     * 用于接收搜索时所需参数，传递给service层
     * @param keywords 关键字
     * @param pageSize 页面数据条数
     * @param pageNum 页面页码
     * @param fromDate 开始日期
     * @param toDate 截止日期
     * @param userName 用户名字
     * @return 操作结果
     */
    @PostMapping("/search")
    @PreAuthorize("hasAnyAuthority('normal:video:search')")
    public Result list(@RequestParam(required = false) String keywords,@RequestParam(value = "page_size",required = false) Integer pageSize,
                       @RequestParam(value = "page_num",required = false) Integer pageNum,
                       @RequestParam(value = "from_date",required = false) Long fromDate,
                       @RequestParam(value = "to_date",required = false) Long toDate,
                       @RequestParam(value = "username",required = false) String userName){
        return videoService.search(keywords,pageSize,pageNum,fromDate,toDate,userName);

    }


}
