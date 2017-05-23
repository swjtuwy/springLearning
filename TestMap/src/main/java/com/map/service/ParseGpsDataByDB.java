package com.map.service;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.map.constant.Const;
import com.map.constant.FomartGPSFromDB;
import com.map.model.CoordAndPoint;
import com.map.model.PointAndDis;
import com.map.util.HttpUtil;
import com.map.util.Response;
import com.map.util.ToolUtil;

@Component
public class ParseGpsDataByDB {
	
	private static final Logger logger = LoggerFactory.getLogger(ParseGpsDataByDB.class);
	
	private String dbHost = "http://10.69.6.111:8080";  
	//
//	private String dbHost = "http://10.68.193.114:8080";
	
	@Autowired
	private HttpUtil httpUtil ;
	@Autowired
	private ToolUtil toolUtil ;
	
	
	public List<ArrayList<CoordAndPoint>> getLines(String url) {
		Response resp = httpUtil.doGet(url);

		if (!resp.getCode().equals(HttpStatus.OK)){
			return null;
		}
		return formatData(resp.getData());
	}
	
	private List<ArrayList<CoordAndPoint>> formatData(String jsonstr) {

		List<ArrayList<CoordAndPoint>> lines = new ArrayList<ArrayList<CoordAndPoint>>();
		// locations
		JSONObject dataJson = new JSONObject(jsonstr);
		JSONArray jsonarray =dataJson.getJSONArray(Const.ResponseKey.DATA);
		if (jsonarray==null||jsonarray.length()<=0) {
			return null;
		}
		try {

			int len = jsonarray.length();

			for (int i = 0; i < len; i++) {
				ArrayList<CoordAndPoint> listOne = new ArrayList<CoordAndPoint>();
				JSONObject jsonObject = (JSONObject) jsonarray.get(i);
				
				String[] lanlangs = jsonObject.getString("locations").split("\\)\\(");

				for (String item : lanlangs) {
					CoordAndPoint cp = new CoordAndPoint();
					String temp = item.replaceAll("[\\(]", "");
					temp = temp.replaceAll("[\\)]", "");
					cp.setLongti(Double.parseDouble(temp.split(",")[0]));
					cp.setLanti(Double.parseDouble(temp.split(",")[1]));
					toolUtil.lonlat2mercator(cp);
					listOne.add(cp);
				}

				lines.add(listOne);
			}

		} catch (Exception e) {
			String err = "Can't parse to a json string with:\n ";
			logger.error(err,e);
		}

		return lines;
	}
	
	public List<ArrayList<CoordAndPoint>> getTrajectory(String url) {
		Response resp = httpUtil.doGet(url);

		if (!resp.getCode().equals(HttpStatus.OK)){
			return null;
		}
		return formatTrajectoryData(resp.getData());
	}
	
	private List<ArrayList<CoordAndPoint>> formatTrajectoryData(String jsonstr) {

		List<ArrayList<CoordAndPoint>> lines = new ArrayList<ArrayList<CoordAndPoint>>();
		// locations
		JSONObject dataJson = new JSONObject(jsonstr);
		JSONArray jsonarray =dataJson.getJSONArray(Const.ResponseKey.DATA);
		if (jsonarray==null||jsonarray.length()<=0) {
			return null;
		}
		try {


			ArrayList<CoordAndPoint> listOne = new ArrayList<CoordAndPoint>();
			JSONObject jsonObject = (JSONObject) jsonarray.get(0);
			
			String[] line = jsonObject.getString("locations").split("\\|");

			for (String s: line) {
				String[] tmp = s.split("\\)\\(");
				for (String item  : tmp) {
					CoordAndPoint cp = new CoordAndPoint();
					String temp = item.replaceAll("[\\(]", "");
					temp = temp.replaceAll("[\\)]", "");
					cp.setLongti(Double.parseDouble(temp.split(",")[0]));
					cp.setLanti(Double.parseDouble(temp.split(",")[1]));
					toolUtil.lonlat2mercator(cp);
					listOne.add(cp);
				}
				
				lines.add(listOne);
			}
		
		} catch (Exception e) {
			String err = "Can't parse to a json string with:\n ";
			logger.error(err,e);
		}

		return lines;
	}
	

	
	public int getVersion(){
		Response res = httpUtil.doGet(dbHost+Const.PreGenURL.version);
		if(res!=null&&res.getCode().equals(HttpStatus.OK)&& res.getData()!=null&& !res.getData().isEmpty()){
			JSONObject data = new JSONObject(res.getData());
			JSONArray jsonArray = data.getJSONArray(Const.dbConst.DATA);
			if(jsonArray==null||jsonArray.length()==0){
				return Const.dbConst.errorVersion;
			}
			JSONObject vJson= (JSONObject) jsonArray.get(0);
			int version = vJson.getInt(Const.dbConst.MAXVERSION);
			return version;
		}else{
			return Const.dbConst.errorVersion;
		}
	}
	
	
	public Map<String, CoordAndPoint> getMaxMinPoint(){
		Response resp = httpUtil.doGet(dbHost+Const.PreGenURL.maxMin);
		if(!resp.getCode().equals(Const.ResponseCode.SUCCESS)){
			return null;
		}
		if (resp.getData()==null||resp.getData().isEmpty()) {
			return null;
		}
		JSONObject jsonObject = new JSONObject(resp.getData());
		JSONArray jsonArray=jsonObject.getJSONArray(Const.MapConstants.moreResults).getJSONArray(0);
		JSONObject lngLat = jsonArray.getJSONObject(0);
		
		CoordAndPoint mincoorAndPoint = new CoordAndPoint();
		mincoorAndPoint.setLanti(lngLat.getDouble(Const.MapConstants.min_lat));
		mincoorAndPoint.setLongti(lngLat.getDouble(Const.MapConstants.min_lng));
		
		CoordAndPoint maxcoorAndPoint = new CoordAndPoint();
		maxcoorAndPoint.setLanti(lngLat.getDouble(Const.MapConstants.max_lat));
		maxcoorAndPoint.setLongti(lngLat.getDouble(Const.MapConstants.max_lng));
		
		toolUtil.lonlat2mercator(mincoorAndPoint);
		toolUtil.lonlat2mercator(maxcoorAndPoint);
		
		Map<String, CoordAndPoint> map = new HashMap<String, CoordAndPoint>();
		map.put(Const.MapConstants.min, mincoorAndPoint);
		map.put(Const.MapConstants.max, maxcoorAndPoint);
		return map;
	}
	
	
	
