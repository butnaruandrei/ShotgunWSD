package shotgunwsd.relatedness.embeddings.sense.computations;

import java.util.ArrayList;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class AverageComputation extends SenseComputation {
    static AverageComputation instance = null;

    public static AverageComputation getInstance() {
        if(instance == null){
            instance = new AverageComputation();
        }

        return instance;
    }

    public double[] compute(ArrayList<Double[]> senseEmbeddings) {
        int len = senseEmbeddings.get(0).length;

        double[] senseEmbedding = new double[len];

        for (int i = 0; i < len; i++) {
            senseEmbedding[i] = 0;
        }

        for (Double[] tmpSenseEmbedding : senseEmbeddings) {
            for (int i = 0; i < tmpSenseEmbedding.length; i++) {
                senseEmbedding[i] += tmpSenseEmbedding[i];
            }
        }

        int size = senseEmbeddings.size();
        for (int i = 0; i < senseEmbedding.length; i++) {
            senseEmbedding[i] /= size;
        }

        return senseEmbedding;
    }
}
