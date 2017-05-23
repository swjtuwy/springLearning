package com.map.model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.map.constant.Const;
import com.map.constant.Const.MapConstants;
import com.map.constant.Const.folder;
import com.map.util.FileUtil;
import com.map.util.ToolUtil;

public class Tile {
	private static String IMAGEBASE = Const.folder.imagePath;
	private int x, y, zoom;
	private CoordAndPoint maxCp, minCp;
	private double length;
	
	private ToolUtil toolUtil = new ToolUtil();
	
	public int getX() {
		return x;
	}

	public Tile(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Tile(int x, int y, int zoom, double length) {
		this.x = x;
		this.y = y;
		this.zoom= zoom;
		this.length= length;
		initMaxAndMinCp();
	}
	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	private void initMaxAndMinCp() {
		double minX = MapConstants.minLng + (this.length * this.x);
		double minY = MapConstants.maxLat - (this.length * (this.y + 1));
		double maxX = minX + this.length;
		double maxY = minY + this.length;
		
		CoordAndPoint mincoorAndPoint = new CoordAndPoint();
		CoordAndPoint maxcoorAndPoint = new CoordAndPoint();
		
		mincoorAndPoint.setX(minX);
		mincoorAndPoint.setY(minY);
		maxcoorAndPoint.setX(maxX);
		maxcoorAndPoint.setY(maxY);
		
		toolUtil.mercator2lonlat(mincoorAndPoint);
		toolUtil.mercator2lonlat(maxcoorAndPoint);
		this.setMinCp(mincoorAndPoint);
		this.setMaxCp(maxcoorAndPoint);
	}

	public int getZoom() {
		return zoom;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public void setZoom(int zoom) {
		this.zoom = zoom;
	}

	public CoordAndPoint getMaxCp() {
		return maxCp;
	}

	public void setMaxCp(CoordAndPoint maxCp) {
		this.maxCp = maxCp;
	}

	public CoordAndPoint getMinCp() {
		return minCp;
	}

	public void setMinCp(CoordAndPoint minCp) {
		this.minCp = minCp;
	}
	public Tile(int x,int y, int zoom){
		this.x =x;
		this.y =y;
		this.zoom =zoom;
	}
	public CoordAndPoint getCenter() {
		CoordAndPoint cp = new CoordAndPoint();
		cp.setLanti((maxCp.getLanti() + minCp.getLanti()) /2d);
		cp.setLongti((maxCp.getLongti() + minCp.getLongti()) /2d);
		cp.setX((maxCp.getX() + minCp.getX()) /2d);
		cp.setY((maxCp.getY() + minCp.getY()) /2d);
		return cp;
	}
	
	public double getRadius() {
		return ((maxCp.getX() + minCp.getX()) /2d);
	}
	
	public boolean draw(String preFix, boolean tragj, String type, List<ArrayList<CoordAndPoint>> lines, Color color, int width, int height, boolean overlying, int weight) {
//		if (tragj) {
//			if(FileUtil.exists(String.format("%s/%s/%s/%s/", IMAGEBASE, preFix, type, zoom))){
//				FileUtil.delDir(String.format("%s/%s/%s/%s/", IMAGEBASE, preFix, type, zoom));
//			}
//		}
		if (!FileUtil.exists(String.format("%s/%s/%s/%s/%s/", IMAGEBASE, preFix, type, zoom, x))) {
			initfolder(preFix, type);
		}

		CoordAndPoint cps = maxCp;
		CoordAndPoint cpe = minCp;

		// 创建BufferedImage对象
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		// 获取Graphics2D
		Graphics2D g2d = image.createGraphics();

		// ---------- 增加下面的代码使得背景透明 -----------------
		image = g2d.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
		g2d.dispose();
		g2d = image.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);  
		// ---------- 背景透明代码结束 -----------------

		String pngPath = getImagePath(preFix,type);
		if (overlying && FileUtil.exists(pngPath)) {
//			Toolkit toolkit = Toolkit.getDefaultToolkit();
//			Image png = toolkit.getImage(pngPath);
			
			Image png=null;
			try {
				File _filebiao = new File(pngPath);
				png = ImageIO.read(_filebiao);
			} catch (IOException e) {
				e.printStackTrace();
			}
			   
			g2d.drawImage(png, 0, 0, width, height, null);
		}

		// 画图
		g2d.setColor(color);
		g2d.setStroke(new BasicStroke(weight));
		if(lines==null||lines.size()==0){
			System.err.println("no need draw...");
			return true;
		}

		for (ArrayList<CoordAndPoint> line : lines) {
			
//			List<CoordAndPoint> plist = toolUtil.intersectLineWithPoly(line, cps, cpe);
//			if(plist == null || plist.size() == 0){
//				//System.out.println("skip.........");
//				continue;
//			}
			
 
//			if(!toolUtil.checkIntersect(line, cps, cpe)){
//				//System.out.println("skip.........");
//				continue;
//			}
			
			draw(g2d,line,cps,cpe,width,height);
		}

		// 释放对象
		g2d.dispose();
		// 保存文件
		try {
			ImageIO.write(image, "png", new File(pngPath));
		} catch (IOException e) {
			e.printStackTrace();
			
			return false;
		}

		return true;
	}
	
	private void draw(Graphics2D g2d,List<CoordAndPoint> plist, CoordAndPoint cps,CoordAndPoint cpe, int width, int height) {

		int psLen = plist.size();
		int ox[] = new int[psLen];
		int oy[] = new int[psLen];
		for (int i = 0; i < psLen; i++) {
			ox[i] = meauscan(cps.getX(), cpe.getX(), width, 0, plist.get(i).getX());
			oy[i] = meauscan(cps.getY(), cpe.getY(), 0, height, plist.get(i).getY());
		}
		g2d.drawPolyline(ox, oy, psLen);
	}

	public String getImagePath(String prefix, String type) {

		return String.format("%s/%s/%s/%s/%s/%s.png", IMAGEBASE, prefix, type, zoom, x, y);
	}

	private boolean initfolder(String preFix, String type) {
		if (!FileUtil.createFolder(IMAGEBASE)) {
			return false;
		}

		if (!FileUtil.createFolder(String.format("%s/%s/", IMAGEBASE, preFix))) {
			return false;
		}
		
		if (!FileUtil.createFolder(String.format("%s/%s/%s/", IMAGEBASE, preFix, type))) {
			return false;
		}
		
		if (!FileUtil.createFolder(String.format("%s/%s/%s/%s/", IMAGEBASE, preFix, type, zoom))) {
			return false;
		}

		if (!FileUtil.createFolder(String.format("%s/%s/%s/%s/%s/", IMAGEBASE, preFix, type, zoom, x))) {
			return false;
		}

		return true;
	}

	
	@SuppressWarnings("unused")
	private void Draw(List<ArrayList<CoordAndPoint>> lines, CoordAndPoint cps, CoordAndPoint cpe, String pngPath)
			throws Exception {

		System.out.println(cps.toString());
		System.out.println(cpe.toString());

		double x2y = Math.abs(cps.getX() - cpe.getX()) / Math.abs(cps.getY() - cpe.getY());

		int width = 4400;
		int height = (int) (((double) width) / x2y);
		// 创建BufferedImage对象
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		// 获取Graphics2D
		Graphics2D g2d = image.createGraphics();

		// ---------- 增加下面的代码使得背景透明 -----------------
		image = g2d.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
		g2d.dispose();
		g2d = image.createGraphics();
		// ---------- 背景透明代码结束 -----------------

		// 画图
		g2d.setColor(new Color(255, 0, 0));
		g2d.setStroke(new BasicStroke(1));

		for (ArrayList<CoordAndPoint> line : lines) {
			int psLen = line.size();
			int ox[] = new int[psLen];
			int oy[] = new int[psLen];
			for (int i = 0; i < psLen; i++) {
				ox[i] = meauscan(cps.getX(), cpe.getX(), width, 0, line.get(i).getX());
				oy[i] = meauscan(cps.getY(), cpe.getY(), 0, height, line.get(i).getY());
			}
			g2d.drawPolyline(ox, oy, psLen);
		}

		// 释放对象
		g2d.dispose();
		// 保存文件
		ImageIO.write(image, "png", new File(pngPath));
	}

	private int meauscan(double minV, double maxV, int start, int end, double current) {

		double len = maxV - minV;
		double scan = (current - minV) / len;
		double value = (double) start + scan * (double) (end - start);
		return (int) value;
	}

}
