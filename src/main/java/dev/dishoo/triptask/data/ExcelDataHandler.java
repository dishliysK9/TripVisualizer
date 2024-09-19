package dev.dishoo.triptask.data;

import dev.dishoo.triptask.models.Point;
import dev.dishoo.triptask.models.Trip;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class ExcelDataHandler implements DataHandler {


    @Value("${app.inputFile}")
    private String filePath;

    // all trips for chosen driver, ordered by timestamp
    @Override
    public List<Trip> getTripsByDriverId(String driverId) {
        System.out.println("USING EXCEL HANDLER");
        List<Trip> trips = new ArrayList<>();

        // apache poi to open excel file
        try (FileInputStream fis = new FileInputStream(new File(filePath));
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            XSSFSheet sheet = workbook.getSheetAt(0); // sheet index 0 should be trips



            //iterate to rows to find trips by driverid
            /*for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // skip the header

                String driver = row.getCell(1).getStringCellValue(); //get the driver id

                //check if both id's match
                if (driver.equals(driverId)) {

                    // when they match extract data and create a trip object and add to list
                    String tripId = row.getCell(0).getStringCellValue();
                    String startTs = row.getCell(2).toString();
                    String endTs = row.getCell(3).toString();
                    Trip trip = new Trip(tripId, driverId, startTs, endTs);
                    trips.add(trip);
                }
            }
            */

            Iterator<Row> rowIterator = sheet.iterator();
            int i = 0;
            while (rowIterator.hasNext()) {

                Row row = rowIterator.next();
                if (row.getRowNum() == 0) continue;




                String driver = row.getCell(1).getStringCellValue();
                //System.out.println("DRIVER VALUE: " + driver);

                if (driver.equals(driverId)) {

                    String tripId = row.getCell(0).getStringCellValue();
                    String startTs = row.getCell(2).toString();
                    String endTs = row.getCell(3).toString();
                    Trip trip = new Trip(tripId, driver, startTs, endTs);
                    trips.add(trip);
                   // System.out.println(trips);
                    i++;
                }
            }
            if (i == 0) {
                System.out.println("NO TRIPS WITH THAT DRIVER ID, TRY AGAIN");

            }
            //sort by start timestamp
            trips.sort(Comparator.comparing(Trip::getStartTs));


        } catch (IOException e) {
            e.printStackTrace();
        }
        return trips;
    }


    // retrieve all points based on given trip id (which we get from the list of trips from the getTripsByDriverId)
    @Override
    public Map<String, List<Point>> getPointsByTripIds(List<String> tripIds) {
        Map<String, List<Point>> tripPointsMap = new HashMap<>();
        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet pointsSheet = workbook.getSheet("Sheet2"); //sheet 2 is points

            // iterate to find points for given trip IDs
            for (Row row : pointsSheet) {

                if (row.getRowNum() == 0) continue; // skip header

                //get trip id from 2nd column
                String tripId = row.getCell(1).getStringCellValue();

                // check if this trip id is in the provided list
                if (tripIds.contains(tripId)) {

                    // if it is just get the data
                    int x = (int) row.getCell(2).getNumericCellValue();
                    int y = (int) row.getCell(3).getNumericCellValue();
                    Point point = new Point(tripId, x, y);

                    // add the point to the corresponding list in the map
                    tripPointsMap.computeIfAbsent(tripId, k -> new ArrayList<>()).add(point);

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(tripPointsMap);
        return tripPointsMap;
    }
}
