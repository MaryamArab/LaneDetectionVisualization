package odu.lane_detection;

/**
 * Created by marab on 10/8/2015.
 */
public class RightLaneSwitchEvent extends MotionEvent {
    byte switchedLanesCount;
    public byte getSwitchedLanesCount() {
        return switchedLanesCount;
    }
    public void setSwitchedLanesCount(byte switchedLanesCount) {
        this.switchedLanesCount = switchedLanesCount;
    }

    public RightLaneSwitchEvent(byte switchedLanesCount, double longitude, double latitude) {
        this.switchedLanesCount = switchedLanesCount;
        this.longitude = longitude;
        this.latitude = latitude;
    }

}
