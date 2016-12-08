package hr.city.publications;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;

import org.openiot.cupus.artefact.HashtablePublication;
import org.openiot.cupus.artefact.Publication;
import org.openiot.cupus.artefact.Subscription;
import org.openiot.cupus.artefact.TripletSubscription;
import org.openiot.cupus.common.Triplet;
import org.openiot.cupus.common.enums.Operator;
import org.openiot.cupus.entity.publisher.Publisher;
import org.openiot.cupus.entity.subscriber.NotificationListener;
import org.openiot.cupus.entity.subscriber.Subscriber;

import com.graphhopper.GraphHopper;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.util.BikeFlagEncoder;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.CmdArgs;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.PointList;
import com.vividsolutions.jts.geom.Coordinate;

import util.DatabaseConnections;

public class PublicationSubscriberAll {

	private final static String SERVER_IP = "161.53.19.88";
	private static String osmFile = "D:/Moje/TheProject/OSMosis/bin/vienna.osm";
	private static String graphDiskLocation = "D:/Moje/TheProject/viennaMaps";
	private static DatabaseConnections conn = new DatabaseConnections();

	private static GraphHopper hopper = new GraphHopper();
	private static GraphHopperOSM graphOsm = new GraphHopperOSM();
	

	public static void main(String[] argv) throws FileNotFoundException {

		//final LocationIndex index = hopper.getLocationIndex();
		graphOsm.setDataReaderFile(osmFile);
		graphOsm.setOSMFile(osmFile);
		graphOsm.setGraphHopperLocation(graphDiskLocation);
		graphOsm.setEncodingManager(new EncodingManager("bike"));
		graphOsm.setPreferredLanguage("3166-2");
		graphOsm.importOrLoad();
		
		hopper.setStoreOnFlush(true);
		hopper.setEncodingManager(new EncodingManager("bike"));
		hopper.setGraphHopperLocation(graphDiskLocation);
		
		//hopper.importOrLoad();
		//CmdArgs tmpArgs = CmdArgs.readFromConfigAndMerge(CmdArgs.read(argv), "config", "graphhopper.config");
        //tmpArgs.put("dataReader.file", osmFile);
		//System.out.println(tmpArgs.toString());
		//hopper.init(tmpArgs);


		FlagEncoder encoder = new BikeFlagEncoder();

		/*
		 * GraphHopperAPI gh = new GraphHopperWeb();
		 * gh.load("http://localhost");
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

		String SERVER_IP = pubProps.getProperty("server_ip");

		if (SERVER_IP == null) {
			throw new NullPointerException("Server IP address must be defined!");
		}

		//conn.ConnectToDatabase();

		/****************************************************
		 * Initializing subscriber for data from cyclists
		 ***************************************************/

		final Publisher publisher = new Publisher("myRoutesPublisher", SERVER_IP, 10000);

		Subscriber subscriber = new Subscriber("serverUserSubscriber", SERVER_IP, 10000);
		subscriber.setNotificationListener(new NotificationListener() {
			public void notify(UUID subscriberId, String subscriberName, Publication publication) {

				// UserLocationModel userModel= new UserLocationModel();

				HashtablePublication notification = (HashtablePublication) publication;
				HashMap<String, Object> receivedData = notification.getProperties();

				if (notification.getGeometry() != null) {
					Coordinate[] coordinates = notification.getGeometry().getCoordinates();

					String[] timestamps = ((String) receivedData.get("Timestamp")).split(",");

					for (int i = 0; i < coordinates.length; i++) {

						Coordinate c = coordinates[i];

						QueryResult qr = hopper.getLocationIndex().findClosest(c.x, c.y, EdgeFilter.ALL_EDGES);

						EdgeIteratorState edge = qr.getClosestEdge();
						PointList pl = edge.fetchWayGeometry(3);
						int path_id = edge.getEdge();
						String uuid = (String) receivedData.get("UUID");
						System.out.println(path_id);
						System.out.println(uuid);

						double latFrom = pl.getLat(0);
						double lonFrom = pl.getLon(0);
						double latTo;
						double lonTo;
						latTo = pl.getLat(pl.size() - 1);
						lonTo = pl.getLon(pl.size() - 1);

						// DataType MyRoutes
						Coordinate[] coordinatesArray = new Coordinate[2];
						coordinatesArray[0] = new Coordinate(latFrom, lonFrom);
						coordinatesArray[1] = new Coordinate(latTo, lonTo);
						HashtablePublication hp = new HashtablePublication(-1, System.currentTimeMillis(),
								coordinatesArray);
						hp.setProperty("DataType", "MyRoutes");

						publisher.publish(hp);

						conn.insertRoute(latFrom, lonFrom, latTo, lonTo, uuid, path_id);
						// System.out.println(latTo + "," + lonTo);

						String query = "INSERT INTO all_data (uuid, lat, lng, timestamp) VALUES('"
								+ receivedData.get("UUID") + "', " + c.x + ", " + c.y + ", "
								+ Long.parseLong(timestamps[i]) + ")";

						conn.InsertIntoDatabase(query);

					}
				}
			}

			public void notify(UUID arg0, String arg1, Subscription arg2, boolean arg3) {
				// TODO Auto-generated method stub

			}
		});

		Subscriber subscriber2 = new Subscriber("serverArduinoSubscriber", SERVER_IP, 10000);
		subscriber2.setNotificationListener(new NotificationListener() {
			public void notify(UUID subscriberId, String subscriberName, Publication publication) {

				HashtablePublication notification = (HashtablePublication) publication;
				HashMap<String, Object> receivedData = notification.getProperties();

				if (notification.getGeometry() != null) {
					Coordinate c = notification.getGeometry().getCoordinates()[0];

					String query = "INSERT INTO sensors_data (sensor_type, value, lat, lng, timestamp) VALUES('"
							+ receivedData.get("SensorType") + "', " + receivedData.get("Value") + ", " + c.x + ", "
							+ c.y + ", " + receivedData.get("Timestamp") + ")";

					conn.InsertIntoDatabase(query);
				}
			}

			public void notify(UUID arg0, String arg1, Subscription arg2, boolean arg3) {
				// TODO Auto-generated method stub

			}
		});

		// connect to the broker
		subscriber.connect();
		subscriber2.connect();

		// define subscriptions for speed data

		TripletSubscription ts1 = new TripletSubscription(-1, System.currentTimeMillis());
		ts1.addPredicate(new Triplet("DataType", "User", Operator.EQUAL));

		// define subscription for registration

		TripletSubscription ts2 = new TripletSubscription(-1, System.currentTimeMillis());
		ts2.addPredicate(new Triplet("DataType", "Arduino", Operator.EQUAL));

		// subscribe to subscriptions
		subscriber.subscribe(ts1);
		subscriber2.subscribe(ts2);

	}
	
};