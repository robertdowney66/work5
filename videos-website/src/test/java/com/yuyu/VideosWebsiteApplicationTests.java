package com.yuyu;

import com.yuyu.pojo.DO.Dialogue;
import com.yuyu.utils.RedisCache;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;


@SpringBootTest(classes = VideosWebsiteApplication.class)
class VideosWebsiteApplicationTests {
    @Autowired
    RedisCache redisCache;

    @Test
    public void test(){
        RedisCache redisCache1 = new RedisCache();
        redisCache.setCacheSet("hhh",new HashSet<Dialogue>());
//        redisCache.addCacheSet("hhh",new Dialogue(null,null,"123",null));
    }




}
