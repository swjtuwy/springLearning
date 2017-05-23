package com.map.restful;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.map.constant.Const;
import com.map.constant.TileManager;
import com.map.model.CoordAndPoint;
import com.map.model.Tile;
import com.map.service.MapServiceImpl;
import com.map.util.FileUtil;
import com.map.util.Response;
import com.map.util.ToolUtil;

@RestController
@RequestMapping("/images_2d")
public class MapRestController {
	
	private static final Logger logger = LoggerFactory.getLogger(MapRestController.class);
	private static double totalCount;
	
	private static int version;
	
	private static String splitStr = "\\|";
	private static boolean finished = false;
	@Autowired
	private MapServiceImpl mapserviceImpl;
	
	@Autowired
	private TileManager tileManager;
	
	@Autowired
	private ToolUtil toolUtil;
	
	
	@CrossOrigin
	@RequestMapping(value = "/getmaps_v1",method = RequestMethod.POST)
	public List<Map<String, String>> getMapList(@RequestParam("max") String maxGpsPoint, @RequestParam("min") String minGpsPoint, @RequestParam("level") int level){
		List<Map<String, String>> retList = new ArrayList<Map<String, String>>();
		
		String[] maxArr = maxGpsPoint.split(",");
		String[] minArr = minGpsPoint.split(",");
		
		double maxLat = Double.parseDouble(maxArr[0]);
		double maxLng = Double.parseDouble(maxArr[1]);
		double minLat = Double.parseDouble(minArr[0]);
		double minLng = Double.parseDouble(minArr[1]);
		
		
		ToolUtil t = new ToolUtil();
		CoordAndPoint maxcp = new CoordAndPoint(maxLat, maxLng);
		CoordAndPoint mincp = new CoordAndPoint(minLat, minLng);
		
		t.lonlat2mercator(mincp);
		t.lonlat2mercator(maxcp);
		
		Map<String, CoordAndPoint> mp = new HashMap<String, CoordAndPoint>();
		mp.put("min", mincp);
		mp.put("max", maxcp);
		
		List<Tile> tiles =  t.getAllTile(mp,level);
		System.out.println(tiles.size());
		for(Tile tmp:tiles){
			Map<String, String> tempMap = new HashMap<String, String>();
			String tempPath = tmp.getImagePath("","line");
			if(FileUtil.exists(tempPath)){
				tempMap.put("max", String.format("%.14f,%.15f",tmp.getMaxCp().getLanti(), tmp.getMaxCp().getLongti()));
				tempMap.put("min", String.format("%.14f,%.15f",tmp.getMinCp().getLanti(), tmp.getMinCp().getLongti()));
				System.out.println(tempPath);
				int pos = tempPath.indexOf("/images/");
				String tempURL = tempPath.substring(pos);
				tempMap.put("path", tempURL);
				retList.add(tempMap);
			}
		}
		
		return retList;
	}
	
