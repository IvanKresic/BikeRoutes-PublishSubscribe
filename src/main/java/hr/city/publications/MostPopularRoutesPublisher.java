package hr.city.publications;

import org.openiot.cupus.artefact.HashtablePublication;
import org.openiot.cupus.entity.publisher.Publisher;

import com.vividsolutions.jts.geom.Coordinate;

import hr.city.util.DatabaseConnections;
import hr.city.util.PopularRoutesType;

public class MostPopularRoutesPublisher {
	private final static String SERVER_IP = "161.53.19.88";
	final Publisher allUsersPpublisher = new Publisher("allUsersPpublisher", SERVER_IP, 10000);
	private static DatabaseConnections conn = new DatabaseConnections();
	HashtablePublication mostPopularRoutesPublication;

	// Create and fill userDataPublisher
	public void publishMostPopularRoutes() {
		conn.connect();
		for (PopularRoutesType popularRoutesType : conn.getMostPopularRoutes()) {
			Coordinate[] coordinatesArray = new Coordinate[2];
			coordinatesArray[0] = new Coordinate(popularRoutesType.getGeoPointFrom().lat,
					popularRoutesType.getGeoPointFrom().lon);
			coordinatesArray[1] = new Coordinate(popularRoutesType.getGeoPointTo().lat,
					popularRoutesType.getGeoPointTo().lon);
			mostPopularRoutesPublication = new HashtablePublication(-1, System.currentTimeMillis(), coordinatesArray);
			mostPopularRoutesPublication.setProperty("Category", popularRoutesType.getCategory());
			mostPopularRoutesPublication.setProperty("DataType", "MostPopularRoutes");
			publishMostPopularroutesData(mostPopularRoutesPublication);
		}
	}

	// Publish publishUserData
	private void publishMostPopularroutesData(HashtablePublication hp) {
		allUsersPpublisher.publish(hp);
	}

}
