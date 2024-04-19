package com.yuyu.handler.impl;

import com.alibaba.fastjson.JSON;
import com.yuyu.exception.Code;
import com.yuyu.pojo.Result;
import com.yuyu.utils.WebUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
/**
 * 实现权限验证异常的捕获
 */
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        Result result = new Result<>(Code.ACCESSDENIED_ERROR, "您的权限不足");
        String jsonString = JSON.toJSONString(result);

        // 处理异常
        WebUtils.renderString(response,jsonString);
    }
}
