package com.map.model;

import java.awt.Color;

public class GpsType {
	
	private Color color;
	
	private String url;
	
	public GpsType(){
		
	}
	
	public GpsType(Color color, String url){
		this.color = color;
		this.url = url;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	
	

}
