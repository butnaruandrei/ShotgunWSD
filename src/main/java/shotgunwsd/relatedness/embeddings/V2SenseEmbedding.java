package shotgunwsd.relatedness.embeddings;

import edu.smu.tspell.wordnet.Synset;
import org.apache.commons.lang.ArrayUtils;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import shotgunwsd.relatedness.embeddings.sense.computations.SenseComputation;
import shotgunwsd.relatedness.kernel.kmeans.EuclidianDistance;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class V2SenseEmbedding {
    public static HashMap<Synset, Double[]> backupWordEmbeddings = new HashMap<>();

    public static double[] getSenseEmbedding(WordVectors wordVector, Synset synset, String word, SenseComputation senseComputation) {
        if (backupWordEmbeddings.containsKey(synset)) {
            return ArrayUtils.toPrimitive(backupWordEmbeddings.get(synset));
        }

        double[] senseEmbedding = SenseEmbedding.getSenseEmbedding(wordVector, synset, word, senseComputation);

        String[] words = SenseEmbedding.getSenseBag(synset, word);
        ArrayList<Double[]> wordEmbeddings = new ArrayList<>();

        for (String w : words) {
            if (w != null) {
                if (wordVector.hasWord(w)) {
                    wordEmbeddings.add(ArrayUtils.toObject(wordVector.getWordVector(w)));
                }
            }
        }

        return computeV2SenseEmbedding(words, wordEmbeddings, synset, word, senseComputation, senseEmbedding);
    }

    public static double[] getSenseEmbedding(HashMap<String, Double[]> wordVector, Synset synset, String word, SenseComputation senseComputation) {
        if(backupWordEmbeddings.containsKey(synset)){
            return ArrayUtils.toPrimitive(backupWordEmbeddings.get(synset));
        }

        double[] senseEmbedding = SenseEmbedding.getSenseEmbedding(wordVector, synset, word, senseComputation);
        String[] words = SenseEmbedding.getSenseBag(synset, word);
        ArrayList<Double[]> wordEmbeddings = new ArrayList<>();

        for (String w : words) {
            if (w != null) {
                if (wordVector.containsKey(w)) {
                    wordEmbeddings.add(wordVector.get(w));
                }
            }
        }

        return computeV2SenseEmbedding(words, wordEmbeddings, synset, word, senseComputation, senseEmbedding);
    }

    private static double[] computeV2SenseEmbedding(String[] words, ArrayList<Double[]> wordEmbeddings, Synset synset, String word, SenseComputation senseComputation, double[] senseEmbedding){
        EuclidianDistance euclidian = new EuclidianDistance();

        double mean = 0d, std = 0d;
        for(Double[] wordEmbedding : wordEmbeddings) {
            mean += euclidian.distance(senseEmbedding, ArrayUtils.toPrimitive(wordEmbedding));
        }
        mean /= wordEmbeddings.size();

        double[] distances = new double[wordEmbeddings.size()];

        int i = 0;
        for(Double[] wordEmbedding : wordEmbeddings) {
            distances[i] = euclidian.distance(senseEmbedding, ArrayUtils.toPrimitive(wordEmbedding));
            std += Math.pow(distances[i] - mean, 2);
            i++;
        }

        std = Math.sqrt(std / (wordEmbeddings.size() - 1));

        ArrayList<Double[]> senseEmbeddings = new ArrayList<>();
        for (i = 0; i < wordEmbeddings.size(); i++) {
            if(distances[i] <= mean + std ) {
                senseEmbeddings.add(wordEmbeddings.get(i));
            }
        }

        double[] tmpSenseEmbedding = SenseEmbedding.convertSenseEmbeddings(senseEmbeddings, synset, senseComputation);
        backupWordEmbeddings.put(synset, ArrayUtils.toObject(tmpSenseEmbedding));

        return tmpSenseEmbedding;
    }
}
