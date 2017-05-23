package com.map.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.map.constant.Const;

@Configuration
public class ImageWebAppConfig extends WebMvcConfigurerAdapter {
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/"+Const.folder.StrIndex+"/**").addResourceLocations("File:"+Const.folder.imagePath+"/");
        super.addResourceHandlers(registry);
    }
}
