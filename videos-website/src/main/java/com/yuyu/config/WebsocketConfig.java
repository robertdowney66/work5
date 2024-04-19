package com.yuyu.config;

import com.yuyu.pojo.LoginUser;
import com.yuyu.utils.JwtUtil;
import com.yuyu.utils.RedisCache;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import java.util.List;
import java.util.Map;

@Configuration
@Slf4j
public class WebsocketConfig extends ServerEndpointConfig.Configurator {

    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        WebsocketConfig.applicationContext = applicationContext;
    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter (){
        return new ServerEndpointExporter();
    }

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        //这个userProperties,通过sec.getUserProperties() 获取
        final Map<String, Object> userProperties = sec.getUserProperties();
        // 获取token
        Map<String, List<String>> headers = request.getHeaders();
        List<String> protocol = headers.get("Sec-WebSocket-Protocol");
        String token = "";
        // 这里已经接收过验证，所以只需要存储就可以了

        token = protocol.get(0);

        // 解析Token
        String userid;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userid = claims.getSubject();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("token非法");
        }

        RedisCache redisCache = (RedisCache) applicationContext.getBean(RedisCache.class);
        // 从redis中获取用户信息
        String rediskey = "login:" + userid;
        LoginUser loginUser = redisCache.getCacheObject(rediskey);
        log.info(loginUser.getUsername()+"存在");
        sec.getUserProperties().put("id",loginUser.getUserDO().getId());

    }

    @Override
    public <T> T getEndpointInstance(Class<T> clazz) throws InstantiationException {
        return super.getEndpointInstance(clazz);
    }
}
