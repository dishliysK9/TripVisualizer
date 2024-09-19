package dev.dishoo.triptask;

import dev.dishoo.triptask.gui.TripViewerJavaFXApp;
import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.Map;


@ComponentScan({"dev.dishoo.triptask.gui", "dev.dishoo.triptask", "dev.dishoo.triptask.configuration"})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class TriptaskApplication {

	private static ApplicationContext context;
	private static String outputDir;

	public static void main(String[] args) {


		// little check so we can be sure the app receives the required arguments when we call it
		if (args.length < 2) {
			System.out.println("Please provide [pathToInputFile] and [pathToOutputDir] as arguments.");
			System.exit(1);
		}

		String pathToInputFile = args[0];  // First argument: input file path
		String pathToOutputDir = args[1]; // Second argument: output directory

		// adding the arguments to Spring application context (so we can get help from spring
		// to inject them if needed
		SpringApplication app = new SpringApplication(TriptaskApplication.class);
		app.setDefaultProperties(Map.of(
				"app.inputFile", pathToInputFile,
				"app.outputDir", pathToOutputDir
		));

		context = app.run(args);

		// Retrieve the outputDir from Spring environment
		outputDir = context.getEnvironment().getProperty("app.outputDir");



		// launch JavaFX application (show the gui) [after spring boot app has started]
		Application.launch(TripViewerJavaFXApp.class, args);



	}

	// Method to allow JavaFX to retrieve Spring beans
	public static ApplicationContext getContext() {
		return context;
	}

	public static String getOutputDir() {
		return outputDir;
	}
}


