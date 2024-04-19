package com.yuyu.service;

import com.yuyu.pojo.Result;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 定义了video业务层所需的接口方法
 */
public interface VideoService {
    /**
     * 实现视频发布功能
     * @param file
     * @param title
     * @param description
     * @return 操作结果
     * @throws IOException
     */
    Result publish(MultipartFile file,String title,String description) throws IOException;

    /**
     * 实现发布列表查询功能
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return 操作结果
     */
    Result list(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 实现搜索功能
     * @param keywords
     * @param pageSize
     * @param pageNum
     * @param fromDate
     * @param toDate
     * @param userName
     * @return 操作结果
     */
    Result search(String keywords, Integer pageSize, Integer pageNum, Long fromDate, Long toDate, String userName);

    /**
     * 实现视频点击功能
     * @param id
     * @return 操作结果
     */
    Result click(Long id);

    /**
     * 实现视频热度排行榜功能
     * @param pageNum
     * @param pageSize
     * @return 操作结果
     */
    Result popular(Integer pageNum, Integer pageSize);
}
