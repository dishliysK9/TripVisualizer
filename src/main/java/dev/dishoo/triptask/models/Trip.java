package dev.dishoo.triptask.models;

public class Trip {


    // we get the trips that we want to visualize by driverId, but need the tripId
    // in order to get access to the fields of the points table, so we can display them
    private String tripId;
    private String driverId;

    private String startTs;

    private String endTs;

    public Trip(String tripId, String driverId, String startTs, String endTs) {
        this.tripId = tripId;
        this.driverId = driverId;
        this.startTs = startTs;
        this.endTs = endTs;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getStartTs() {
        return startTs;
    }

    public void setStartTs(String startTs) {
        this.startTs = startTs;
    }

    public String getEndTs() {
        return endTs;
    }

    public void setEndTs(String endTs) {
        this.endTs = endTs;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "tripId='" + tripId + '\'' +
                ", driverId='" + driverId + '\'' +
                ", startTs='" + startTs + '\'' +
                ", endTs='" + endTs + '\'' +
                '}';
    }
}
