package hr.city.bikeroutes;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openiot.cupus.artefact.HashtablePublication;
import org.openiot.cupus.entity.publisher.Publisher;

import com.vividsolutions.jts.geom.Coordinate;

public class App {

	public static void main(String[] argv) throws FileNotFoundException {

		
		/**
		 * This is for connecting to eventos, and online broker;
		 */
		
		
		String configFile = "./system.config";

		Properties pubProps = new Properties();
		FileInputStream fileIn = new FileInputStream(configFile);
		try {
			pubProps.load(fileIn);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String url = pubProps.getProperty("api");
		if (url == null) {
			throw new NullPointerException("Api must be defined!");
		} else {
			url += System.currentTimeMillis();
		}

		String SERVER_IP = pubProps.getProperty("server_ip");

		if (SERVER_IP == null) {
			throw new NullPointerException("Server IP address must be defined!");
		}

		InputStream is = null;
		 
		// Initializing publisher on server side
		Publisher publisher = new Publisher("serverPublisher", SERVER_IP, 10000);
		publisher.connect();

		try {

			is = new URL(url).openStream();

			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

			StringBuilder sb = new StringBuilder();
			int cp;
			while ((cp = rd.read()) != -1) {
				sb.append((char) cp);
			}
			String jsonText = sb.toString();
			System.out.println(jsonText);

			JSONObject json = new JSONObject(jsonText);
			if (json.getBoolean("success")) {
				JSONArray data = json.getJSONArray("data");

				for (int i = 0; i < data.length(); i++) {
					JSONObject event = data.getJSONObject(i);
					JSONObject location = event.getJSONObject("Location");
					// System.out.println(location.toString());
					String lngStr = location.getString("lng");
					String latStr = location.getString("lat");
					if (lngStr == null || latStr == null) {
						continue;
					}

					Double lng = Double.parseDouble(lngStr);
					Double lat = Double.parseDouble(latStr);

					String address = location.getString("address");

					JSONObject eventObj = event.getJSONObject("Event");
					String eventName = eventObj.getString("name");

					Coordinate[] coordinates = new Coordinate[1];
					coordinates[0] = new Coordinate(lat, lng);

					HashtablePublication hb = new HashtablePublication(-1, System.currentTimeMillis(), coordinates);
					hb.setProperty("DataType", "Event");
					hb.setProperty("address", address);
					hb.setProperty("eventName", eventName);

					publisher.publish(hb);
				}
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
};