package relatedness;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import org.apache.commons.lang.ArrayUtils;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import utils.MathUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class SenseEmbedding {
    public static HashMap<Synset, Double[]> wordEmbeddings = new HashMap<>();

    // TODO add docs
    /**
     *
     * @param wordVector The Word Embeddings dictionary
     * @param synset The synset ...
     * @param word The word ...
     * @return The sense embedding of a synset
     */
    public static double[] getSenseEmbedding(WordVectors wordVector, Synset synset, String word) {
        if(wordEmbeddings.containsKey(synset)){
            return ArrayUtils.toPrimitive(wordEmbeddings.get(synset));
        }

        String[] words = getSenseBag(synset, word);

        double[] senseEmbedding, tmpEmbedding;
        Double[] tmpEmbedding2, tmpSenseEmbedding;
        ArrayList<Double[]> senseEmbeddings = new ArrayList<>();

        // For each word in the sense bag, get the coresponding word embeddings and store them in an array
        for (String w : words) {
            if (w != null) {
                if (wordVector.hasWord(w)) {
                    tmpEmbedding = wordVector.getWordVector(w);

                    tmpEmbedding2 = new Double[tmpEmbedding.length];
                    for (int i = 0; i < tmpEmbedding.length; i++) {
                        tmpEmbedding2[i] = tmpEmbedding[i];
                    }
                    senseEmbeddings.add(tmpEmbedding2);
                }
            }
        }

        senseEmbedding = computeGeometricMedian(senseEmbeddings);

        tmpSenseEmbedding = new Double[senseEmbedding.length];
        for (int i = 0; i < tmpSenseEmbedding.length; i++) {
            tmpSenseEmbedding[i] = senseEmbedding[i];
        }
        wordEmbeddings.put(synset, tmpSenseEmbedding);

        return senseEmbedding;
    }

    /**
     * Computes the Geometric Median of a given array of word embeddings
     * @param senseEmbeddings Array of Word Embeddings
     * @return The vector that represents the geometric median of the given array
     */
    private static double[] computeGeometricMedian(ArrayList<Double[]> senseEmbeddings) {
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

    private static String[] getSenseBag(Synset synset, String word) {
        if(synset == null)
            return new String[0];

        SynsetType pos = synset.getType();

        if(pos == SynsetType.NOUN) {
            return NounRelatedness.getSenseBag(synset);
        } else if (pos == SynsetType.VERB) {
            return VerbRelatedness.getSenseBag(synset);
        } else if(pos == SynsetType.ADJECTIVE) {
            return AdjectiveRelatedness.getSenseBag(synset, word);
        } else if(pos == SynsetType.ADJECTIVE_SATELLITE) {
            return AdjectiveRelatedness.getSenseBag(synset, word);
        } else if(pos == SynsetType.ADVERB) {
            return AdverbRelatedness.getSenseBag(synset, word);
        }

        return new String[0];
    }
}
