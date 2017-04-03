package hr.city.util;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnections {
	static Connection connection;
	Statement stmt;
	DatabaseMetaData dbm;
	ResultSet tables;

	public void connect() {
		try {
			connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/postgres", "postgres",
					"postgres");
			stmt = connection.createStatement();
			dbm = connection.getMetaData();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void ConnectToDatabase() {
		/**************************
		 * Connecting to database
		 **************************/
		try {
			Class.forName("org.postgresql.Driver");
			connect();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
			e1.printStackTrace();
		}

		try {
			// check if "employee" table is there
			tables = dbm.getTables(null, null, "sensors_data", null);
			if (tables.next()) {
				// Table exists
			} else {
				String sqlSensors = "CREATE TABLE IF NOT EXISTS sensors_data " + "(id SERIAL PRIMARY KEY ,"
						+ " sensor_type           TEXT    NOT NULL, " + " value            REAL     NOT NULL, "
						+ " lat        DOUBLE PRECISION   NOT NULL, " + " lng         DOUBLE PRECISION  NOT NULL, "
						+ " timestamp   BIGINT  NOT NULL)";

				stmt.executeUpdate(sqlSensors);
			}

			tables = dbm.getTables(null, null, "all_data", null);
			if (tables.next()) {
				// Table exists
			} else {
				String sqlUser = "CREATE TABLE IF NOT EXISTS all_data " + "("
						+ " uuid           text    NOT NULL, " + " lat        DOUBLE PRECISION   NOT NULL, "
						+ " lng         DOUBLE PRECISION  NOT NULL, " + " timestamp   BIGINT  NOT NULL)";

				stmt.executeUpdate(sqlUser);
			}

			tables = dbm.getTables(null, null, "user_routes", null);
			if (tables.next()) {
				// Table exists
			} else {
				String sqlUserRoutes = "CREATE TABLE IF NOT EXISTS user_routes("
						+ " uuid           text    NOT NULL, " + " path_id     INT    NOT NULL, "
						+ " lat_from        DOUBLE PRECISION   NOT NULL, "
						+ " lng_from         DOUBLE PRECISION  NOT NULL, "
						+ " lat_to        DOUBLE PRECISION   NOT NULL, "
						+ " lng_to         DOUBLE PRECISION  NOT NULL, " + " counter     INT  NOT NULL)";

				String sqlUpadteUserRoutesProcedure = "CREATE OR REPLACE FUNCTION upsert_user_routes(" + " uid text, "
						+ "path_id int, " + "latFrom DECIMAL, " + "lngFrom DECIMAL, " + "latTo DECIMAL, "
						+ "lngTo DECIMAL)" + "\nRETURNS VOID AS $$ " + "\nDECLARE " + "\nBEGIN "
						+ " \nUPDATE user_routes SET counter = counter + 1 " + "\nWHERE user_routes.uuid = uuid"
						+ "\nAND user_routes.lat_from = latFrom " + "\nAND user_routes.lng_from = lngFrom "
						+ "\nAND user_routes.lat_to = latTo " + "\nAND user_routes.lng_to = lngTo;" + " \nIF NOT FOUND THEN "
						+ "\n\tINSERT INTO user_routes values (uid, path_id, latFrom, lngFrom, latTo, lngTo, 1);"
						+ "\nEND IF;" + "\nEND;" + "\n$$ LANGUAGE 'plpgsql';";

				stmt.executeUpdate(sqlUserRoutes);
				stmt.executeUpdate(sqlUpadteUserRoutesProcedure);
			}

			tables = dbm.getTables(null, null, "popular_routes", null);
			if (tables.next()) {
				// Table exists
			} else {
				String sqlPopularRoutes = "CREATE TABLE IF NOT EXISTS popular_routes "
						+ "(path_id INT NOT NULL, " + " lat_from DOUBLE PRECISION NOT NULL, "
						+ " lng_from DOUBLE PRECISION  NOT NULL, "
						+ " lat_to DOUBLE PRECISION   NOT NULL, "
						+ " lng_to DOUBLE PRECISION  NOT NULL, " + " counter INT  NOT NULL,"
						+ " stored_at timestamptz NOT NULL DEFAULT now())";

				String updateProcedure = "CREATE OR REPLACE FUNCTION"
						+ " upsert_popular_routes(path_id int, latFrom DECIMAL, lngFrom DECIMAL, latTo DECIMAL, lngTo DECIMAL)"
						+ "\nRETURNS VOID AS $$" + " \nDECLARE " + " \nBEGIN "
						+ "\nUPDATE popular_routes SET counter = counter + 1, stored_at = now()"
						+ " \nWHERE popular_routes.lat_from = latFrom" + " \nAND popular_routes.lng_from = lngFrom"
						+ " \nAND popular_routes.lat_to = latTo" + " \nAND popular_routes.lng_to = lngTo;"
						+ " \nIF NOT FOUND THEN"
						+ " \n\tINSERT INTO popular_routes values (path_id, latFrom, lngFrom, latTo, lngTo, 1, now());"
						+ " \nEND IF;" + " \nEND;" + "\n$$ LANGUAGE 'plpgsql';";

				stmt.executeUpdate(sqlPopularRoutes);
				stmt.executeUpdate(updateProcedure);
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

	public void insertIntoDatabase(String query) {
		try {
			stmt.execute(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public void insertRoute(double latFrom, double lonFrom, double latTo, double lonTo, String uuid, int path_id) {
		String queryUser = "INSERT INTO user_routes (id, uuid, lat_from, lng_from, lat_to, lng_to) VALUES('" + uuid
				+ "', " + latFrom + ", " + lonFrom + ", " + latTo + ", " + lonTo + ")";

		insertIntoDatabase(queryUser);
	}

	public void insertIntoPopularRoute(int pathId, double latFrom, double lngFrom, double latTo, double lngTo) {
		String sqlCallPopularRoutesInsertionProcedure = "select upsert_popular_routes(?, ?, ?, ?, ?)";

		try {
			CallableStatement popularRoutesProcedure = connection.prepareCall(sqlCallPopularRoutesInsertionProcedure);
			popularRoutesProcedure.setInt(1, pathId);
			popularRoutesProcedure.setDouble(2, latFrom);
			popularRoutesProcedure.setDouble(3, lngFrom);
			popularRoutesProcedure.setDouble(4, latTo);
			popularRoutesProcedure.setDouble(5, lngTo);
			insertIntoDatabase(popularRoutesProcedure.toString());

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void insertIntoUserRoutes(String uuid, int pathId, double latFrom, double lngFrom, double latTo,
			double lngTo) {
		String sqlCallUserRoutesInsertionProcedure = "select upsert_user_routes(?, ?, ?, ?, ?, ?);";
		try {
			CallableStatement userRoutesProcedure = connection.prepareCall(sqlCallUserRoutesInsertionProcedure);
			userRoutesProcedure.setString(1, uuid);
			userRoutesProcedure.setInt(2, pathId);
			userRoutesProcedure.setDouble(3, latFrom);
			userRoutesProcedure.setDouble(4, lngFrom);
			userRoutesProcedure.setDouble(5, latTo);
			userRoutesProcedure.setDouble(6, lngTo);
			insertIntoDatabase(userRoutesProcedure.toString());

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void insertAllDataIntodatabase(String UUID, double latitude, double longitude, long timestamp) {
		String query = "INSERT INTO all_data (uuid, latFrom, lngFrom, latTo, lngTo, timestamp) VALUES('" + UUID + "', "
				+ latitude + "," + longitude + ", " + timestamp + ")";

		insertIntoDatabase(query);
	}

	public List<PopularRoutesType> getMostPopularRoutes() {
		List<PopularRoutesType> popularRoutesList = new ArrayList<PopularRoutesType>();
		PopularRoutesType popularRoute;
		try {
			int maximumCounter = getMaximumCounter();
			tables = dbm.getTables(null, null, "popular_routes", null);
			tables.next();
			while (!tables.isAfterLast()) {
				popularRoute = new PopularRoutesType(
						tables.getLong(2), 
						tables.getLong(3), 
						tables.getLong(4),
						tables.getLong(5), 
						resolveRouteCategory(maximumCounter, tables.getInt(6)));

				popularRoutesList.add(popularRoute);
				tables.next();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return popularRoutesList;
	}
	
	public List<UserRoutesType> getUserRoutes()
	{
		List<UserRoutesType> userRoutesList = new ArrayList<UserRoutesType>();
		UserRoutesType userRoutesType;
		try {
			tables = dbm.getTables(null, null, "user_routes", null);
			tables.next();
			while(!tables.isAfterLast())
			{
				
					userRoutesType = new UserRoutesType(tables.getString(1), 
							tables.getLong(3), 
							tables.getLong(4), 
							tables.getLong(5), 
							tables.getLong(6));
					userRoutesList.add(userRoutesType);	
					tables.next();
			}		
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userRoutesList;
	}

	private int resolveRouteCategory(int maxCounter, int routeCounter) {
		int categoryNumber;
		int category_3 = maxCounter / 3;
		int category_2 = category_3 * 2;

		if (routeCounter <= category_3) {
			categoryNumber = 3;
		} else if (routeCounter > category_3 && routeCounter <= category_2) {
			categoryNumber = 2;
		} else // category_1
			categoryNumber = 1;

		return categoryNumber;
	}

	/**
	 * Returns the maximum counter from popular_routes. Categorization is based
	 * on that number.
	 */
	private int getMaximumCounter() {
		int result = 0;
		ResultSet resultSet;
		String query = "SELECT counter FROM popular_routes ORDER BY counter DESC LIMIT 1";
		try {
			stmt.execute(query);
			resultSet = stmt.getResultSet();
			if (resultSet.next()) {
				result = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

}
