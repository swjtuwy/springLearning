package com.map.model;

public class CoordAndPoint {

	private double lanti,longti,x,y;
	
	public CoordAndPoint(){
		
	}
	
	public CoordAndPoint(double lanti,double longti){
		this.lanti=lanti;
		this.longti=longti;
	}
	

	public double getLanti() {
		return lanti;
	}

	public void setLanti(double lanti) {
		this.lanti = lanti;
	}

	public double getLongti() {
		return longti;
	}

	public void setLongti(double langti) {
		this.longti = langti;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	public String toString(){
		return String.format("(%.14f,%.15f),(%f,%f)", this.lanti,this.longti,x,y);
	}
	
}
