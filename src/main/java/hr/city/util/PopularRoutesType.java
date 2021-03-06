package hr.city.util;

import com.graphhopper.util.shapes.GHPoint;

public class PopularRoutesType {

	private GHPoint geoPointFrom = new GHPoint();
	private GHPoint geoPointTo = new GHPoint();
	private int category;
	
	public PopularRoutesType(double latFrom, double lonFrom, double latTo, double lonTo, int category)
	{
		this.geoPointFrom.lat = latFrom;
		this.geoPointFrom.lon = latTo;
		this.geoPointTo.lat = latTo;
		this.geoPointTo.lon = lonTo;
		this.category = category;
	}

	public GHPoint getGeoPointFrom() {
		return geoPointFrom;
	}

	public GHPoint getGeoPointTo() {
		return geoPointTo;
	}

	public int getCategory() {
		return category;
	}	
}
