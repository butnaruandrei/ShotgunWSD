package utils;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class MathUtil {
    public static double computeNorm(Double[] x, Double[] y) {
        double norm = 0;

        for (int i = 0; i < x.length; i++) {
            norm += Math.pow(x[i] - y[i], 2);
        }

        return Math.sqrt(norm);
    }

    public static Double[] sumVectors(Double[] x, Double[] y){
        Double[] newVec = new Double[300];

        for (int i = 0; i < x.length; i++) {
            newVec[i] = x[i] + y[i];
        }

        return newVec;
    }

    public static Double[] divideVectors(Double[] x, double y){
        Double[] newVec = new Double[300];

        for (int i = 0; i < x.length; i++) {
            newVec[i] = x[i] / y;
        }

        return newVec;
    }
}
