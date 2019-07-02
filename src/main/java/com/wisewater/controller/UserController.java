package com.wisewater.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wisewater.annotation.MartinController;
import com.wisewater.annotation.MartinRequestMapping;
import com.wisewater.annotation.MartinRequestParam;
import com.wisewater.annotation.MartinRequestQualifier;
import com.wisewater.service.UserService;

@MartinController
@MartinRequestMapping("/martin")
public class UserController {

	@MartinRequestQualifier("UserServiceImpl")
	private UserService userService;

	@MartinRequestMapping("/query")
	public void query(HttpServletRequest request, HttpServletResponse response, @MartinRequestParam("name") String name,
			@MartinRequestParam("age") String age) {
		PrintWriter out = null;

		try {
			out = response.getWriter();
			String query = userService.query(name, age);
			out.print(query);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

}
