package odu.lane_detection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import com.google.maps.model.SnappedPoint;

public class LaneSwitchTest {
    public static void main(String[] args) throws Exception {
//        String filePath = "Z:/A_MobileApp/Projects/source/lane_detection/Data/3Lane_dirty.csv";
        String filePath = "Z:/Projects/LaneDetectionProject/Data/LeftTurn1_3_cleaned.csv";
        MotionReader reader = new MotionReader(filePath);
        TraveledPath path = reader.process();
        System.out.println("Source file read complete: " + path.getStatuses().size() + " rows.");

        RoadApiHelper roadApiHelper = new RoadApiHelper();
      //  ArrayList<SnappedPoint> roadPoints = roadApiHelper.getRoadPoints(path);

        MotionEventManager manager = new MotionEventManager();
        LaneEvent laneEvent = new LaneEvent(manager, filePath);
        laneEvent.process();

        CurveFittingHelper curveFittingHelper = new CurveFittingHelper();
        //curveFittingHelper.fitCurve(roadPoints, 0, 1000);
    }
}
