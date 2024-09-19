package dev.dishoo.triptask.configuration;

import dev.dishoo.triptask.data.DataHandler;
import dev.dishoo.triptask.data.ExcelDataHandler;
import dev.dishoo.triptask.data.SQLiteDataHandler;
import dev.dishoo.triptask.service.TripService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    // this is the pathToInputFile which we get as an argument when launching the application
    // and we inject it's value here
    @Value("${app.inputFile}")
    private String pathToInputFile;
    
    @Bean
    public DataHandler dataHandler() {

        System.out.println("CHECK FOR PERF HANDLER");
        // Return the DataHandler bean initialized in the @PostConstruct method
        if (pathToInputFile.endsWith(".xlsx")) {
            return new ExcelDataHandler();
        } else if (pathToInputFile.endsWith(".db")) {
            return new SQLiteDataHandler();
        } else {
            throw new IllegalArgumentException("Unsupported file type");
        }

    }

    @Bean
    public TripService tripService(DataHandler dataHandler) {
        return new TripService(dataHandler);
    }



}
