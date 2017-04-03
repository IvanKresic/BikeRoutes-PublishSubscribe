package hr.city.publications;

import java.util.List;

import org.openiot.cupus.artefact.HashtablePublication;
import org.openiot.cupus.entity.publisher.Publisher;

import com.vividsolutions.jts.geom.Coordinate;

import hr.city.util.DatabaseConnections;
import hr.city.util.UserRoutesType;

public class UserRoutesPublisher {

	private final static String SERVER_IP = "161.53.19.88";
	final Publisher userDataPublisher = new Publisher("userDataPublisher", SERVER_IP, 10000);
	private static DatabaseConnections conn = new DatabaseConnections();
	HashtablePublication userDataPublication;

	// Create and fill userDataPublisher
	public void publishUserDataPublication() {
		conn.ConnectToDatabase();
		List<UserRoutesType> userRoutes = conn.getUserRoutes();
		for (UserRoutesType userRoutesType : userRoutes) {
			// DataType MyRoutes
			Coordinate[] coordinatesArray = new Coordinate[2];
			coordinatesArray[0] = new Coordinate(userRoutesType.getGeoPointFrom().lat,
					userRoutesType.getGeoPointFrom().lon);
			coordinatesArray[1] = new Coordinate(userRoutesType.getGeoPointTo().lat,
					userRoutesType.getGeoPointTo().lon);
			userDataPublication = new HashtablePublication(-1, System.currentTimeMillis(), coordinatesArray);
			userDataPublication.setProperty("UUID", userRoutesType.getUUID());
			userDataPublication.setProperty("DataType", "MyRoutes");
			publishUserData(userDataPublication);
		}
	}

	// Publish publishUserData
	private void publishUserData(HashtablePublication hp) {
		userDataPublisher.publish(hp);
	}

}
