package odu.lane_detection;

public class LeftLaneSwitchEvent extends MotionEvent {
    private byte switchedLanesCount;

    public byte getSwitchedLanesCount() {
        return switchedLanesCount;
    }

    public void setSwitchedLanesCount(byte switchedLanesCount) {
        this.switchedLanesCount = switchedLanesCount;
    }

    public LeftLaneSwitchEvent(byte switchedLanesCount, double longitude, double latitude) {
        this.switchedLanesCount = switchedLanesCount;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
