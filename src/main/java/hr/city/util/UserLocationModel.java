package hr.city.util;
import com.google.gson.annotations.SerializedName;

public class UserLocationModel {

	@SerializedName("DeviceID")
	private String deviceId;
	@SerializedName("LONGITUDE")
	private double longitude;
	@SerializedName("LATITUDE")
	private double latitude;
	@SerializedName("TIMESTAMP")
	private double timestamp;
	
	public void setUID(String uid)
	{
		this.deviceId = uid;
	}
	
	public String getUID()
	{
		return deviceId;
	}
	
	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(double timestamp) {
		this.timestamp = timestamp;
	}	
	
}