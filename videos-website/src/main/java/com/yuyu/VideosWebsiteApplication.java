package com.yuyu;

import com.github.jeffreyning.mybatisplus.conf.EnableMPP;
import com.yuyu.config.WebsocketConfig;
import com.yuyu.ws.ChatEndPoint;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.yuyu.dao")
@EnableMPP
@EnableScheduling
@EnableTransactionManagement
public class VideosWebsiteApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(VideosWebsiteApplication.class);
        ConfigurableApplicationContext run = SpringApplication.run(VideosWebsiteApplication.class, args);
        // 使得websocket能够调用自动装配的类
        WebsocketConfig.setApplicationContext(run);
        ChatEndPoint.setApplicationContext(run);
    }

}
