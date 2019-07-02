package com.wisewater.argmentResolver;

import java.lang.reflect.Method;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wisewater.annotation.MartinService;

@MartinService("requestArgResolver")
public class ServletRequestArgResolver implements ArgumentResolver {

	public boolean support(Class<?> type, int index, Method method) {
		return ServletRequest.class.isAssignableFrom(type);
	}

	public Object argumentResolver(HttpServletRequest request, HttpServletResponse response, Class<?> type, int index,
			Method method) {
		return request;
	}

}