	@CrossOrigin
	@PostMapping(value = "/getmaps_v2")
	//@RequestMapping(value = "/getmaps_v2",method = RequestMethod.POST)
	public List<Map<String, String>> getMaps(@RequestParam("lat") double lat, @RequestParam("lng") double lng, @RequestParam("level") int level, @RequestParam("scope") double scope){
		List<Map<String, String>> retList = new ArrayList<Map<String, String>>();
		
		ToolUtil t = new ToolUtil();
		
		CoordAndPoint center = new CoordAndPoint(lat, lng);
		t.lonlat2mercator(center);
		
		double minX = center.getX() - scope;
		double minY = center.getY() - scope;
		double maxX = center.getX() + scope;
		double maxY = center.getY() + scope;
		
		CoordAndPoint maxcp = new CoordAndPoint();
		CoordAndPoint mincp = new CoordAndPoint();
		maxcp.setX(maxX);
		maxcp.setY(maxY);
		mincp.setX(minX);
		mincp.setY(minY);
		t.mercator2lonlat(maxcp);
		t.mercator2lonlat(mincp);
		
		Map<String, CoordAndPoint> mp = new HashMap<String, CoordAndPoint>();
		mp.put("min", mincp);
		mp.put("max", maxcp);
		
		List<Tile> tiles =  t.getAllTile(mp,level);
		System.out.println(tiles.size());
		for(Tile tmp:tiles){
			Map<String, String> tempMap = new HashMap<String, String>();
			String tempPath = tmp.getImagePath("","line");
			if(FileUtil.exists(tempPath)){
				tempMap.put("max", String.format("%.14f,%.15f",tmp.getMaxCp().getLanti(), tmp.getMaxCp().getLongti()));
				tempMap.put("min", String.format("%.14f,%.15f",tmp.getMinCp().getLanti(), tmp.getMinCp().getLongti()));
				System.out.println(tempPath);
				int pos = tempPath.indexOf("/"+Const.folder.StrIndex+"/");
				String tempURL = tempPath.substring(pos);
				tempMap.put("path", "http://10.69.6.233:9090/mapService"+tempURL);
				retList.add(tempMap);
			}
		}
		
		return retList;
	}
	
	
//	@CrossOrigin
//	@GetMapping(value = "/{type}")
//	//@RequestMapping(value = "/getmaps_v2",method = RequestMethod.POST)
//	public ResponseEntity<String> getMapslipeng(@PathVariable("type") String type,@RequestParam("point") String point, @RequestParam("level") int level, @RequestParam("radius") double radius){
//		logger.info("get in "+type);
//		//String point="(-83.41076378434923,42.511436473292264)";
//		
//		if(level<=14 && !Const.preLevelList().contains(level) ){
//			
//			Response response = new Response();
//			return new ResponseEntity<String>(null, response.getHeader(), HttpStatus.NOT_MODIFIED);
//		}
//		point=point.substring(1,point.length()-1);
//		double lat = Double.parseDouble(point.split(",")[1]);
//		double lng = Double.parseDouble(point.split(",")[0]);
//		
//		
//		Map<String, CoordAndPoint> mp = getMaxMinPoint(lat,lng ,radius);
//		List<Tile> tiles =  toolUtil.getAllTile(mp,level);
//		System.out.println(tiles.size());
//		
//		JSONArray jsonArray = getTiles(tiles,type);
//		
//		Response response = new Response();
//		return new ResponseEntity<String>(jsonArray.toString(), response.getHeader(), HttpStatus.OK);
//	}
	
	
	
	@CrossOrigin
	@GetMapping(value = "/orginal_trajectory")
	// @RequestMapping(value = "/getmaps_v2",method = RequestMethod.POST)
	public ResponseEntity<String> getMapslipeng2(
			@RequestParam("point") String point,
			@RequestParam("level") int level,
			@RequestParam("radius") double radius,
			@RequestParam("vehicle") String vehicle,
			@RequestParam("color") String color) {
		
		Date  date = new Date();
		@SuppressWarnings("deprecation")
		int hour = date.getHours();
		String prefix = hour+"_trajectory";
		logger.info("get in " + vehicle + " force level : " + level
				+ " color : " + level);
		if(vehicle==null || vehicle.isEmpty()){
			logger.error("vehicle is null");
			return new ResponseEntity<String>("vehicle is null",new HttpHeaders(),HttpStatus.EXPECTATION_FAILED);
		}
		if(color==null|| color.isEmpty()){
			logger.error("color is null");
			return new ResponseEntity<String>("color is null",new HttpHeaders(),HttpStatus.EXPECTATION_FAILED);
		}
		if(!checkVehicleColorParam(vehicle, color)){
			logger.error("vehicle and color is not match");
			return new ResponseEntity<String>("vehicle and color is not match" ,new HttpHeaders(),HttpStatus.EXPECTATION_FAILED); 
		}
		String[] vehicleStr = vehicle.split(splitStr);
		String[] colorStr = color.split(splitStr);
		
		mapserviceImpl.drawRealTraj(prefix, point, level, colorStr, radius, 10000,vehicleStr);
		System.err.println("finished");
		
		point=point.substring(1,point.length()-1);
		double lat = Double.parseDouble(point.split(",")[1]);
		double lng = Double.parseDouble(point.split(",")[0]);
		
		Map<String, CoordAndPoint> mp = getMaxMinPoint(lat,lng ,radius);

		List<Tile> tiles =  toolUtil.getAllTile(mp,level);
		JSONArray jsonArray = getRealTrajTiles(prefix, tiles, colorStr,vehicleStr);
		return new ResponseEntity<String>(jsonArray.toString(), new HttpHeaders(), HttpStatus.OK);

	}
	
	
	private JSONArray getRealTrajTiles(String prefix, List<Tile> tiles,String[] colorStr, String[] ids){
		JSONArray  jsonAll = new JSONArray();
		for (int j = 0; j < colorStr.length; j++) {
			String type = "trajectory_"+colorStr[j]+"_"+String.valueOf(ids[j]);
			System.out.println(tiles.size());
			JSONObject jsonV = new JSONObject();
			jsonV.put("id", ids[j]);
			
			JSONArray jsonArray = new JSONArray();
			for(Tile tmp:tiles){
				String tempPath = tmp.getImagePath(prefix, type);
				if(FileUtil.exists(tempPath)){
					JSONObject jsonOne = new JSONObject();
					JSONObject max  = new JSONObject();
					JSONObject min  = new JSONObject();
					
					max.put("lng", tmp.getMaxCp().getLongti());
					max.put("lat", tmp.getMaxCp().getLanti());
					
					min.put("lng", tmp.getMinCp().getLongti());
					min.put("lat", tmp.getMinCp().getLanti());
					jsonOne.put("max", max);
					jsonOne.put("min", min);
					
					System.out.println(tempPath);
					int pos = tempPath.indexOf("/"+Const.folder.StrIndex+"/");
					String tempURL = tempPath.substring(pos);
					jsonOne.put("url", "http://10.69.6.233:9090/mapService"+tempURL);
					jsonArray.put(jsonOne);
				}
			}
			jsonV.put("images", jsonArray);
			jsonAll.put(jsonV);
		}
		
		return jsonAll;
	}
	
