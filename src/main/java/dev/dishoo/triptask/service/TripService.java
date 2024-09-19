package dev.dishoo.triptask.service;


import dev.dishoo.triptask.data.DataHandler;
import dev.dishoo.triptask.models.Point;
import dev.dishoo.triptask.models.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TripService {

    private final DataHandler dataHandler;

    @Autowired
    public TripService(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    public List<Trip> getTripsByDriverId(String driverId) {
        // Additional business logic if needed
        return dataHandler.getTripsByDriverId(driverId);
    }

    public Map<String, List<Point>> getPointsByTripIds(List<String> tripIds){
        return dataHandler.getPointsByTripIds(tripIds);
    }


}
