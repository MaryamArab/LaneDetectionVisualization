package lanedetectionvis

import odu.lane_detection.*

class RoadController {

    def index() {
        String filePath = "Z:/Projects/LaneDetectionProject/Data/LeftTurn1_3_cleaned.csv";
//        MotionReader reader = new MotionReader(filePath);
//        TraveledPath path = reader.process();

        MotionEventManager manager = new MotionEventManager();
        LaneEvent laneEvent = new LaneEvent(manager, filePath);
        TraveledPath.reset();
        laneEvent.process();

//        def model = [lng1:path.getStatuses()[0].latitude , lat1: path.getStatuses()[0].longitude, lng2: path.getLastStatus().latitude , lat2:path.getLastStatus().longitude]

        def statuses = TraveledPath.instance.statuses

        def currentPart = [possibleLanes: statuses[0].possibleLanes, points: [[lat: statuses[0].latitude, lng: statuses[0].longitude]]]
        def pathParts = [currentPart]
        def prePossibleLanes = statuses[0].possibleLanes

        for (int i = 2; i < statuses.size(); i++) {
//        for (int i = 2; i < 3000; i++) {
            //if ((i == 0) || (statuses[i].latitude != statuses[i - 1].latitude) || (statuses[i].longitude != statuses[i - 1].longitude))
            //    points << [lat : statuses[i].latitude, lng: statuses[i].longitude]
            def status = statuses[i]
            //if (differentLanes(prePossibleLanes, status.possibleLanes))
            if (!prePossibleLanes.equals(status.possibleLanes))
            {
                currentPart = [possibleLanes: status.possibleLanes, points: [[lat: statuses[i-1].latitude, lng: statuses[i-1].longitude, newPoint: true]]]
                pathParts << currentPart

                prePossibleLanes = status.possibleLanes
            }
            def newPoint = (status.latitude != statuses[i - 1].latitude) || (status.longitude != statuses[i - 1].longitude)
            currentPart.points << [lat: status.latitude, lng: status.longitude, newPoint: newPoint]
        }

        def model = [:]
        model.pathParts = pathParts
        model
    }

//    private boolean differentLanes(List<Integer> lanes1, List<Integer> lanes2) {
//        def x = []
//        x.
//    }
}
