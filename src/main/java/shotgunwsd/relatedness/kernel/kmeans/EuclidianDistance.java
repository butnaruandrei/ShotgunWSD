package shotgunwsd.relatedness.kernel.kmeans;

import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class EuclidianDistance implements DistanceFunction {
    public double distance(double[] p1, double[] p2) {
        double s = 0;
        for (int d = 0; d < p1.length && d < p2.length; d++) {
            s += Math.pow(Math.abs(p1[d] - p2[d]), 2);
        }
        return Math.sqrt(s);
    }

    public static double distance(INDArray p1, INDArray p2) {
        double s = 0;
        for (int d = 0; d < p1.length() && d < p2.length(); d++) {
            s += Math.pow(Math.abs(p1.getDouble(d) - p2.getDouble(d)), 2);
        }
        return Math.sqrt(s);
    }
}
