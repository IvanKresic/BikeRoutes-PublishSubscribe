package util;

import com.graphhopper.util.shapes.GHPoint;

public class UserRoutesType {

	private String UUID;
	private GHPoint geoPointFrom = new GHPoint();
	private GHPoint geoPointTo = new GHPoint();
	
	public UserRoutesType(String uuid, double latFrom, double lonFrom, double latTo, double lonTo)
	{
		this.UUID = uuid;
		this.geoPointFrom.lat = latFrom;
		this.geoPointFrom.lon = latTo;
		this.geoPointTo.lat = latTo;
		this.geoPointTo.lon = lonTo;
	}

	public String getUUID() {
		return UUID;
	}

	public GHPoint getGeoPointFrom() {
		return geoPointFrom;
	}

	public GHPoint getGeoPointTo() {
		return geoPointTo;
	}
}
