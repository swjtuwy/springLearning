package com.map.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.map.constant.Const;
import com.map.constant.Const.color;
import com.map.model.CoordAndPoint;
import com.map.model.GpsType;
import com.map.model.Tile;
//import com.vividsolutions.jts.io.ParseException;

@Component
public class ToolUtil {
//	private GeotoolsUtil geotoolsUtil = new GeotoolsUtil();
	
	
	
	public void lonlat2mercator(CoordAndPoint cp){
	    double x = cp.getLongti() * 20037508.34d/180.0d;  //getLanti
	    double y = Math.log(Math.tan((90d+cp.getLanti())*Math.PI/360d))/(Math.PI/180d);
	    y = y *20037508.34d/180d; 
	    cp.setX(x); 
	    cp.setY(y); 
	}
	
	public void mercator2lonlat(CoordAndPoint cp){ 
	     
	    double longti = cp.getX()/20037508.34d*180d; 
	    double lanti = cp.getY()/20037508.34d*180d; 
	    lanti= 180d/Math.PI*(2d*Math.atan(Math.exp(lanti*Math.PI/180d))-Math.PI/2d);
	    cp.setLongti(longti);
	    cp.setLanti(lanti); 
	}
	
	//计算分多少块
	private double clip(int level){
		double allPX = Const.getAllPXByLevel(level);
		double clip = allPX/Const.MapConstants.basePx;
		return clip;
	}
	
	//每一块的长度
	public double lengthPerTile(int level){
		double clip = clip(level);
		double length=(Const.MapConstants.maxLat-Const.MapConstants.minLat)/clip;
		return length;
	}
	
	
	public List<Tile> getAllTile(Map<String, CoordAndPoint> orgalMap, int  level){
		double length = lengthPerTile(level);
		Map<String, Tile> tileMap = covertCoorCp(orgalMap,level,length);
		Tile minTile = tileMap.get(Const.MapConstants.min);
		Tile maxTile = tileMap.get(Const.MapConstants.max);
		List<Tile> tiles = getTilesInner(minTile,maxTile,level,length);
		
		return tiles;
		
	}
	
	public double getAllTileSize(Map<String, CoordAndPoint> orgalMap, List<Integer>  levelList){
		double sizeAll = 0;
		for (Integer level : levelList) {
			double length = lengthPerTile(level);
			Map<String, Tile> tileMap = covertCoorCp(orgalMap,level,length);
			Tile minTile = tileMap.get(Const.MapConstants.min);
			Tile maxTile = tileMap.get(Const.MapConstants.max);
			double size = getTileSize(minTile, maxTile);
			sizeAll = sizeAll+size;
		}
		
		return sizeAll;
		
	}
	
	private Map<String, Tile> covertCoorCp(Map<String, CoordAndPoint> orgalMap, int  level, double length){
		
		CoordAndPoint orgalMin = orgalMap.get(Const.MapConstants.min);
		CoordAndPoint orgalMax = orgalMap.get(Const.MapConstants.max);
		
//		double clip = clip(level);
//		System.out.println("lengt: "+length +" clip: "+clip);
//		double lng_minCount = Math.abs(Math.floor((Const.MapConstants.minLng -orgalMin.getX())/length));
//		double lat_minCount = Math.abs(Math.ceil((Const.MapConstants.minLat -orgalMin.getY())/length));
//		
//		double lng_maxCount = Math.abs(Math.floor((Const.MapConstants.maxLng -orgalMax.getX())/length));
//		double lat_maxCount = Math.abs(Math.ceil((Const.MapConstants.maxLat -orgalMax.getY())/length));
		
//		System.out.println("lng_minCount: "+lng_minCount+" lat_minCount: "+lat_minCount);
//		System.out.println("lng_maxCount: "+lng_maxCount+" lat_maxCount: "+lat_maxCount);

		
//		int minx = (int)lng_minCount;
//		int maxy = (int)(clip - lat_minCount);
//		
//		int miny = (int) lat_maxCount;
//		int maxx = (int)(clip - lng_maxCount);
		
		int maxx = (int)Math.ceil(Math.abs(orgalMax.getX() - Const.MapConstants.minLng )/length);
		int minx = (int)Math.floor(Math.abs(orgalMin.getX() - Const.MapConstants.minLng )/length);
		int miny = (int)Math.floor(Math.abs(Const.MapConstants.maxLat  -orgalMax.getY() )/length);
		int maxy= (int)Math.ceil(Math.abs(Const.MapConstants.maxLat  -orgalMin.getY() )/length);
		
		Tile MinTile = new Tile(minx,miny);
		Tile MaxTile = new Tile(maxx,maxy);
		MinTile.setZoom(level);
		MaxTile.setZoom(level);
		MinTile.setLength(length);
		MaxTile.setLength(length);
		
		Map<String, Tile> tmap = new HashMap<String, Tile>();
		tmap.put(Const.MapConstants.min, MinTile);
		tmap.put(Const.MapConstants.max, MaxTile);
		return tmap;
	}
	
	private List<Tile> getTilesInner(Tile minTile,Tile maxTile, int level , double length){
		List<Tile> tiles = new ArrayList<Tile>();
		for (int i = minTile.getX(); i <= maxTile.getX(); i++) {
			for (int j = minTile.getY(); j <= maxTile.getY(); j++) {
				Tile t = new Tile(i,j,level,length);
				tiles.add(t);
			}
		}
		return tiles;
	}
	