	private boolean checkVehicleColorParam(String vehicle, String color){
		String[] vehicleStr = vehicle.split(splitStr);
		String[] colorStr = color.split(splitStr);
		if(vehicleStr.length!=colorStr.length){
			return false;
		}
		if(!checkVehicleId(vehicleStr)){
			return false;
		}
		if(!checkColor(colorStr)){
			return false;
		}
		return true;
	}
	
	private boolean checkColor(String[] colorStr) {

		String regString = "[a-f0-9A-F]{6}";

		for (String str : colorStr) {
			if (!Pattern.matches(regString, str)) {
				return false;
			}
		}
		return true;
	}
	private boolean checkVehicleId (String[] vehicleStr) {
		
		for (String str : vehicleStr) {
			if(!StringUtils.isNumeric(str) ){
				return false;
			}
		}
		return true;
	}


	@CrossOrigin
	@GetMapping(value = "/{type}")
	// @RequestMapping(value = "/getmaps_v2",method = RequestMethod.POST)
	public ResponseEntity<String> getMapslipeng1(
			@PathVariable("type") String type,
			@RequestParam("point") String point,
			@RequestParam("level") int level,
			@RequestParam("radius") double radius,
			@RequestParam(value = "forceLevel", required = false) String forceLevel) {
		logger.info("get in "+type+" force level : "+forceLevel);
		//String point="(-83.41076378434923,42.511436473292264)";
		
		int currentVersion = mapserviceImpl.getVersion();
		//当前版本和产生图片的版本不一致， 返回错误 205 Reset Content.
		if(MapRestController.version!=currentVersion){
			return new ResponseEntity<String>(null, new HttpHeaders(), HttpStatus.RESET_CONTENT);
		}
		if (forceLevel==null && level<=14 && !Const.preLevelList().contains(level)  ) {
			return new ResponseEntity<String>(null, new HttpHeaders(), HttpStatus.NOT_MODIFIED);
		}else if(forceLevel!=null ){
			if(level<14&&level>0 && !Const.preLevelList().contains(level) && "in".equals(forceLevel)){
				level=level + 1;
			}else if(level<14&&level>0 && !Const.preLevelList().contains(level) && "out".equals(forceLevel)){
				level=level - 1;
			}else if(!Const.preLevelList().contains(level)){
				return new ResponseEntity<String>(null, new HttpHeaders(), HttpStatus.NOT_MODIFIED);
			}
		}
		System.out.println("level "+level);
		point=point.substring(1,point.length()-1);
		double lat = Double.parseDouble(point.split(",")[1]);
		double lng = Double.parseDouble(point.split(",")[0]);
		
		Map<String, CoordAndPoint> mp = getMaxMinPoint(lat,lng ,radius);
		
		String prefix = MapRestController.version + Const.MessageConstants.VERSION_SUB;

		List<Tile> tiles =  toolUtil.getAllTile(mp,level);
		JSONArray jsonArray = getTiles(prefix, tiles,type);
		return new ResponseEntity<String>(jsonArray.toString(), new HttpHeaders(), HttpStatus.OK);
	}
	
