package com.wisewater.argmentResolver;

import java.lang.reflect.Method;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wisewater.annotation.MartinService;

@MartinService("responseArgResolver")
public class ServletResponseArgResolver implements ArgumentResolver {

	public boolean support(Class<?> type, int index, Method method) {
		return ServletResponse.class.isAssignableFrom(type);
	}

	public Object argumentResolver(HttpServletRequest request, HttpServletResponse response, Class<?> type, int index,
			Method method) {
		return response;
	}

}
