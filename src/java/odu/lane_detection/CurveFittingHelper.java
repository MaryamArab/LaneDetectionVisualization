package odu.lane_detection;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import java.util.List;

public class CurveFittingHelper {
    public void fitCurve(List<Point> roadPoints, int startIndex, int endIndex) {
        final WeightedObservedPoints obs = new WeightedObservedPoints();
        for (int i = startIndex; i <= endIndex; i++) {
            obs.add(roadPoints.get(i).getLongitude(), roadPoints.get(i).getLatitude());
        }
        final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(3);

        final double[] coeff = fitter.fit(obs.toList());
        System.out.println("Curve Params: ");
        for (double p: coeff) {
            System.out.print(p + " ");
        }
        System.out.println();
    }
}