	private double getTileSize(Tile minTile,Tile maxTile){
		return (maxTile.getX()-minTile.getX()+1) * (maxTile.getY()-minTile.getY()+1);
	}
	
	
	public Map<String, CoordAndPoint> getPosition(List<ArrayList<CoordAndPoint>> kmlDataList){
		
		if (kmlDataList==null ||kmlDataList.size()<=0) {
			return null;
		}
		Map<String, CoordAndPoint> result = new HashMap<String,CoordAndPoint>();
		
		List<Double> lng = new ArrayList<Double>();
		List<Double> lat = new ArrayList<Double>();
		
		for (List<CoordAndPoint> list : kmlDataList) {
			for (CoordAndPoint point : list) {
				lng.add(point.getLongti());
				lat.add(point.getLanti());
			}
		}
		CoordAndPoint max = new CoordAndPoint(Collections.max(lat),Collections.max(lng));
		CoordAndPoint min = new CoordAndPoint(Collections.min(lat),Collections.min(lng));
		
		result.put(Const.MapConstants.max, max);
		result.put(Const.MapConstants.min, min);
		return result;
	}

//	public List<CoordAndPoint> intersectLineWithPoly(List<CoordAndPoint> pointList,CoordAndPoint p1, CoordAndPoint p2) {
//		String line=geotoolsUtil.listPoint2LineStr(pointList);
//		String poly= geotoolsUtil.pointToPolyStr(p1,p2);
//		if(line==null||poly==null){
//			return null;
//		}
//		List<CoordAndPoint>  intersectLine = new ArrayList<CoordAndPoint>();
//		String geometry=null;
//		try {
//			geometry = geotoolsUtil.intersectLineWithPoly(line, poly);
//		} catch (ParseException e) {
//			e.printStackTrace();
//			return null;
//		}
//		if (Const.geoConstant.empty.equals(geometry)) {
//			return null;
//		}
//		if (geometry.contains(Const.geoConstant.line)) {
//			String[] aa=geometry.split("\\(|\\)")[1].split(",");
//			for (int i = 0; i < aa.length; i++) {
//				String[] currentPoint=aa[i].trim().split(" ");
//				CoordAndPoint point = new CoordAndPoint();
//				//Double.valueOf(currentPoint[0]),Double.valueOf(currentPoint[1])
//				point.setX(Double.valueOf(currentPoint[0]));
//				point.setY(Double.valueOf(currentPoint[1]));
//				intersectLine.add(point);
//			}
//			return intersectLine;
//		}else {
//			return null;
//		}
//		
//	}
	
	public Color covertColor(String colorStr){
		Color color =  new Color(Integer.parseInt(colorStr, 16)) ;  
        //java.awt.Color[r=0,g=0,b=255]  
        return color;  
		
	}
	
	
	private final double EARTH_RADIUS = 6378.137;

	private double rad(double d) {
		return d * Math.PI / 180.0;
	}

	public double getDistance(double lat1, double lng1, double lat2, double lng2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}
	
	public int getWeight(int level) {
		double currentVal = Const.MapConstants.levelzoom[level];
		int[] widthList = { 4, 3, 2, 1 };
		if (currentVal < 0 || currentVal > 29) {
			return currentVal < 0 ? widthList[0] : widthList[widthList.length - 1];
		}
		int diffVal = 29 - 0;
		int span = diffVal / (widthList.length - 1);
		int index = (int) Math.floor(currentVal / span);
		return widthList[index];
	}
	
	public GpsType getPreURLByType(String type){
		 GpsType gpsType =null;
		if(type == null || type.isEmpty()){
			return null;
		}
		switch (type) {
		case "avg_gps":
			gpsType = new GpsType(Const.color.averageGps, Const.PreGenURL.avg_gps) ;
			break;
		case "slam_avg":
			gpsType = new GpsType(Const.color.slam, Const.PreGenURL.slam_avg) ;
			break;
		case "line":
			gpsType = new GpsType(Const.color.line, Const.PreGenURL.line) ;
			break;
		case "paint":
			gpsType = new GpsType(Const.color.paint, Const.PreGenURL.paint) ;
			break;
		case "road_edge":
			gpsType = new GpsType(Const.color.roadedge, Const.PreGenURL.road_edge) ;
		default:
			break;
		}
		return gpsType;
	}
	
	public String getRealURLByType(String type){
		String url = "";
		if(type == null || type.isEmpty()){
			return null;
		}
		switch (type) {
		case "avg_gps":
			url = Const.RealGenURL.avg_gps;
			break;
		case "slam_avg":
			url = Const.RealGenURL.slam_avg;
			break;
		case "line":
			url =Const.RealGenURL.line;
			break;
		case "paint":
			url = Const.RealGenURL.paint;
			break;
		case "road_edge":
			url = Const.RealGenURL.road_edge;
		default:
			url ="";
			break;
		}
		return url;
	}
	
//	public static void main(String args[]){
//		ToolUtil t = new ToolUtil();
//		Color  color = t.covertColor("ffffff");
//		System.out.println(color.toString());
//	}
//	
	
//	public double GetZoomByLevel(int level)  
//    {  
//        double zoom = 0.0;  
//        if (this.zoomList == null || this.zoomList.Count == 0)  
//            return zoom;  
//        foreach (MapZoom item in this.zoomList)  
//        {  
//            if (level == item.Level)  
//            {  
//                zoom = item.Zoom;  
//            }  
//        }  
//        return zoom;  
//    }  
	
//	public static void main(String args[]) {
//
//		// int w = getWeight(18);
//		// System.err.println(w);
//		
//	}


}
