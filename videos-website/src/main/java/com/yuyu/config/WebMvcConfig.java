package com.yuyu.config;

import com.yuyu.utils.JacksonObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
/**
 * 配置了springmvc的相关配置
 */
public class WebMvcConfig implements WebMvcConfigurer {
    @Value("${files.upload.location}")
    private String fileLocation;

    @Value("${files.upload.path}")
    private String filePath;

    @Value("${files.upload.location-video}")
    private String fileLocationVideo;

    @Value("${files.upload.path-video}")
    private String filePathVideo;


    /**
     * 使用自定义转换器
     * @param converters
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        // 设置对象转换器
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        // 添加到mvc框架消息转换器中，优先使用自定义转换器
        converters.add(0, messageConverter);


    }

    /**
     * 将本地路径配置到映射路径上
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 注册配置类,使用addResourceHandlers方法，将本地路径fileLocation映射到filePath路由上
        // file:不敢漏了
        registry.addResourceHandler(filePath).addResourceLocations("file:"+fileLocation);
        registry.addResourceHandler(filePathVideo).addResourceLocations("file:"+fileLocationVideo);
        WebMvcConfigurer.super.addResourceHandlers(registry);
    }
}
