package com.jcz.test;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mobile.device.DeviceResolverHandlerInterceptor;

public class BackDeviceResolverHandlerInterceptor extends DeviceResolverHandlerInterceptor {
	
	private static final Logger log = LoggerFactory.getLogger(BackDeviceResolverHandlerInterceptor.class);
	
	public BackDeviceResolverHandlerInterceptor() {
		
	}
	
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        String uri = request.getRequestURI();
//        Object account = request.getSession().getAttribute("loginData");
        log.info("====interceptor===="+uri);
        if(uri.contains("tologin")
        		||uri.contains("/index.jsp")
        		||uri.contains("/login.do")
        		||uri.contains("/bill.do")
        		||uri.contains("/provider.do")
        		||uri.contains("/pwdmodify.do")
        		||uri.contains("/logout.do")
        		||uri.contains("tolist.do")){
        	return super.preHandle(request, response, handler);
        }
//		if(null == account){//未登录
//			response.sendRedirect(request.getContextPath() +"/tologin");
//			return false;
//		}
		return super.preHandle(request, response, handler);
	}
}