	public PointAndDis getCenterPointAndDis(Map<String, CoordAndPoint> map){
		PointAndDis pointAndDis = new PointAndDis();
		CoordAndPoint max = map.get(Const.MapConstants.max);
		CoordAndPoint min = map.get(Const.MapConstants.min);
		CoordAndPoint centerPoint = new CoordAndPoint((max.getLanti()+min.getLanti())/2, (max.getLongti()+min.getLongti())/2);

		double d1 = toolUtil.getDistance(max.getLanti(), max.getLongti(), centerPoint.getLanti(), centerPoint.getLongti());
		double d2 = toolUtil.getDistance(min.getLanti(), min.getLongti(), centerPoint.getLanti(), centerPoint.getLongti());
		double distance = d1 > d2 ? d1 : d2;
		pointAndDis.setCoordAndPoint(centerPoint);;
		pointAndDis.setDistance(distance + 50);
		return pointAndDis;
	}
	
	
	public double getTileCount(Map<String, CoordAndPoint> map){
		double size = toolUtil.getAllTileSize(map, Const.preLevelList());
		return size;
	}
	
	
	
//	public static void main(String args[]){
//		
//		ParseGpsDataByDB p = new ParseGpsDataByDB();
////		System.err.println(p.getVersion());
////		List<ArrayList<CoordAndPoint>>  gps = p.getLines(p.dbHost+Const.PreGenURL.gps);
//		//List<ArrayList<CoordAndPoint>>  l = p.getLines(p.dbHost+String.format(Const.PreGenURL.avgslams, 4));
//		
//		//threshold=50&type=merged&point=&radius=324384&vehicle_id=1
//		///trajectory/visualization?point=%s&radius=%d&threshold=%d&vehicle_id=%d
//		///roads/visualization?point=%s&radius=%d&threshold=%d&get2d=1
////		List<ArrayList<CoordAndPoint>>  t= p.getTrajectory(p.dbHost+String.format(Const.RealGenURL.real_trajectory, "(-83.30520629882814,42.512601715736686)", 324384, 1000, 1));
////		/lines/maps_to_kml?version=%d
//	
//		
////		List<ArrayList<CoordAndPoint>>  t= p.getLines(p.dbHost+String.format(Const.PreGenURL.roadedge, "(-83.30520629882814,42.512601715736686)", 324384, 1000));
//		
//		
////		String aString ="adfa|dfad";
////		System.err.println(aString.split("\\|")[1]);
//		
////		System.out.println(l.size());
////		Map<String, CoordAndPoint> m =p.getMaxMinPoint();
//		
////		System.out.println(p.getTileCount(m));
//		if(Const.preLevelList().contains(13)){
//			System.out.println("true");
//		}else{
//			System.out.println("false");
//		}
////		
////		Color color = new col
//	}
	
	

	
	

}
