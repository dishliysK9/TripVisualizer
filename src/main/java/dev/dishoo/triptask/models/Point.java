package dev.dishoo.triptask.models;

public class Point {

    private int id;
    private String tripId;
    private int x;
    private int y;
    private String ts;

    public Point(String tripId, int x, int y) {
        this.tripId = tripId;
        this.x = x;
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    @Override
    public String toString() {
        return "Point{" +
                "id=" + id +
                ", tripId='" + tripId + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", ts='" + ts + '\'' +
                '}';
    }
}
