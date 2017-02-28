package relatedness.embeddings.sense.computations;

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
        double[] senseEmbedding = new double[300];

        for (int i = 0; i < 300; i++) {
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
