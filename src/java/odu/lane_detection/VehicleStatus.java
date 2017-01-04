package odu.lane_detection;

import java.util.Date;
import java.util.List;

public class VehicleStatus {
    private Date statusTime;
    private Point location;

    private List<Integer> possibleLanes;

    public VehicleStatus(Date statusTime, double longitude, double latitude, List<Integer> possibleLanes) {
        this.statusTime = statusTime;
        this.location = new Point(latitude, longitude);
        this.possibleLanes = possibleLanes;
    }

    public Date getStatusTime() {
        return statusTime;
    }

    public void setStatusTime(Date statusTime) {
        this.statusTime = statusTime;
    }

    public double getLongitude() {
        return location.getLongitude();
    }

    public double getLatitude() {
        return location.getLatitude();
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public List<Integer> getPossibleLanes() {
        return possibleLanes;
    }

    public void setPossibleLanes(List<Integer> possibleLanes) {
        this.possibleLanes = possibleLanes;
    }


}
