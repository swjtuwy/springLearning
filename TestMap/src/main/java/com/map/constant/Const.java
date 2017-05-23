/**
 *******************************************************************************
 *                       Continental Confidential
 *                  Copyright (c) Continental AG. 2017
 *
 *      This software is furnished under license and may be used or
 *      copied only in accordance with the terms of such license.
 *******************************************************************************
 * @file  Const.java
 * @brief Define Const
 *******************************************************************************
 */
package com.map.constant;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;

public class Const {
	
	public static interface ResponseCode {
		HttpStatus SUCCESS = HttpStatus.OK;
		HttpStatus INVALID_TOKEN = HttpStatus.UNAUTHORIZED;
		HttpStatus NOT_AUTHORIZED = HttpStatus.FORBIDDEN;
		HttpStatus FAILED = HttpStatus.BAD_REQUEST;
		HttpStatus NOT_FOUND = HttpStatus.NOT_FOUND;
		HttpStatus INTERNAL_SERVER_ERROR = HttpStatus.INTERNAL_SERVER_ERROR;
		HttpStatus LOGIN_SUCCESS = HttpStatus.CREATED;
		HttpStatus LOGIN_ERROR = HttpStatus.ACCEPTED;
		HttpStatus LOGIN_NEEDED = HttpStatus.NON_AUTHORITATIVE_INFORMATION;
		HttpStatus LOGOUT_SUCCESS = HttpStatus.NO_CONTENT;
	}
	public static interface MessageConstants {
		String MESSAGE_HANDLER_NOT_FOUND = "Handler not found";
		String MESSAGE_PARSE_BODY_FAILED = "Parse body failed";
		String SERVER_ERROR_FORMAT = "{\"code\":-1, \"src\":\"%s\", \"message\":\"%s\"}";
		String SERVER_UNSERVICE = "Connect to server failed!";
		String DOWNLOAD_FILE_WRITE_FAIL = "Download file write to file fail";
		String VERSION_SUB= "_version";
	}
	
	public static interface folder{
//		String imagePath = "/home/test/Documents/sprint0512/images200";
		String imagePath = "/opt/roadDB/images200";
		
		String StrIndex= "images200";
	}

	public static interface PreGenURL {
		// String maxMin = "http://127.0.0.1:8080/vehicle_list/visualization";
		String maxMin = "/vehicle_list/visualization";
		String version = "/version/maps_to_kml";
		
		String line = "/lines/maps_to_kml?version=%d";
		String paint = "/paints/maps_to_kml?version=%d";
		String slam_avg ="/avgslams/maps_to_kml?version=%d";
		String avg_gps = "/rd/maps_to_kml?query=1";
		String road_edge = "/roads/visualization?point=%s&radius=%s&threshold=%s&get2d=1";
//		String trajectory = "/trajectory/visualization?threshold=%d&point=%s&radius=%d&vehicle_id=%d";
	}
	
	public static interface RealGenURL {
		String line = "/line/visualization?point=%s&radius=%s&threshold=%s";
		String paint = "/paint/visualization?point=%s&radius=%s&threshold=%s";
		String slam_avg ="/expected_path/visualization?point=%s&radius=%s&threshold=%s";
		String avg_gps = "/rd/visualization?point=%s&radius=%s&threshold=%s";
		String road_edge = "/roads/visualization?point=%s&radius=%s&threshold=%s&get2d=1";
		String orginal_trajectory = "/trajectory/visualization?point=%s&radius=%s&threshold=%s&vehicle_id=%d&type=merged";
  		
	}
	
	public static interface MapConstants {
		//{"max_lat":42.51665931584794,"min_lng":-83.64810490018381,"min_lat":42.5166342737121,"max_lng":-83.63939612750755}
		String max_lat="max_lat";
		String min_lng="min_lng";
		String min_lat="min_lat";
		String max_lng="max_lng";
		
		String min = "min";
		String max = "max";
		String moreResults="moreResults";
		double maxLat= 20037508.3427892d;
		double minLat=-20037508.3427892d;
		double maxLng= 20037508.3427892d;
		double minLng=-20037508.3427892d;
		
		double basePx = 512;
		
		double[] levelzoom ={3000*1000,3000*1000,3000*1000,1000*1000,500*1000,300*1000,200*1000,50*1000,20*1000,10*1000,5*1000,3*1000,1*1000,500,
				300,200,100,50,20,10,5,3,1,1};
		
//		String[] typeList = {"avg_gps","slam_avg","line","paint","road_edge"};
		
		String[] typeList = {"avg_gps","slam_avg","line","paint"};
	}
	
	public static List<Integer> allLevelList(){
		List<Integer> levelList = new ArrayList<Integer>();
		for (int i = 2; i < 23; i=i+2) {
			levelList.add(i);
		}
		return levelList;
	}
	
	public static List<Integer> preLevelList(){

		List<Integer> levelList = new ArrayList<Integer>();
		for (int i = 2; i < 16; i=i+2) {
			levelList.add(i);
		}
		return levelList;
	
		
	}
	
   public static interface ResponseKey{
		String OPS = "ops";
		String DATA = "data";
		String RETURN_CODE = "code";
		String RETURN_MSG = "message";
		String COMMAND_RET_CODE = "ret_code";
		String STD_OUT = "std_out";
		String REQ_ID = "returnId";
	}
	
	public static interface geoConstant{
		String empty = "GEOMETRYCOLLECTION EMPTY";
		String multipoint = "MULTIPOINT";
		String point = "POINT";
		String line = "LINESTRING";
	}
	
	public static double getAllPXByLevel(int level){
		double base = 512;
		if(level<0||level>23){
			return 0;
		}else if(level==0){
			return 1*base;
		}else{
			return base * Math.pow(2,level-1);
		}
	}
	
	//Color[] colors ={Color.RED, Color.BLACK, Color.darkGray,Color.pink};
	public static interface color{
		Color paint = Color.WHITE;
		Color line = Color.BLACK;
		Color slam = Color.GRAY;
		Color averageGps = Color.YELLOW;
		Color roadedge = new Color(85,107,47);
	}
	
	public static interface RSTColor{
		Color Corvette = Color.BLUE;
		Color Sonic = Color.RED;
		Color Suburban = Color.GREEN;
		Color Tahoe = Color.MAGENTA;
	}
	
	public static interface dbConst{
		String DATA="data";
		String MAXVERSION="max_version";
		int errorVersion = -1;
	}
	
	
//	public static void main(String args[]){
//		List<Integer> levelList = levelList();
//		System.out.println(levelList.size());
//	}
}
