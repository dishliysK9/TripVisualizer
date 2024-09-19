package dev.dishoo.triptask.data;

import dev.dishoo.triptask.models.Point;
import dev.dishoo.triptask.models.Trip;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


public interface DataHandler {

    List<Trip> getTripsByDriverId(String driverId);
    public Map<String, List<Point>> getPointsByTripIds(List<String> tripIds);
}
