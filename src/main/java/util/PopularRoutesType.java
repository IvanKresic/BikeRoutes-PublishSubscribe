package util;

import com.graphhopper.util.shapes.GHPoint;

public class PopularRoutesType {

	private GHPoint geoPointFrom = new GHPoint();
	private GHPoint geoPointTo = new GHPoint();
	private int counter;
	
	public PopularRoutesType(double latFrom, double lonFrom, double latTo, double lonTo, int counter)
	{
		this.geoPointFrom.lat = latFrom;
		this.geoPointFrom.lon = latTo;
		this.geoPointTo.lat = latTo;
		this.geoPointTo.lon = lonTo;
		this.counter = counter;
	}
}
