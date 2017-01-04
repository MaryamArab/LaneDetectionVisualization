package odu.lane_detection;

import com.google.maps.GeoApiContext;
import com.google.maps.RoadsApi;
import com.google.maps.model.LatLng;
import com.google.maps.model.SnappedPoint;
import java.util.ArrayList;

public class RoadApiHelper {

    static final int PAGINATION_OVERLAP = 100;
    static final int PAGE_SIZE_LIMIT = 100;

    public ArrayList<SnappedPoint> getRoadPoints(TraveledPath path) throws Exception {
        ArrayList<LatLng> result = new ArrayList<LatLng>();
        for (VehicleStatus status: path.getStatuses()) {
            result.add(new LatLng(status.getLatitude(), status.getLongitude()));
        }

        GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyAXeLyYN_Q27PgHwfBgMk5Bos7qR6XWLJY");
        ArrayList<SnappedPoint> snappedPoints = snapToRoads(context, result);

        return snappedPoints;
    }

    private ArrayList<SnappedPoint> snapToRoads(GeoApiContext context, ArrayList<LatLng> capturedLocations) throws Exception {
        ArrayList<SnappedPoint> snappedPoints = new ArrayList<SnappedPoint>();

        int offset = 0;
        while (offset < capturedLocations.size()) {
            // Calculate which points to include in this request. We can't exceed the APIs
            // maximum and we want to ensure some overlap so the API can infer a good location for
            // the first few points in each request.
            if (offset > 0) {
                offset -= PAGINATION_OVERLAP;   // Rewind to include some previous points
            }
            int lowerBound = offset;
            int upperBound = Math.min(offset + PAGE_SIZE_LIMIT, capturedLocations.size());

            // Grab the data we need for this page.
            LatLng[] page = capturedLocations
                    .subList(lowerBound, upperBound)
                    .toArray(new LatLng[upperBound - lowerBound]);

            // Perform the request. Because we have interpolate=true, we will get extra data points
            // between our originally requested path. To ensure we can concatenate these points, we
            // only start adding once we've hit the first new point (i.e. skip the overlap).
            SnappedPoint[] points = RoadsApi.snapToRoads(context, true, page).await();
            boolean passedOverlap = false;
            for (SnappedPoint point : points) {
                if (offset == 0 || point.originalIndex >= PAGINATION_OVERLAP - 1) {
                    passedOverlap = true;
                }
                if (passedOverlap) {
                    snappedPoints.add(point);
                }
            }

            offset = upperBound;
        }

        return snappedPoints;
    }
}
