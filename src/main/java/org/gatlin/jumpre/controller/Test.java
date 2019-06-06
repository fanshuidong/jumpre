package org.gatlin.jumpre.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/Test")
public class Test {

	@ResponseBody
	@RequestMapping("/test")
	public Object test() {
		return "success";
	}
}
