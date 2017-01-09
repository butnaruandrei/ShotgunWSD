package relatedness.embeddings.sense.computations;

import org.apache.commons.lang.ArrayUtils;
import utils.MathUtil;

import java.util.ArrayList;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class GeometricMedianComputation extends SenseComputation {
    static GeometricMedianComputation instance = null;

    public static GeometricMedianComputation getInstance() {
        if(instance == null){
            instance = new GeometricMedianComputation();
        }

        return instance;
    }

    /**
     * Computes the Geometric Median of a given array of word embeddings
     * @param senseEmbeddings Array of Word Embeddings
     * @return The vector that represents the geometric median of the given array
     */
    public double[] compute(ArrayList<Double[]> senseEmbeddings) {
        Double[] senseEmbedding = new Double[300];
        Double[] sum1;
        double sum2;

        int iterations = 10;
        double norm;

        for (int i = 0; i < 300; i++) {
            senseEmbedding[i] = 0d;
        }

        for (int i = 0; i < iterations; i++) {
            sum1 = new Double[300]; sum2 = 0;

            for (int j = 0; j < 300; j++) {
                sum1[j] = 0d;
            }

            for(Double[] tmpSenseEmbedding : senseEmbeddings) {
                // this is x
                norm = MathUtil.computeNorm(tmpSenseEmbedding, senseEmbedding);

                sum1 = MathUtil.sumVectors(sum1, MathUtil.divideVectors(tmpSenseEmbedding, norm));
                sum2 += 1 / norm;
            }

            senseEmbedding = MathUtil.divideVectors(sum1, sum2);
        }

        return ArrayUtils.toPrimitive(senseEmbedding);
    }
}
