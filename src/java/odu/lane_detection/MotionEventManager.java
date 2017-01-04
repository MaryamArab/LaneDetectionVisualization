package odu.lane_detection;

import java.util.*;

public class MotionEventManager {

    public void OnMotionEvent(MotionEvent event) {
        VehicleStatus lastStatus = TraveledPath.getInstance().getLastStatus();
        VehicleStatus newStatus = processNewStatus(event, lastStatus);
        TraveledPath.getInstance().add(newStatus);
    }

    public VehicleStatus processNewStatus(MotionEvent event, VehicleStatus lastStatus) {
        List<Integer> newPossibleLanes = new ArrayList<Integer>();
        List<Integer> oldPossibleLanes;

        byte laneCount = Road.lanesCount(event.getLongitude(), event.getLatitude());

        if (lastStatus != null)
            oldPossibleLanes = lastStatus.getPossibleLanes();
        else {
            oldPossibleLanes = new ArrayList<Integer>();
            for (int i = 1; i <= laneCount; i++)
                oldPossibleLanes.add(i);
        }


       // printLanes(oldPossibleLanes);


        if (event instanceof LeftLaneSwitchEvent)
        {
            LeftLaneSwitchEvent leftLaneSwitchEvent = (LeftLaneSwitchEvent) event;
            for (Integer lane: oldPossibleLanes) {
                //By each leftSwitch lane , the title of each lane is increased by the number of lane switches.
                Integer newLane = lane + leftLaneSwitchEvent.getSwitchedLanesCount();
                if (newLane <= laneCount)
                    newPossibleLanes.add(newLane);
            }
        }

        else if(event instanceof RightLaneSwitchEvent)
        {
            RightLaneSwitchEvent rightLaneSwitchEvent = (RightLaneSwitchEvent)event;
            for (Integer lane: oldPossibleLanes) {
                Integer newLane = lane - rightLaneSwitchEvent.getSwitchedLanesCount();
                if (newLane > 0)
                    newPossibleLanes.add(newLane);
            }
        }

        else if(event instanceof LeftTurnEvent)
        {
            //most probably before a left turn , the car was in the left most lane(lane 1)
            LeftTurnEvent leftTurnEvent = (LeftTurnEvent) event;


        }
        else if(event instanceof RightTurnEvent)
        {

        }
        //TODO Uturn detect
        //TODO: other event types should be added here
        else
            newPossibleLanes = oldPossibleLanes;
//TODO comment:
     //   printLanes(newPossibleLanes);

        VehicleStatus newStatus = new VehicleStatus(event.getEventTime(), event.getLongitude(), event.getLatitude(), newPossibleLanes);
        return newStatus;
    }

    private void printLanes(List<Integer> lanes) {
        System.out.print("Current possible lanes: ");
        for (int i = 0; i < lanes.size();i++) {
            System.out.print(" " + lanes.get(i));
        }
        System.out.println();
    }
}
