package com.wisewater.argmentResolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wisewater.annotation.MartinRequestParam;
import com.wisewater.annotation.MartinService;

@MartinService("requestParamArgResolver")
public class RequestParamArgResolver implements ArgumentResolver {

	public boolean support(Class<?> type, int index, Method method) {
		Annotation[][] annotations = method.getParameterAnnotations();
		Annotation[] annotations2 = annotations[index];
		for (Annotation annotation : annotations2) {
			if (MartinRequestParam.class.isAssignableFrom(annotation.getClass())) {
				return true;
			}
		}

		return false;
	}

	public Object argumentResolver(HttpServletRequest request, HttpServletResponse response, Class<?> type, int index,
			Method method) {
		Annotation[][] annotations = method.getParameterAnnotations();
		Annotation[] annotations2 = annotations[index];
		for (Annotation annotation : annotations2) {
			if (MartinRequestParam.class.isAssignableFrom(annotation.getClass())) {

				MartinRequestParam requestParam = (MartinRequestParam) annotation;
				String value = requestParam.value();

				return request.getParameter(value);
			}
		}

		return null;
	}

}
