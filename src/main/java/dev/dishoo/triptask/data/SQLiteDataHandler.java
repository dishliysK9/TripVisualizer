package dev.dishoo.triptask.data;

import dev.dishoo.triptask.models.Point;
import dev.dishoo.triptask.models.Trip;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;
import java.sql.*;
import java.util.*;

public class SQLiteDataHandler implements DataHandler {

    @Value("${app.inputFile}")
    private String filePath;



    public SQLiteDataHandler() {
        try {
            // Load SQLite JDBC driver (optional, but ensures driver is loaded)
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite Driver could not be loaded XXXxxxxXXXXXX");
        }
    }

    @Override
    public List<Trip> getTripsByDriverId(String driverId) {
        System.out.println("USING SQL HANDLER");
        // Initialize an empty list to store the Trip objects
        List<Trip> trips = new ArrayList<>();

        // Try-with-resources block to automatically close the connection after usage
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + filePath)) {
            // SQL query to fetch trips for the specified driver, ordered by START_TS
            String tripsQuery = "SELECT * FROM TRIPS WHERE DRIVER_ID = ? ORDER BY START_TS";

            // Prepare the SQL query to avoid SQL injection and handle the dynamic driver ID
            try (PreparedStatement pstmt = conn.prepareStatement(tripsQuery)) {
                // Set the driverId as the first parameter in the SQL query
                pstmt.setString(1, driverId);

                // Execute the query and obtain the result set
                ResultSet rs = pstmt.executeQuery();

                // Iterate through the result set (each row corresponds to a trip)
                while (rs.next()) {
                    // Retrieve the TRIP_ID, START_TS, and END_TS values from the result
                    String tripId = rs.getString("TRIP_ID");
                    String startTs = rs.getString("START_TS");
                    String endTs = rs.getString("END_TS");

                    // Create a new Trip object and add it to the list
                    Trip trip = new Trip(tripId, driverId, startTs, endTs);
                    trips.add(trip);
                }
            }
        } catch (SQLException e) {
            // Handle any SQL exceptions that occur during query execution
            e.printStackTrace();
        }

        // Return the list of trips after the query completes
        return trips;
    }


    @Override
    public Map<String, List<Point>> getPointsByTripIds(List<String> tripIds) {
        // Initialize an empty map to store the points associated with each trip
        Map<String, List<Point>> tripPointsMap = new HashMap<>();

        // Try-with-resources block to automatically close the connection after usage
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + filePath)) {
            // Build the SQL query dynamically with the appropriate number of placeholders (?)
            String pointsQuery = "SELECT * FROM POINTS WHERE TRIP_ID IN (" +
                    String.join(",", Collections.nCopies(tripIds.size(), "?")) + ")";

            // Prepare the SQL query, dynamically injecting the number of placeholders for trip IDs
            try (PreparedStatement pstmt = conn.prepareStatement(pointsQuery)) {
                // Set each trip ID in the prepared statement
                for (int i = 0; i < tripIds.size(); i++) {
                    pstmt.setString(i + 1, tripIds.get(i));
                }

                // Execute the query and obtain the result set
                ResultSet rs = pstmt.executeQuery();

                // Iterate through the result set (each row corresponds to a point)
                while (rs.next()) {
                    // Retrieve the TRIP_ID, X, and Y values from the result
                    String tripId = rs.getString("TRIP_ID");
                    int x = rs.getInt("X");
                    int y = rs.getInt("Y");

                    // Create a new Point object
                    Point point = new Point(tripId, x, y);

                    // Add the point to the map, associating it with the correct trip ID
                    tripPointsMap.computeIfAbsent(tripId, k -> new ArrayList<>()).add(point);
                }
            }
        } catch (SQLException e) {
            // Handle any SQL exceptions that occur during query execution
            e.printStackTrace();
        }

        // Return the map of trip IDs to their associated points after the query completes
        return tripPointsMap;
    }

}
