package com.wisewater.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wisewater.annotation.MartinController;
import com.wisewater.annotation.MartinRequestMapping;
import com.wisewater.annotation.MartinRequestParam;
import com.wisewater.annotation.MartinRequestQualifier;
import com.wisewater.annotation.MartinService;
import com.wisewater.handle.HandleToolService;

public class DispatchServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<String> classNames = new ArrayList<String>();
	Map<String, Object> beans = new HashMap<String, Object>();
	Map<String, Object> handMap = new HashMap<String, Object>();

	@Override
	public void init(ServletConfig config) throws ServletException {
		// 1. 扫描包
		doScanPackage("com.wisewater");

		for (String className : classNames) {
			System.out.println("className: " + className);
		}

		// 2. 把对应的注解的类实例化
		doInstance();

		for (Map.Entry<String, Object> entry : beans.entrySet()) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}

		// 3. 把已经实例化的类注入到对应的添加@MartinRequestQualifier变量中
		iocDi();

		// 4. 把url映射控制器的方法
		handleMapping();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// martin-mvc/martin/query
		String requestURI = req.getRequestURI();
		String servletPath = req.getContextPath();

		String url = requestURI.replace(servletPath, "");
		String[] split = url.split("/");
		if (split.length == 1) {
			PrintWriter out = resp.getWriter();
			out.println("Hello World!!!");
			out.flush();
			return;
		}

		Method method = (Method) handMap.get(url);

		if (method == null) {
			PrintWriter out = resp.getWriter();
			out.println("404");
			out.flush();
			return;
		}

		Object object = beans.get("/" + split[1]);

		HandleToolService handleToolService = (HandleToolService) beans.get("HandleToolServiceImpl");
		Object[] args = handleToolService.handle(req, resp, method, beans);

		try {
			method.invoke(object, args);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// 扫描包
	public void doScanPackage(String packages) {
		URL url = DispatchServlet.class.getClassLoader().getResource(packages.replaceAll("\\.", "/"));
		String rootPath = url.getPath();
		File file = new File(rootPath);
		String[] list = file.list();

		for (String path : list) {
			File file2 = new File(rootPath + "/" + path);
			if (file2.isDirectory()) {
				doScanPackage(packages + "." + path);
			} else {
				classNames.add(packages + "." + path);
			}
		}
	}

	// 为所有扫描到加入Controller和Service注解的类初始化
	public void doInstance() {

		for (String className : classNames) {
			String cn = className.replace(".class", "");
			try {
				Class<?> clazz = Class.forName(cn);

				if (clazz.isAnnotationPresent(MartinController.class)) {

					Object instance = clazz.newInstance();
					// TODO 这里可能会存在问题，就是该控制器添加这个注解
					MartinRequestMapping requestMapping = clazz.getAnnotation(MartinRequestMapping.class);

					String value = requestMapping.value();

					beans.put(value, instance);

				} else if (clazz.isAnnotationPresent(MartinService.class)) {
					Object instance = clazz.newInstance();
					MartinService martinService = clazz.getAnnotation(MartinService.class);
					String value = martinService.value();
					beans.put(value, instance);
				} else {
					continue;
				}

			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	// 依赖注入
	public void iocDi() {
		for (Map.Entry<String, Object> entry : beans.entrySet()) {

			Object object = entry.getValue();

			Class<? extends Object> class1 = object.getClass();

			Field[] fields = class1.getDeclaredFields();

			for (Field field : fields) {

				if (field.isAnnotationPresent(MartinRequestQualifier.class)) {

					MartinRequestQualifier qualifier = field.getAnnotation(MartinRequestQualifier.class);

					String key = qualifier.value();
					Object instance = beans.get(key);

					try {
						field.setAccessible(true);
						field.set(object, instance);
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					continue;
				}
			}

		}
	}

	// 处理映射
	public void handleMapping() {
		for (Map.Entry<String, Object> entry : beans.entrySet()) {

			Object object = entry.getValue();

			Class<? extends Object> class1 = object.getClass();

			if (class1.isAnnotationPresent(MartinController.class)) {

				MartinRequestMapping requestMapping = class1.getAnnotation(MartinRequestMapping.class);

				String url = requestMapping.value();

				Method[] methods = class1.getMethods();

				for (Method method : methods) {

					if (method.isAnnotationPresent(MartinRequestMapping.class)) {
						MartinRequestMapping annotation = method.getAnnotation(MartinRequestMapping.class);
						String path = annotation.value();
						handMap.put(url + path, method);
					} else {
						continue;
					}
				}

			} else {
				continue;
			}

		}
	}

	public static void main(String[] args) {
		new DispatchServlet().doScanPackage("com.wisewater");

		// File file = new File
		// System.out.println(rootPath);
	}
}
