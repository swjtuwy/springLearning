package com.map.constant;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.map.model.CoordAndPoint;
import com.map.model.Tile;
import com.map.util.ToolUtil;

@Component
public class TileManager {
	
	private static int TIlESIZE=(int)Const.MapConstants.basePx;
	
//	private Color[] colors ={Color.RED, Color.BLACK, Color.darkGray,Color.pink};
	
	private static double currentCount;
	
	@Autowired
	private ToolUtil toolUtil;
	
	
	
	public void createTile(String preFix, boolean tragj, String type,List<ArrayList<CoordAndPoint>> lines, int level, Color color){
		int weight = toolUtil.getWeight(level);
		if(lines == null || lines.size() <= 0){
			return;
		}
		
		ToolUtil toolUtil = new ToolUtil();
		Map<String, CoordAndPoint> mp = toolUtil.getPosition(lines);
		CoordAndPoint maxp = mp.get("max");
		CoordAndPoint minp = mp.get("min");
		toolUtil.lonlat2mercator(maxp);
		toolUtil.lonlat2mercator(minp);
		
		List<Tile> tiles =  toolUtil.getAllTile(mp,level);
		System.out.println("level="+level + " tiles count=" + tiles.size());
		for(Tile tile:tiles){
			tile.draw(preFix, tragj, type, lines, color, TIlESIZE, TIlESIZE, true, weight);//colors[index%4]
			TileManager.currentCount++;
		}
	}
	
	public double getCurrentTileCount(){
		return currentCount;
	}
	
//	public void createTile(File kml, int level){
//	int weight =1;
//	List<ArrayList<CoordAndPoint>> lines = null;
//	
//	try {
//		lines = formatGPSFromKML.loadKML(kml);
//	} catch (Exception e) {
//		e.printStackTrace();
//	}
//	
//	ToolUtil toolUtil = new ToolUtil();
//	Map<String, CoordAndPoint> mp = toolUtil.getPosition(lines);
//	CoordAndPoint maxp = mp.get("max");
//	CoordAndPoint minp = mp.get("min");
//	toolUtil.lonlat2mercator(maxp);
//	toolUtil.lonlat2mercator(minp);
//	
//	List<Tile> tiles =  toolUtil.getAllTile(mp,level);
//	for(Tile tile:tiles){
//		tile.draw(lines, new Color(255,0, 0), TIlESIZE, TIlESIZE, false,weight);
//	}
//}
	
	
//	
//	public void createTileTraj(String type,List<ArrayList<CoordAndPoint>> lines, int level, Color color){
//		int weight = toolUtil.getWeight(level);
//		if(lines == null || lines.size() <= 0){
//			return;
//		}
//		
//		ToolUtil toolUtil = new ToolUtil();
//		Map<String, CoordAndPoint> mp = toolUtil.getPosition(lines);
//		CoordAndPoint maxp = mp.get("max");
//		CoordAndPoint minp = mp.get("min");
//		toolUtil.lonlat2mercator(maxp);
//		toolUtil.lonlat2mercator(minp);
//		
//		List<Tile> tiles =  toolUtil.getAllTile(mp,level);
//		System.out.println("level="+level + " tiles count=" + tiles.size());
//		for(Tile tile:tiles){
//			tile.draw(type, lines, color, TIlESIZE, TIlESIZE, true, weight);//colors[index%4]
//			TileManager.currentCount++;
//		}
//	}
	
	


	
	
//	public void createTile(boolean tragj, String type,File kml,int startLevel, int endLevel){
//    	List<ArrayList<CoordAndPoint>> lines = null;
//		try {
//			System.out.println("use file: " + kml.getAbsolutePath());
//	        
//			FormatGPSFromKML formatGPSFromKML = new FormatGPSFromKML();
//			lines = formatGPSFromKML.loadKML(kml);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return;
//		}
//		
//        int level = startLevel;
//        
//        long startTimeAll = System.currentTimeMillis();     
//        do{
//        	long startTime = System.currentTimeMillis();     
////        	System.out.println("start create tiles for level !" + level);
//        	createTile(tragj,type,lines,level, Color.red);
//           	long endTime = System.currentTimeMillis();   
//        	System.out.println("done create tiles for level=" + level + " use time:"+ (endTime - startTime) + "ms");
//        	level++;
//        }while(level <= endLevel);
//        System.out.println("done! use time:" + (System.currentTimeMillis() - startTimeAll) + "ms");
//        
//    }
	
//	public void generateImageFromKML(boolean tragj, String type,String path) {
//		File file = new File(path);
//		if (file.isDirectory()) {
//			for (File kml : file.listFiles()) {
//				if (kml.isFile()) {
//					createTile(tragj, type,kml, 0, 16);
//				}
//			}
//		}
//	}
	
//	public static void main(String args[]) {
//        System.out.println("starting...!");
//        TileManager tManager = new TileManager();
//        
//       // /Users/test/Downloads/Lane_Boundaries_20170425_195837
//        long startTimeAll = System.currentTimeMillis();  
//        String type="line";
//        String path="/home/test/Documents/sprint0428/kml/sources";
//        tManager.generateImageFromKML(type,path);
//        
//        
////        File file = new File("/Users/test/Downloads/Lane_Boundaries_20170425_195837");
////        if(file.isDirectory()){
////        	 TileManager f= new TileManager();
////        	for(File kml:file.listFiles()){
////        		if(kml.isFile()){
////        			f.createTile(kml, 0, 16);
////        		}
////        	}
////        }
////        
//        long endTime = System.currentTimeMillis();   
//    	System.out.println("all kmls use time:"+ (endTime - startTimeAll) + "ms");
//        
////        List<ArrayList<CoordAndPoint>> lines = null;
////		try {
//////			FomartGPSFromDB fomartGPSFromDB = new FomartGPSFromDB();
//////			fomartGPSFromDB.loadXML();
//////			
//////			CoordAndPoint cps = fomartGPSFromDB.getMaxcp();
//////	    	CoordAndPoint cpe = fomartGPSFromDB.getMincp();
//////	    	
//////	    	System.out.println(cps.toString());
//////	    	System.out.println(cpe.toString());
////			
////			FormatGPSFromKML formatGPSFromKML = new FormatGPSFromKML();
////			lines = formatGPSFromKML.loadKML(new File("/Users/test/Downloads/small_14.kml"));
////		} catch (Exception e) {
////			e.printStackTrace();
////			return;
////		}
////		
////        int level = 0;
////        TileManager f= new TileManager();
////        long startTimeAll = System.currentTimeMillis();     
////        do{
////        	long startTime = System.currentTimeMillis();     
//////        	System.out.println("start create tiles for level !" + level);
////        	f.createTile(lines,level);
////           	long endTime = System.currentTimeMillis();   
////        	System.out.println("done create tiles for level=" + level + " use time:"+ (endTime - startTime) + "ms");
////        	level++;
////        }while(level <= 19);
////        System.out.println("done! use time:" + (System.currentTimeMillis() - startTimeAll) + "ms");
//    } 
	
	
    

}
