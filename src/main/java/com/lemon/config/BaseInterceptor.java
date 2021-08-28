package com.lemon.config;

import com.lemon.utils.IPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Liang Zhancheng
 * @date 2021/8/11 10:21
 */
@Configuration
public class BaseInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleTask.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        LOGGER.info("UserAgent: {}", request.getHeader("User-Agent"));
        LOGGER.info("用户访问地址：{}，访问者IP地址：{}", uri, IPUtil.getIpAddress(request));

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
