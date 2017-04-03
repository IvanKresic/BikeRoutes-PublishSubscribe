package hr.city.bikeroutes;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.graphhopper.GraphHopper;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.util.BikeFlagEncoder;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.routing.util.FlagEncoder;

import hr.city.publications.MostPopularRoutesPublisher;
import hr.city.publications.UserRoutesPublisher;
import hr.city.subscriptions.UserDataSubscriber;
import hr.city.util.DatabaseConnections;

public class BikeRoutesApp {

	private final static String SERVER_IP = "161.53.19.88";
	private static String osmFile = "D:/Moje/TheProject/OSMosis/bin/croatia.osm";
	private static String graphDiskLocation = "D:/Moje/TheProject/croatiaMaps";
	private static DatabaseConnections conn = new DatabaseConnections();

	private static GraphHopper hopper = new GraphHopper();
	private static GraphHopperOSM graphOsm = new GraphHopperOSM();

	public static void main(String[] argv) throws FileNotFoundException {

		// final LocationIndex index = hopper.getLocationIndex();
		graphOsm.setDataReaderFile(osmFile);
		graphOsm.setOSMFile(osmFile);
		graphOsm.setGraphHopperLocation(graphDiskLocation);
		graphOsm.setEncodingManager(new EncodingManager("bike"));
		graphOsm.setPreferredLanguage("3166-2");
		graphOsm.importOrLoad();
		
		hopper.setStoreOnFlush(true);
		hopper.setEncodingManager(new EncodingManager("bike"));
		hopper.setGraphHopperLocation(graphDiskLocation);

		FlagEncoder encoder = new BikeFlagEncoder();
		String configFile = "./system.config";

		Properties pubProps = new Properties();
		FileInputStream fileIn = new FileInputStream(configFile);
		try {
			pubProps.load(fileIn);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		conn.ConnectToDatabase();
		//String SERVER_IP = pubProps.getProperty("server_ip");

		if (SERVER_IP == null) {
			throw new NullPointerException("Server IP address must be defined!");
		}
		
		/****************************************************
		 * Initializing subscriber for data from cyclists
		 ***************************************************/
		UserDataSubscriber userDataSubscriber = new UserDataSubscriber();
		userDataSubscriber.setUserDataSubscriberAndSubscribe("serverUserSubscriber", hopper);
		
		//Arduino subscriber exists, but not used currently
		
		/**
		 * Start publications threads
		 */
		MostPopularRoutesPublisher mostPopularRoutesPublisher = new MostPopularRoutesPublisher();
		UserRoutesPublisher userRoutesPublisher = new UserRoutesPublisher();
		
		PublishMostPopularRoutesThread t1 = new PublishMostPopularRoutesThread(mostPopularRoutesPublisher);
		PublishUserDataThread t2 = new PublishUserDataThread(userRoutesPublisher);
		
		t1.start();
		t2.start();
	}
};