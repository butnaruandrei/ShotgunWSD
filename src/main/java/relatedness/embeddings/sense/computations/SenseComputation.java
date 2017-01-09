package relatedness.embeddings.sense.computations;

import java.util.ArrayList;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public abstract class SenseComputation {
    protected SenseComputation() {
        // Exists only to defeat instantiation.
    }

    /**
     * Computes the Geometric Median of a given array of word embeddings
     * @param senseEmbeddings Array of Word Embeddings
     * @return The vector that represents the geometric median of the given array
     */
    public abstract double[] compute(ArrayList<Double[]> senseEmbeddings);
}
