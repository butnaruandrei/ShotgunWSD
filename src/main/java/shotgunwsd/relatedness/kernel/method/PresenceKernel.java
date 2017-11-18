package shotgunwsd.relatedness.kernel.method;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class PresenceKernel {
    public static double compute(double[] A, double[] B) {
        double score = 0;

        for (int i = 0; i < A.length; i++) {
            score += in(A[i]) * in(B[i]);
        }

        return score;
    }

    private static int in(double value) {
        if(value > 0)
            return 1;
        else
            return 0;
    }
}
