package com.map.constant;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.util.TextUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.map.model.CoordAndPoint;

public class FormatGPSFromKML {
	
	public List<ArrayList<CoordAndPoint>>  loadKML(File kml) throws Exception{
		
		SAXReader saxReader = new SAXReader();

        //Document document = saxReader.read(new File("/Users/test2/Downloads/small_14.kml"));
		Document document = saxReader.read(kml);

        // 获取根元素
        Element root = document.getRootElement();
        //System.out.println("Root: " + root.getName());

        // 获取所有子元素
        List<Element> childList = root.elements();
        //System.out.println("total child count: " + root.element("Document").elements().size());

        // 获取特定名称的子元素
 
        //root.element("kml").elements().size();
        // 获取名字为指定名称的第一个子元素
        Element MultiGeometry  =  root
        		.element("Document")
        		.element("Placemark")
        		.element("MultiGeometry");

        System.out.println("迭代输出-----------------------");
        
        List<ArrayList<CoordAndPoint>> lines = new ArrayList<ArrayList<CoordAndPoint>> ();
        // 迭代输出
        for (Iterator iter = MultiGeometry.elementIterator(); iter.hasNext();)
        {
            Element e = (Element) iter.next();
            //System.out.println(e.element("coordinates").getStringValue());
            ArrayList<CoordAndPoint> listOne = new ArrayList<CoordAndPoint>();
            String[] lanlangs = e.element("coordinates").getStringValue().split(System.lineSeparator());
			for(String item: lanlangs){
				if(TextUtils.isEmpty(item))
					continue;
				CoordAndPoint cp = new CoordAndPoint();
				cp.setLongti(Double.parseDouble(item.split(",")[0]));
				cp.setLanti(Double.parseDouble(item.split(",")[1]));
				lonlat2mercator(cp);
				listOne.add(cp);
			}
			lines.add(listOne);
        }
        
        return lines;
	}
	
	public void lonlat2mercator(CoordAndPoint cp) {
		double x = cp.getLongti() * 20037508.34d / 180.0d; // getLanti
		double y = Math.log(Math.tan((90d + cp.getLanti()) * Math.PI / 360d)) / (Math.PI / 180d);
		y = y * 20037508.34d / 180d;
		cp.setX(x);
		cp.setY(y);
	}

	public void mercator2lonlat(CoordAndPoint cp) {

		double longti = cp.getX() / 20037508.34d * 180d;
		double lanti = cp.getY() / 20037508.34d * 180d;
		lanti = 180d / Math.PI * (2d * Math.atan(Math.exp(lanti * Math.PI / 180d)) - Math.PI / 2d);
		cp.setLongti(longti);
		cp.setLanti(lanti);
	}
}
