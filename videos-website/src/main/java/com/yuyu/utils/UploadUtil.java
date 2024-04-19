package com.yuyu.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;
import java.util.UUID;

@Slf4j
/**
 * 上传文件或者视频的工具类
 */
public class UploadUtil {
    /**
     * 该字段为域名
     */
    public static final String ALI_DOMAIN = "https://robertdowney.oss-cn-fuzhou.aliyuncs.com/";

    public static String uploadImageByBytes(String s,String fromId)throws IOException{
        // 先进行校验
        if (!s.contains("base64")){
            // 说明不是图片消息
            return null;
        }

        byte[] bytes = null;
        String type = judge(s);
        if(!StringUtils.hasText(type)){
            return null;
        }
        try {
            // 将标识字段替换
            String s1 = s.replaceAll("data:image/" + type + ";base64,", "");
            bytes = Base64.getDecoder().decode(s1);
            log.info("成功解码");
        } catch (Exception e) {
            // 说明不是二进制数组，直接return
            e.printStackTrace();
            return null;
        }

        log.info("开始存储");
        // 现在准备存储工作

        InputStream inputStream = new ByteArrayInputStream(bytes);
        String uuid = UUID.randomUUID().toString().replace("-","");
        String fileName = uuid + fromId;

        // 地域节点
        String endpoint = "http://oss-cn-fuzhou.aliyuncs.com";
        String accessKeyId = "****";
        String accessKeySecret = "****";
        // OSS客户端对象
        OSS ossclient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
        ossclient.putObject(
                //仓库名
                "robertdowney",
                //文件名
                fileName,
                inputStream
        );
        log.info(fileName+"存入oss中");
        ossclient.shutdown();
        return ALI_DOMAIN + fileName;
    }

    public static String judge(String s){
        //判断图片base64字符串的文件格式
            String type = "";
            if (s.contains("png")){
                return "png";
            }else if(s.contains("jpeg")){
                return "jpeg";
            }
            return type;
    }

    public static String uploadImage(MultipartFile file) throws IOException {
        // 获取文件原始名
        String originalFilename = file.getOriginalFilename();
        String ext = "." + FilenameUtils.getExtension(originalFilename);
        // 定义一个文件唯一标识码（UUID）
        String uuid = UUID.randomUUID().toString().replace("-","");
        String fileName = uuid + ext;

        // 地域节点
        String endpoint = "http://oss-cn-fuzhou.aliyuncs.com";
        String accessKeyId = "****";
        String accessKeySecret = "****";
        // OSS客户端对象
        OSS ossclient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
        ossclient.putObject(
                //仓库名
                "robertdowney",
                //文件名
                fileName,
                file.getInputStream()
        );
        ossclient.shutdown();
        return ALI_DOMAIN + fileName;
    }

}
