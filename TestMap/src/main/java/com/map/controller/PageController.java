package com.map.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

	/**
	 * 
	 * @return testImage page
	 */
	@RequestMapping("/mapdemo")
	public String testPng() {
		return "/html/test.html";
	}
	
	@RequestMapping("/mapdemo1")
	public String testPng1() {
		return "/html/test1.html";
	}

}
