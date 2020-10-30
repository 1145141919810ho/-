package com.edu_management.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.edu_management.JWT.JWTUtil;

@CrossOrigin  
@RestController
public class TokenCheck {
	@Autowired
	    HttpServletRequest request;
	@RequestMapping(value = "/loginCheck", method = {RequestMethod.POST})
   
    public Map<String, Object> Login() {
		Map<String, Object> result=new HashMap<String, Object>();
		String token = request.getHeader("token");
		if(JWTUtil.verify(token) != null) {
			result.put("code", 0);
			result.put("msg","自动登录成功");
		}
		else {
			result.put("code", 1);
			result.put("msg", "身份过期请重新登陆");
		}
		return result;	
	}
}
