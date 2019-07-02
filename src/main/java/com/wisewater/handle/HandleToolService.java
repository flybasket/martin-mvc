package com.wisewater.handle;

import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface HandleToolService {

	public Object[] handle(HttpServletRequest request, HttpServletResponse response, Method method,
			Map<String, Object> beans);
}
