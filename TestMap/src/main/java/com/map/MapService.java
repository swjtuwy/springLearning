package com.map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

import com.map.restful.MapRestController;


@SpringBootApplication
@ComponentScan
public class MapService extends  SpringBootServletInitializer {
	public static void main(String[] args){
		
		MapRestController.setVersion(1);
		SpringApplication.run(MapService.class, args);
	}
}
