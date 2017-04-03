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
	
	int pathId1 = 1;
	double latFrom1 = 1.1;
	double lngFrom1 = 1.1;
	double latTo1 = 2.2;
	double lngTo1 = 1.1;
	
	int pathId2 = 2;
	double latFrom2 = 2.12;
	double lngFrom2 = 1.11;
	double latTo2 = 1.21;
	double lngTo2 = 3.13;
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
		for (int i = 0; i < 10; i++) {
			conn.insertIntoPopularRoute(pathId1, latFrom1, lngFrom1, latTo1, lngTo1);
		}
		for (int i = 0; i < 5; i++) {
			conn.insertIntoPopularRoute(pathId2, latFrom2, lngFrom2, latTo2, lngTo2);
		}
	}
}