	private JSONArray getTiles(String prefix, List<Tile> tiles, String type){
//		version = mapserviceImpl.getVersion();
		System.out.println(tiles.size());
		JSONArray jsonArray = new JSONArray();
		for(Tile tmp:tiles){
			String tempPath = tmp.getImagePath(prefix, type);
			if(FileUtil.exists(tempPath)){
				JSONObject jsonOne = new JSONObject();
				JSONObject max  = new JSONObject();
				JSONObject min  = new JSONObject();
				
				max.put("lng", tmp.getMaxCp().getLongti());
				max.put("lat", tmp.getMaxCp().getLanti());
				
				min.put("lng", tmp.getMinCp().getLongti());
				min.put("lat", tmp.getMinCp().getLanti());
				jsonOne.put("max", max);
				jsonOne.put("min", min);
				
				System.out.println(tempPath);
				int pos = tempPath.indexOf("/"+Const.folder.StrIndex+"/");
				String tempURL = tempPath.substring(pos);
				jsonOne.put("url", "http://10.69.6.233:9090/mapService"+tempURL);
				jsonArray.put(jsonOne);
			}
		}
		return jsonArray;
	}
	
	private Map<String, CoordAndPoint>  getMaxMinPoint(double lat,double lng, double radius){
		
		CoordAndPoint center = new CoordAndPoint(lat, lng);
		toolUtil.lonlat2mercator(center);
		
		double minX = center.getX() - radius;
		double minY = center.getY() - radius;
		double maxX = center.getX() + radius;
		double maxY = center.getY() + radius;
		
		CoordAndPoint maxcp = new CoordAndPoint();
		CoordAndPoint mincp = new CoordAndPoint();
		maxcp.setX(maxX);
		maxcp.setY(maxY);
		mincp.setX(minX);
		mincp.setY(minY);
		toolUtil.mercator2lonlat(maxcp);
		toolUtil.mercator2lonlat(mincp);
		
		Map<String, CoordAndPoint> mp = new HashMap<String, CoordAndPoint>();
		mp.put("min", mincp);
		mp.put("max", maxcp);
		return mp;
		
	}
	
//	
//	public static void main(String args[]){
//		String point="(-83.41076378434923,42.511436473292264)";
//		point=point.substring(1,point.length()-1);
//		System.err.println(point);
//	}
	
	
//	public static void main(String args[]){
//		String point="a|aa";
//		String[] aa = point.split("\\|");
//		
//		System.err.println(aa.length);
//}
	
//	public static void checkNum (String aNumber) {
//	 
//   String regString = "[a-f0-9A-F]{6}";
//   if (Pattern.matches(regString, aNumber)){
//       //匹配成功
//   	System.out.println("true");
//   }
//}
//
	@CrossOrigin
	@GetMapping(value="/status")
	public ResponseEntity<String> getProgress(){
		
//        Response response = deviceManagerController.resetMessageUrl(device_id);
//        logger.info("response = " + response);
//        return new ResponseEntity<String>(response.getData(), response.getHeader(), response.getCode());
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("progress", 100);
		return new ResponseEntity<String>(jsonObject.toString(), new HttpHeaders(), HttpStatus.OK);

	}
	
	@GetMapping(value="/status/progress")
	public double getProgressF(){
		
		double size = mapserviceImpl.getAllTileSize();
		System.out.println(size);
		return size;
		
	}
	
	@GetMapping(value="/status/progress1")
	public void getProgressF1(){
		
		System.out.println(totalCount);

	}
	
	@GetMapping(value="/status/progress2")
	public double getProgressF2(){
		
		System.out.println(tileManager.getCurrentTileCount());
		return tileManager.getCurrentTileCount();
		
	}
	
	@GetMapping(value="/generate")
	public void generateImg() {
		int currentVersion = mapserviceImpl.getVersion();
		//初始化版本号
		if(currentVersion==0||currentVersion==-1){
			return ;
		}
		if(MapRestController.version>=0&&currentVersion>MapRestController.version){
			MapRestController.version = currentVersion;
		}
		String prefix = currentVersion+"_version";
		
		System.out.println("starting...!");
		long startTimeAll = System.currentTimeMillis();

		mapserviceImpl.draw(currentVersion,prefix);

		long endTime = System.currentTimeMillis();
		System.out.println("all data use time:" + (endTime - startTimeAll) + "ms");

	}
	
	
	public void initVersion(){
		
	}
	@GetMapping(value= "/version")
	public String getVersion() {
		return "test version";
	}
	
	@GetMapping(value="/pos")
	public void getPos (){
		Map<String, CoordAndPoint> m = mapserviceImpl.getPost();
		System.out.println(m.get("max").toString());
		System.out.println(m.get("min").toString());
	}
	
	
	public boolean getFinishedFlag(){
		return finished;
	}

	public static void setVersion(int version) {
		MapRestController.version = version;
	}
	
}
