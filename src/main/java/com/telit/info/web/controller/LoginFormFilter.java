package com.telit.info.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class LoginFormFilter extends FormAuthenticationFilter {

    private boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWith = request.getHeader("x-requested-with");
        if (requestedWith != null && requestedWith.equalsIgnoreCase("XMLHttpRequest")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        HttpServletResponse httpServletResponse = (HttpServletResponse)response;
        if (isLoginRequest(request, response)) {
            if (isLoginSubmission(request, response)) {
                log.trace("Login submission detected.  Attempting to execute login.");
                return executeLogin(request, response);
            } else {
                log.trace("Login page view.");
                return true;
            }
        } else {
            log.trace("Attempting to access a path which requires authentication.  Forwarding to the Authentication url [" + getLoginUrl() + "]");
            //如果是Ajax请求,不跳转登录
            if (isAjaxRequest(httpServletRequest)){
                //System.out.println("ajax");
                httpServletResponse.setStatus(401);
            } else {
                saveRequestAndRedirectToLogin(request,response);
            }
            return false;
        }
    }
}
