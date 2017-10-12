package shotgunwsd.relatedness.kernel.method;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class IntersectionKernel {
    public static double compute(double[] A, double[] B) {
        double score = 0;

        for (int i = 0; i < A.length; i++) {
            score += Math.min(A[i], B[i]);
        }

        return score;
    }
}
