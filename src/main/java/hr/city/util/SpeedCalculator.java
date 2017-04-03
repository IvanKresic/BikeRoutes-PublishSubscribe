package hr.city.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ListMultimap;
import com.google.gson.Gson;

public class SpeedCalculator {

	
	/*************************************************
	 * Calculate speed between two geolocation points
	 ************************************************/

	public static double calculateSpeed(UserLocationModel userData_1, UserLocationModel userData_2) {
		
		try {
			double lat1, lat2, lon1, lon2, time1, time2;
			
			//Get data from given user
			lat1 = userData_1.getLatitude();
			lat2 = userData_2.getLatitude();
			lon1 = userData_1.getLongitude();
			lon2 = userData_2.getLongitude();
			time1 = userData_1.getTimestamp();
			time2 = userData_2.getTimestamp();

			//Calculate speed
			double distance = calculateDistance(lat1, lon1, lat2, lon2);
			double time = time2 - time1;
			double speed = distance / (time / 1000);
			double finalSpeed = (speed * 3600) / 1000;

			return Math.round(finalSpeed * 100.0) / 100.0;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	
	/*************************************************************
	 * Calculate average speed of all speeds in map for every edge
	 ************************************************************/
	
	public static Map avgSpeedOnEdges(ListMultimap multimap)
	{
		Map<Integer, Double> mapOfSpeedsByEdges = new HashMap<Integer, Double>();
		
		for(Object key : multimap.keySet())
		{
			List<Double> allSpeeds = multimap.get(key);
			int i = 0;
			double speed = 0;
			double avg =0;
			for(double s : allSpeeds)
			{
				speed += s;
				i++;
			}
			avg = speed / i;
			mapOfSpeedsByEdges.put((Integer) key, avg);
		}		
		return mapOfSpeedsByEdges;
	}
	

	/******************************************
	 *  Calculate avarage speed for every user
	 ******************************************/
	
	public static double calculateAvarageSpeedForUser(double speedFromDatabase, double CurrentSpeed)
	{
		double avarageSpeed = 0;
		
		return avarageSpeed;
	}
	
	
	private static String readUrl(String urlString) throws Exception {
		BufferedReader reader = null;
		try {
			URL url = new URL(urlString);
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuffer buffer = new StringBuffer();
			int read;
			char[] chars = new char[1024];
			while ((read = reader.read(chars)) != -1)
				buffer.append(chars, 0, read);

			return buffer.toString();
		} finally {
			if (reader != null)
				reader.close();
		}
	}

	
	/****************************************************
	 * Calculate distance between two geolocation points
	 ***************************************************/
	
	private static double calculateDistance(double lat1, double lon1,
			double lat2, double lon2) {

		// Convert degrees to radians
		lat1 = lat1 * Math.PI / 180.0;
		lon1 = lon1 * Math.PI / 180.0;

		lat2 = lat2 * Math.PI / 180.0;
		lon2 = lon2 * Math.PI / 180.0;

		// radius of earth in metres
		double r = 6378100;

		// P
		double rho1 = r * Math.cos(lat1);
		double z1 = r * Math.sin(lat1);
		double x1 = rho1 * Math.cos(lon1);
		double y1 = rho1 * Math.sin(lon1);

		// Q
		double rho2 = r * Math.cos(lat2);
		double z2 = r * Math.sin(lat2);
		double x2 = rho2 * Math.cos(lon2);
		double y2 = rho2 * Math.sin(lon2);

		// Dot product
		double dot = (x1 * x2 + y1 * y2 + z1 * z2);
		double cos_theta = dot / (r * r);

		double theta = Math.acos(cos_theta);

		// Distance in Metres
		return r * theta;
	}
}
