package com.map.service;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.crypto.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.session.SessionProperties.Redis;
import org.springframework.stereotype.Component;

import com.map.constant.Const;
import com.map.constant.TileManager;
import com.map.model.CoordAndPoint;
import com.map.model.GpsType;
import com.map.util.HttpUtil;
import com.map.util.ToolUtil;

@Component
public class MapServiceImpl {
	
	@Autowired
	private HttpUtil httpUtil ;
	@Autowired
	private ToolUtil toolUtil ;
	
	@Autowired
	private TileManager tileManager;
	@Autowired
	private ParseGpsDataByDB parseGpsDataByDB;
	
	private String dbHost = "http://10.68.193.114:8080";
	
	public void draw(int version, String prefix){
		String[] typelist = Const.MapConstants.typeList;
		for (String type : typelist) {
			System.out.println("type: "+type);
			GpsType gpstype = toolUtil.getPreURLByType(type);
			String url ="";
			if (type.equals("avg_gps")) {
				url = "http://10.69.0.25:8080"+gpstype.getUrl();
			}else{
				url = dbHost+String.format(gpstype.getUrl(),version);
			}
			System.out.println("url : "+ url);
			List<ArrayList<CoordAndPoint>> lines = parseGpsDataByDB.getLines(url);
			List<Integer> levleList = Const.preLevelList();
			for (Integer level : levleList) {
				System.out.println("level :"+level);
				tileManager.createTile(prefix,false, type, lines, level, gpstype.getColor());
			}
		}
	}
	
	public double getAllTileSize(){
		Map<String, CoordAndPoint> m =parseGpsDataByDB.getMaxMinPoint();
		double size = parseGpsDataByDB.getTileCount(m);
		return size;
	}
	
	public Map<String, CoordAndPoint> getPost(){
		Map<String, CoordAndPoint> m =parseGpsDataByDB.getMaxMinPoint();
		return m;
	}
	
	public int getVersion(){
		int version = parseGpsDataByDB.getVersion();
		return version;
	}
	
	
	///trajectory/visualization?point=%s&radius=%d&threshold=%d&vehicle_id=%d
	//http://10.69.6.233:8083/trajectory/visualization?threshold=5000&type=merged&point=(-83.64270865917207,42.51588367087502)&radius=1360&vehicle_id=1
	public synchronized void drawRealTraj(String prefix, String point, int level, String[] colorStr, double radius, double threshold, String[] ids){
		
		for (int i = 0; i < ids.length; i++) {
			String url ="http://10.69.6.233:8083"+ String.format(Const.RealGenURL.orginal_trajectory, point,radius,threshold,Integer.valueOf("1"));
//			String url ="http://10.69.6.233:8083"+ String.format(Const.RealGenURL.orginal_trajectory, point,radius,threshold,Integer.valueOf(ids[i]));
			
			List<ArrayList<CoordAndPoint>> lines = parseGpsDataByDB.getTrajectory(url);
			
			String type = "trajectory_"+colorStr[i]+"_"+ids[i];
			Color color = toolUtil.covertColor(colorStr[i]);
			tileManager.createTile(prefix, false, type, lines, level, color);
			
		}
	}
	
	
	public void drawRealOther(String point,String level, Color color, double redius, double threshold,String type){
		
	}
	
//	
//	public static void main(String args[]){
//		String url ="http://10.69.6.233:8083"+ String.format("/trajectory/visualization?point=%s&radius=%s&threshold=%s&vehicle_id=%d", 
//				"(adfdafdfasdf)",12222.0d,1000d,Integer.valueOf(1));
//		System.out.println(url);
//	}
	
}
