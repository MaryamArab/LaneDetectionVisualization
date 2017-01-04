package odu.lane_detection;

import java.util.*;

public class TraveledPath {
    private static TraveledPath instance = new TraveledPath();

    private List<VehicleStatus> statuses;

    public static TraveledPath getInstance() {
        return instance;
    }

    public List<VehicleStatus> getStatuses() {
        return statuses;
    }

    public static void reset() {
        instance = new TraveledPath();
    }

    public void add(VehicleStatus newStatus) {
        statuses.add(newStatus);
    }

    public VehicleStatus getLastStatus() {
        if (statuses.size() == 0)
            return null;
        return statuses.get(statuses.size() - 1);
    }

    public TraveledPath() {
        this.statuses = new ArrayList<VehicleStatus>();
    }
}
