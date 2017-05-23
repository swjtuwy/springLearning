package com.map.util;

//import java.util.List;

//import org.geotools.geometry.jts.JTSFactoryFinder;

//import com.map.model.CoordAndPoint;
//import com.vividsolutions.jts.geom.Geometry;
//import com.vividsolutions.jts.geom.GeometryFactory;
//import com.vividsolutions.jts.geom.LineString;
//import com.vividsolutions.jts.geom.Polygon;
//import com.vividsolutions.jts.io.ParseException;
//import com.vividsolutions.jts.io.WKTReader;

public class GeotoolsUtil {

//	private GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
//
//	public String intersectLineWithPoly(String line,String poly) throws ParseException {
//		WKTReader reader = new WKTReader(geometryFactory);
//		
//		LineString geometry = (LineString) reader.read(line);
//		
//		Polygon polygon = (Polygon) reader.read(poly);
//		 
//		Geometry interLine = geometry.intersection(polygon);
////		if (!interLine.toText().equals("GEOMETRYCOLLECTION EMPTY")) {
////			
////			System.out.println(interLine.toText());
////		}
//		return interLine.toText();
//	}
//	
//	
//	public String listPoint2LineStr(List<CoordAndPoint> line){
//		//LINESTRING(0 0, 0 1, 1 1, 1 0, 0 0)
//		if(line==null||line.size()<=0){
//			return null;
//		}
//		StringBuilder sb = new StringBuilder();
//		sb.append("LINESTRING(");
//		for (int i=0;i<line.size();i++) {
//			CoordAndPoint localPoint = line.get(i);
//			sb.append(localPoint.getX()+" ");
//			sb.append(localPoint.getY());
//			if (i!=line.size()-1) {
//				sb.append(", ");
//			}
//		}
//		sb.append(")");
//		
//		return sb.toString();
//		
//	}
//	
//	public String pointToPolyStr(CoordAndPoint p1, CoordAndPoint p2){
//		//"POLYGON((0 0, 10 0, 10 10, 0 10,0 0))"
//		if(p1==null||p2==null){
//			return null;
//		}
//		String poly = "POLYGON(("+p1.getX()+" "+p1.getY()+", "+p2.getX()+" "+p1.getY()
//				+", "+p2.getX()+" "+p2.getY()+", "+p1.getX()+" "+p2.getY()
//				+", "+p1.getX()+" "+p1.getY()+"))";
//		
//		return poly;
//	}

}
