package hr.city.subscriptions;

import java.util.HashMap;
import java.util.UUID;

import org.openiot.cupus.artefact.HashtablePublication;
import org.openiot.cupus.artefact.Publication;
import org.openiot.cupus.artefact.Subscription;
import org.openiot.cupus.artefact.TripletSubscription;
import org.openiot.cupus.common.Triplet;
import org.openiot.cupus.common.enums.Operator;
import org.openiot.cupus.entity.subscriber.NotificationListener;
import org.openiot.cupus.entity.subscriber.Subscriber;

import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.PointList;
import com.vividsolutions.jts.geom.Coordinate;

import hr.city.util.DatabaseConnections;

public class UserDataSubscriber {

	private final static String SERVER_IP = "161.53.19.88";
	private static DatabaseConnections conn = new DatabaseConnections();

	public void setUserDataSubscriberAndSubscribe(String subscriberName, final GraphHopper hopper) {
		Subscriber subscriber = new Subscriber(subscriberName, SERVER_IP, 10000);

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
						double latTo = pl.getLat(pl.size() - 1);
						double lonTo = pl.getLon(pl.size() - 1);

						conn.insertIntoPopularRoute(path_id, latFrom, lonFrom, latTo, lonTo);
						conn.insertIntoUserRoutes(uuid, path_id, latFrom, lonFrom, latTo, lonTo);
						conn.insertAllDataIntodatabase(receivedData.get("UUID").toString(), c.x, c.y,
								Long.parseLong(timestamps[i]));
					}
				}
			}
			public void notify(UUID arg0, String arg1, Subscription arg2, boolean arg3) {
				// TODO Auto-generated method stub
			}
		});

		subscriber.connect();
		// define subscriptions for speed data

		TripletSubscription ts1 = new TripletSubscription(-1, System.currentTimeMillis());
		ts1.addPredicate(new Triplet("DataType", "User", Operator.EQUAL));

		// subscribe to subscriptions
		subscriber.subscribe(ts1);
	}
}
