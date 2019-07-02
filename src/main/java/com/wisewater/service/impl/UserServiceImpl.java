package com.wisewater.service.impl;

import com.wisewater.annotation.MartinService;
import com.wisewater.service.UserService;

@MartinService("UserServiceImpl")
public class UserServiceImpl implements UserService {

	public String query(String name, String age) {
		return "============== name: " + name + ",age: " + age;
	}

	public String update(String param) {
		return param;
	}

	public String insert(String param) {
		return param;
	}

}
