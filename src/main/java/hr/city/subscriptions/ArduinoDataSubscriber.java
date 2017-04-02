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

import com.vividsolutions.jts.geom.Coordinate;

import util.DatabaseConnections;

public class ArduinoDataSubscriber {
	private final static String SERVER_IP = "161.53.19.88";
	private static DatabaseConnections conn = new DatabaseConnections();
	
	public void setArduinoDataSubscriberAndSubscribe()
	{
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

					conn.insertIntoDatabase(query);
				}
			}

			public void notify(UUID arg0, String arg1, Subscription arg2, boolean arg3) {
				// TODO Auto-generated method stub

			}
		});

		// connect to the broker
		
		subscriber2.connect();

		

		// define subscription for registration

		TripletSubscription ts2 = new TripletSubscription(-1, System.currentTimeMillis());
		ts2.addPredicate(new Triplet("DataType", "Arduino", Operator.EQUAL));

		
		subscriber2.subscribe(ts2);

	}

}
