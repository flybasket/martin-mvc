package com.wisewater.handle.impl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wisewater.annotation.MartinService;
import com.wisewater.argmentResolver.ArgumentResolver;
import com.wisewater.handle.HandleToolService;

@MartinService("HandleToolServiceImpl")
public class HandleToolServiceImpl implements HandleToolService {

	public Object[] handle(HttpServletRequest request, HttpServletResponse response, Method method,
			Map<String, Object> beans) {

		Class<?>[] parameterTypes = method.getParameterTypes();
		Map<String, Object> argumentResolvers = getArgumentResolvers(beans);
		Object[] args = new Object[parameterTypes.length];
		for (int i = 0; i < parameterTypes.length; i++) {
			Class<?> class1 = parameterTypes[i];
			for (Map.Entry<String, Object> entry : argumentResolvers.entrySet()) {
				ArgumentResolver argumentResolver = (ArgumentResolver) entry.getValue();
				if (argumentResolver.support(class1, i, method)) {
					args[i] = argumentResolver.argumentResolver(request, response, class1, i, method);
					break;
				}
			}

		}

		return args;
	}

	public Map<String, Object> getArgumentResolvers(Map<String, Object> beans) {
		Map<String, Object> argumentResolvers = new HashMap<String, Object>();
		for (Map.Entry<String, Object> entry : beans.entrySet()) {
			Object object = entry.getValue();
			Class<?> clazz = object.getClass();
			if (ArgumentResolver.class.isAssignableFrom(clazz)) {
				argumentResolvers.put(entry.getKey(), object);
			}
		}
		return argumentResolvers;
	}

}
