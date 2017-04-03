package BikeTracks.BikeTracks;

import org.junit.Test;

import hr.city.util.DatabaseConnections;
import junit.framework.TestCase;

public class WTestDatabaseInsertion extends TestCase {

	String uuid = "korisnik_1";
	int pathId = 12;
	double latFrom = 12.12;
	double lngFrom = 11.11;
	double latTo = 21.21;
	double lngTo = 13.13;
	private static DatabaseConnections conn = new DatabaseConnections();
	/*
	public void testConnectToDatabaseAndCreateRelations()
	{
		conn.ConnectToDatabase();
	}
*/

	@Test
	public void testInsertIntoUserDatabase() {
		conn.connect();
		for (int i = 0; i < 15; i++) {
			conn.insertIntoUserRoutes(uuid, pathId, latFrom, lngFrom, latTo, lngTo);
		}
	}

	@Test
	public void testInsertIntoPopularRoutesDatabase() {
		conn.connect();
		for (int i = 0; i < 15; i++) {
			conn.insertIntoPopularRoute(pathId, latFrom, lngFrom, latTo, lngTo);
		}
	}
}
