package com.yuyu.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Order(1)
@Component

@Slf4j
public class WebsocketFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 将请求体中的protocol传递给响应体
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String token = ((HttpServletRequest) servletRequest).getHeader("Sec-WebSocket-Protocol");

        if(StringUtils.hasText(token)){
            response.setHeader("Sec-WebSocket-Protocol",token);
            filterChain.doFilter(servletRequest, servletResponse);
            log.info("我已经加上了");
        }else {
            filterChain.doFilter(servletRequest, servletResponse);
        }

    }
}
