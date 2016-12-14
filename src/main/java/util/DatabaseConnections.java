package util;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.openiot.cupus.artefact.HashtablePublication;

public class DatabaseConnections {
	static Connection connection = null;
	Statement statement;
	ResultSet tables;
	DatabaseMetaData dbm;
	
	public void ConnectToDatabase()
	{
		/**************************
		 *  Connecting to database
		 **************************/
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			System.out.println("Where is your PostgreSQL JDBC Driver? "
					+ "Include in your library path!");
			e1.printStackTrace();
		}
		
		try {
			connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/postgres", "postgres",
					"postgres");
			
			Statement stmt = connection.createStatement();

			DatabaseMetaData dbm = connection.getMetaData();
			// check if "employee" table is there
			ResultSet tables = dbm.getTables(null, null, "sensors_data", null);
			if (tables.next()) {
				// Table exists
			}
			else {
				String sqlSensors = "CREATE TABLE sensors_data " +
	                    "(id SERIAL PRIMARY KEY ," +
	                    " sensor_type           TEXT    NOT NULL, " +
	                    " value            REAL     NOT NULL, " +
	                    " lat        DOUBLE PRECISION   NOT NULL, " +
	                    " lng         DOUBLE PRECISION  NOT NULL, " +
	                    " timestamp   BIGINT  NOT NULL)";
			
				stmt.executeUpdate(sqlSensors);
			}

		
			tables = dbm.getTables(null, null, "all_data", null);
			if (tables.next()) {
				// Table exists
			}
			else {
				String sqlUser = "CREATE TABLE all_data " +
		                "(id SERIAL PRIMARY KEY ," +
		                " uuid           text    NOT NULL, " +
		                " lat        DOUBLE PRECISION   NOT NULL, " +
		                " lng         DOUBLE PRECISION  NOT NULL, " +
		                " timestamp   BIGINT  NOT NULL)";
					
				stmt.executeUpdate(sqlUser);
			}
			
			tables = dbm.getTables(null, null, "user_routes", null);
			if (tables.next()) {
				// Table exists
			}
			else {
				String sqlUserRoutes = "CREATE TABLE user_routes " +
		                "(id SERIAL PRIMARY KEY ," +
		                " uuid           text    NOT NULL, " +
		                " lat_from        DOUBLE PRECISION   NOT NULL, " +
		                " lng_from         DOUBLE PRECISION  NOT NULL, " +
		                " lat_to        DOUBLE PRECISION   NOT NULL, " +
		                " lng_to         DOUBLE PRECISION  NOT NULL)";
					
				stmt.executeUpdate(sqlUserRoutes);
			}
			
			tables = dbm.getTables(null, null, "popular_routes", null);
			if (tables.next()) {
				// Table exists
			}
			else {
				String sqlPopularRoutes = "CREATE TABLE popular_routes " +
		                "(id SERIAL PRIMARY KEY ," +
		                " path_id     INT    NOT NULL, " +
		                " lat_from        DOUBLE PRECISION   NOT NULL, " +
		                " lng_from         DOUBLE PRECISION  NOT NULL, " +
		                " lat_to        DOUBLE PRECISION   NOT NULL, " +
		                " lng_to         DOUBLE PRECISION  NOT NULL, " +
		                " counter     INT  NOT NULL)";
					
				stmt.executeUpdate(sqlPopularRoutes);
			}
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		if (connection != null) {
			System.out.println("You made it bro! You are connected to the database!");
		} else {
			System.out.println("Failed to make connection!");
		}
	}
	
	public void InsertIntoDatabase(String query)
	{
		try {
			Statement statement = connection.createStatement();
			
			statement.execute(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
		}		
	}

	public void insertRoute(double latFrom, double lonFrom, double latTo, double lonTo, String uuid, int path_id) {
		String queryUser = "INSERT INTO user_routes (id, uuid, lat_from, lng_from, lat_to, lng_to) VALUES('"+uuid +"', "+ latFrom +
				", "+lonFrom +", "+latTo +", "+lonTo +")";
		
		InsertIntoDatabase(queryUser);
		
		/*
		String queryPopular = "INSERT INTO popular_routes (path_id, lat_from, lng_from, lat_to, lng_to, counter) VALUES('"+uuid +"', "+ latFrom +
				", "+lonFrom +", "+latTo +", "+lonTo +", "+counter +")";
		*/
	}
	
	public void insertIntoPopularRoute(int pathId, double latFrom, double lngFrom, double latTo, double lngTo)
	{
		String sqlCallPopularRoutesInsertionProcedure = "call upsert_popular_routes(?, ?, ?, ?, ?)";
//		String queryGetPopularRouteIfExists = "SELECT counter FROM popular_routes"+
//				" WHERE lat_from = " + latFrom
//				+"AND lng_from = " + lngFrom
//				+"AND lat_to = " + latTo
//				+"AND lng_to = " + lngTo;
		
		try {
			CallableStatement popularRoutesProcedure = connection.prepareCall(sqlCallPopularRoutesInsertionProcedure);
			popularRoutesProcedure.setInt(1, pathId);
			popularRoutesProcedure.setDouble(2, latFrom);
			popularRoutesProcedure.setDouble(3, lngFrom);
			popularRoutesProcedure.setDouble(4, latTo);
			popularRoutesProcedure.setDouble(5, lngTo);
			popularRoutesProcedure.execute();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Popular routes procedure is not working correctly");
			e.printStackTrace();			
		}
		
		/**
		 * TODO
		 * Ovo je najbolje riješiti pohranjenom procedurom koja će raditi update polja counter
		 * ako zapis već postoji, ili insert ako zapis ne postoji.
		 */
	}	
	
	public void getMostPopularRoutes()
	{
		try {
			tables = dbm.getTables(null, null, "popular_routes", null);
			while(!tables.isAfterLast())
			{				
				tables.next();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
