package dev.dishoo.triptask.gui;

import dev.dishoo.triptask.TriptaskApplication;
import dev.dishoo.triptask.models.Point;
import dev.dishoo.triptask.models.Trip;
import dev.dishoo.triptask.service.TripService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;



import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class TripViewerJavaFXApp extends Application {

    @Override
    public void init() throws Exception {
        // Retrieve TripService from the Spring context
        ApplicationContext context = TriptaskApplication.getContext();
        outputDir = TriptaskApplication.getOutputDir();
        if (context != null) {
            tripService = context.getBean(TripService.class);
        } else {
            throw new RuntimeException("Spring ApplicationContext is not initialized");
        }
    }
    public TripService tripService;

    private TextField driverIdField;
    private ComboBox<String> outputTypeDropdown;
    private TextField outputFileNameField;

    private String outputDir;


    // automatically called when the application is launched
    @Override
    public void start(Stage stage) throws Exception {
        // Access the Spring context and get the TripService bean
        tripService = TriptaskApplication.getContext().getBean(TripService.class);

        if( tripService != null ) {
            // title of the gui window
            stage.setTitle("Trip Viewer");

            //create the "label" of the driverId, which is actually what's shown (the text
            //above the actual field where we type
            Label driverIdLabel = new Label("Driver ID:");
            // the actual field where we enter the driver id
            driverIdField = new TextField();

            // label for outputType
            Label outputTypeLabel = new Label("Output type:");
            // drop down menu for selecting different options like .txt and png
            outputTypeDropdown = new ComboBox<>();
            outputTypeDropdown.getItems().addAll(".txt", "PNG");

            // label for outputFileName
            Label outputFileNameLabel = new Label("Output file name:");
            outputFileNameField = new TextField();

            // create the button
            Button generateButton = new Button("Generate map");

            // set all the labels, text fields, and the button into a VERTICAL layout (as shown in the task instruction)
            VBox layout = new VBox(10); // 10 is the spacing between elements
            layout.getChildren().addAll(
                    driverIdLabel, driverIdField,
                    outputTypeLabel, outputTypeDropdown,
                    outputFileNameLabel, outputFileNameField,
                    generateButton
            );

            // set the action of the button
            generateButton.setOnAction(event -> handleButtonClick());

            //scene with the layout and sets the size to 300 pixels wide by 250 pixels high
            Scene scene = new Scene(layout, 300, 250);
            //set the scene for the window
            stage.setScene(scene);
            //display the stage with all elements
            stage.show();
        } else {
            throw new RuntimeException("Trip Service bean is not initialized");
        }

    }

    private void handleButtonClick() {

        // connect the user input from the gui to java string objects
        String driverId = driverIdField.getText();
        String outputType = outputTypeDropdown.getValue();
        String outputFileName = outputFileNameField.getText();

        // fetch trips and points using service layer
        List<Trip> trips = tripService.getTripsByDriverId(driverId);
        List<String> tripIds = trips.stream().map(Trip::getTripId).collect(Collectors.toList());

        // we finally get a map that has tripIds and for every tripId we have a list of Point objects
        Map<String, List<Point>> pointsMap = tripService.getPointsByTripIds(tripIds);

        // we check the value from dropdown menu and see what type of file the visualization should have
        if (".txt".equals(outputType)) {
            generateTextFile(outputFileName, pointsMap);
        } else if ("PNG".equals(outputType)) {
            generatePngFile(outputFileName, pointsMap);
        }

    }

    // we have the actual implementation of generating a png file
    private void generatePngFile(String outputFileName, Map<String, List<Point>> pointsMap) {

        // use tree map so i can have sorted keys(tripIds) so i always start with trip01
        // it uses the default key ordering of the TreeMap
        Map<String, List<Point>> sortedMap = new TreeMap<>(pointsMap);
        sortedMap.putAll(pointsMap);

        System.out.println("PNG GENERATE METHOD IS CALLED");

        // see if user entered .png at the end of name, if not add it manually
        if (!outputFileName.endsWith(".png")) {
            outputFileName += ".png";
        }

        System.out.println("OUTPUDIR:" + outputDir);
        File outputFile = new File(File.separator + outputDir + File.separator + outputFileName);


        final int MAX_X = 50;
        final int MAX_Y = 50;
        int cellSize = 10; // Make the cells more visible

        //crt canvas
        Canvas canvas = new Canvas(MAX_X * cellSize, MAX_Y * cellSize);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // set every to white
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // 2d arr to mark which points are multiply used so we can black them out
        // we can also check if pixel is white and if not the black it but it's whatever
        int[][] pointCounter = new int[MAX_Y][MAX_X];

        //we increment each time the point is present ( if a point is used by 3 trips it's value
        // in the 2d arr is going to be 3
        for (Map.Entry<String, List<Point>> entry : sortedMap.entrySet()) {

            List<Point> points = entry.getValue();

            for (Point point : points) {
                int x = point.getX();
                int y = point.getY();
                if (x < MAX_X && y < MAX_Y) {
                    pointCounter[y][x]++;
                }
            }
        }

        List<Color> availableColors = List.of(Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.AQUAMARINE, Color.NAVY);
        int colorIndex = 0;

        // draw to the pixels
        for (Map.Entry<String, List<Point>> entry : sortedMap.entrySet()) {
            List<Point> points = entry.getValue();
            Color tripColor = availableColors.get(colorIndex);

            for (Point point : points) {
                int x = point.getX();
                int y = point.getY();

                if (x < MAX_X && y < MAX_Y) {

                    // check if shared by multiple trips
                    if (pointCounter[y][x] > 1) {

                        // if value over 1 set the pixel to black
                        gc.setFill(Color.BLACK);
                    } else {

                        //otherwise keep drawing with trip color
                        gc.setFill(tripColor);
                    }
                    // Draw the point
                    gc.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
                }
            }

            // Move to the next color for the next trip
            colorIndex++;
            if (colorIndex >= availableColors.size()) {
                colorIndex = 0;
            }
        }

        // Convert Canvas to WritableImage
        WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, writableImage);

        // Convert WritableImage to BufferedImage
        BufferedImage bufferedImage = new BufferedImage((int) canvas.getWidth(), (int) canvas.getHeight(), BufferedImage.TYPE_INT_ARGB);
        PixelReader pixelReader = writableImage.getPixelReader();

        for (int y = 0; y < writableImage.getHeight(); y++) {
            for (int x = 0; x < writableImage.getWidth(); x++) {
                // Get color from WritableImage
                javafx.scene.paint.Color fxColor = pixelReader.getColor(x, y);

                // Convert JavaFX Color to an RGB integer using ARGB format
                int argb = ((int)(fxColor.getOpacity() * 255) << 24) |
                        ((int)(fxColor.getRed() * 255) << 16) |
                        ((int)(fxColor.getGreen() * 255) << 8) |
                        ((int)(fxColor.getBlue() * 255));

                // Set the pixel in BufferedImage
                bufferedImage.setRGB(x, y, argb);
            }
        }

        // save BufferedImage to PNG file
        try {
            ImageIO.write(bufferedImage, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
    // generating the text file with the file name which is typed in the gui
    private void generateTextFile(String outputFileName, Map<String, List<Point>> pointsMap) {

        // use tree map so i can have sorted keys(tripIds) so i always start with trip01
        // it uses the default key ordering of the TreeMap
        Map<String, List<Point>> sortedMap = new TreeMap<>(pointsMap);
        sortedMap.putAll(pointsMap);


        System.out.println("TEXT GENERATE METHOD IS CALLED");
        // see if user adds .txt when giving the file name, if not add it manually
        if (!outputFileName.endsWith(".txt")) {
            outputFileName += ".txt";
        }

        System.out.println("OUTPUDIR:" + outputDir);
        File outputFile = new File(File.separator + outputDir + File.separator + outputFileName);


        // Find the maximum X and Y coordinates for dynamically sizing the grid
        final int MAX_X = 100;
        final int MAX_Y = 100;


        // we need 2d grid of characters
        char[][] grid = new char[MAX_X][MAX_Y];

       // initialize the whole grid with whitespaces
        for (int y = 0; y < MAX_Y; y++) {
            for (int x = 0; x < MAX_X; x++) {
                grid[y][x] = ' ';
            }
        }

        char letter = 'A'; // Start from 'A'

        //loop through the points of each trip
        for (Map.Entry<String, List<Point>> entry : sortedMap.entrySet()) {
            List<Point> points = entry.getValue();
            System.out.println(points);

            for (Point point : points) {
                int x = point.getX();
                int y = point.getY();

                if (grid[y][x] == ' ') {
                    grid[y][x] = letter;
                } else {
                    grid[y][x] = 'X'; // if it's not empty, it means it;s used so we give x
                }
            }

            //this is to skip X letter
            if (letter == 'W') {
                letter = 'Y';
            } else {
                letter++;
            }
        }

        // write the grid to a text file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (int y = 0; y < MAX_Y; y++) {
                writer.write(grid[y]);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


