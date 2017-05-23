package com.map.constant;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.TextUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.map.model.CoordAndPoint;
import com.map.util.HttpUtil;
import com.map.util.Response;



public class FomartGPSFromDB {
	private CoordAndPoint mincp;
	private CoordAndPoint maxcp;
	
	public CoordAndPoint getMincp() {
		return mincp;
	}

	public void setMincp(CoordAndPoint mincp) {
		this.mincp = mincp;
	}

	public CoordAndPoint getMaxcp() {
		return maxcp;
	}

	public List<ArrayList<CoordAndPoint>> loadXML() throws Exception {

		mincp = new CoordAndPoint();
		maxcp = new CoordAndPoint();
		
		mincp.setX(0);
		mincp.setY(0);
		
		maxcp.setX(0);
		maxcp.setY(0);
		
		SAXReader saxReader = new SAXReader();

		Document document = saxReader.read(new File("/Users/test/Downloads/small_14.kml"));

		// 获取根元素
		Element root = document.getRootElement();
		System.out.println("Root: " + root.getName());

		// 获取所有子元素
		List<Element> childList = root.elements();
		System.out.println("total child count: " + root.element("Document").elements().size());

		// 获取特定名称的子元素

		// root.element("kml").elements().size();
		// 获取名字为指定名称的第一个子元素
		Element MultiGeometry = root.element("Document").element("Placemark").element("MultiGeometry");

		System.out.println("迭代输出-----------------------");

		List<ArrayList<CoordAndPoint>> lines = new ArrayList<ArrayList<CoordAndPoint>>();
		// 迭代输出
		for (Iterator iter = MultiGeometry.elementIterator(); iter.hasNext();) {
			Element e = (Element) iter.next();
			// System.out.println(e.element("coordinates").getStringValue());
			ArrayList<CoordAndPoint> listOne = new ArrayList<CoordAndPoint>();
			String[] lanlangs = e.element("coordinates").getStringValue().split(System.lineSeparator());

			for (String item : lanlangs) {
				if (TextUtils.isEmpty(item))
					continue;
				CoordAndPoint cp = new CoordAndPoint();

				// String temp = item.replaceAll("[\\(]", "");
				// temp = temp.replaceAll("[\\)]", "");
				cp.setLongti(Double.parseDouble(item.split(",")[0]));
				cp.setLanti(Double.parseDouble(item.split(",")[1]));
				lonlat2mercator(cp);
				if(mincp.getX() == 0 && mincp.getY() == 0){
					mincp.setX(cp.getX());
					mincp.setY(cp.getY());
				}
				else{
					if(mincp.getX() > cp.getX()){
						mincp.setX(cp.getX());
					}
					
					if(  mincp.getY() > cp.getY()){
						mincp.setY(cp.getY());
					}
				}
				
				if(maxcp.getX() == 0 && maxcp.getY() == 0){
					maxcp.setX(cp.getX());
					maxcp.setY(cp.getY());
				}
				else{
					if(maxcp.getX() < cp.getX()){
						maxcp.setX(cp.getX());
					}
					
					if(  maxcp.getY() < cp.getY()){
						maxcp.setY(cp.getY());
					}
				}
				
				listOne.add(cp);
			}

			lines.add(listOne);
		}

		return lines;
	}

	private HttpUtil httpUtil = new HttpUtil();
	private static final Logger logger = LoggerFactory.getLogger(FomartGPSFromDB.class);

	public List<ArrayList<CoordAndPoint>> load(String url) {
		Response resp = httpUtil.doGet(url);

		if (!resp.getCode().equals(HttpStatus.OK))
			return null;
		// resp = handlerDBResult(resp.getData());
		// if (resp.getCode() != 200)
		// return null;
		return formatData(resp.getData());
	}

	public List<ArrayList<CoordAndPoint>> formatData(String jsonstr) {
		mincp = new CoordAndPoint();
		maxcp = new CoordAndPoint();

		mincp.setX(0);
		mincp.setY(0);

		maxcp.setX(0);
		maxcp.setY(0);

		List<ArrayList<CoordAndPoint>> lines = new ArrayList<ArrayList<CoordAndPoint>>();

		// locations

		JSONArray jsonarray;
		try {
			jsonarray = new JSONArray(jsonstr);

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
					lonlat2mercator(cp);

					if (mincp.getX() == 0 && mincp.getY() == 0) {
						mincp.setX(cp.getX());
						mincp.setY(cp.getY());
					} else {
						if (mincp.getX() > cp.getX()) {
							mincp.setX(cp.getX());
						}

						if (mincp.getY() > cp.getY()) {
							mincp.setY(cp.getY());
						}
					}

					if (maxcp.getX() == 0 && maxcp.getY() == 0) {
						maxcp.setX(cp.getX());
						maxcp.setY(cp.getY());
					} else {
						if (maxcp.getX() < cp.getX()) {
							maxcp.setX(cp.getX());
						}

						if (maxcp.getY() < cp.getY()) {
							maxcp.setY(cp.getY());
						}
					}

					listOne.add(cp);
				}

				lines.add(listOne);
			}

		} catch (Exception e) {
			String err = "Can't parse to a json string with: ";
			// logger.error(err);

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
