package com.yuyu.filter;

import com.yuyu.pojo.LoginUser;
import com.yuyu.utils.JwtUtil;
import com.yuyu.utils.RedisCache;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
/**
 * 用于登录验证获取和解析token等操作
 */
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private RedisCache redisCache;

    /**
     * 进行具体token验证的操作
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 判断是http还是ws
        String connection = request.getHeader("Connection");
        if("Upgrade".equals(connection)){
            // 就说明是ws协议了
            log.info(connection);
            log.info("ws");

            // 获取Token
            String token = request.getHeader("Sec-WebSocket-Protocol");

            if(!StringUtils.hasText(token)){
                throw new RuntimeException("请传递token");
            }
            // 解析Token
            String userid;
            try {
                Claims claims = JwtUtil.parseJWT(token);
                userid = claims.getSubject();
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new RuntimeException("token非法");
            }

            // 从redis中获取用户信息
            String rediskey = "login:" + userid;
            LoginUser loginUser = redisCache.getCacheObject(rediskey);
            log.info(loginUser.getUsername()+"存在");

            if(Objects.isNull(loginUser)){
                throw new RuntimeException("用户未登录");
            }

            // 存入SecurityConTextHolder
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(loginUser,null,loginUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            filterChain.doFilter(request,response);
        }else {
            // 以下是http的校验工作
            log.info(connection);
            log.info("http");
            // 获取Token
            String token = request.getHeader("token");
            if(!StringUtils.hasText(token)){
                // 放行
                filterChain.doFilter(request,response);
                return;
            }

            // 解析Token
            String userid;
            try {
                Claims claims = JwtUtil.parseJWT(token);
                userid = claims.getSubject();
            } catch (Exception e) {

                e.printStackTrace();
                throw new RuntimeException("token非法");
            }

            // 从redis中获取用户信息
            String rediskey = "login:" + userid;
            LoginUser loginUser = redisCache.getCacheObject(rediskey);

            if(Objects.isNull(loginUser)){
                throw new RuntimeException("用户未登录");
            }

            // 存入SecurityConTextHolder
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(loginUser,null,loginUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

            filterChain.doFilter(request,response);
        }

    }
}
