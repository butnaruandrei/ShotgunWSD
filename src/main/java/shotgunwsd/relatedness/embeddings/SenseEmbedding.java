package shotgunwsd.relatedness.embeddings;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import org.apache.commons.lang.ArrayUtils;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import shotgunwsd.relatedness.embeddings.sense.bags.AdjectiveSenseBag;
import shotgunwsd.relatedness.embeddings.sense.bags.AdverbSenseBag;
import shotgunwsd.relatedness.embeddings.sense.bags.NounSenseBag;
import shotgunwsd.relatedness.embeddings.sense.bags.VerbSenseBag;
import shotgunwsd.relatedness.embeddings.sense.computations.SenseComputation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class SenseEmbedding {
    public static HashMap<Synset, Double[]> wordEmbeddings = new HashMap<>();
    public static HashSet<String> allWords = new HashSet<>();

    // TODO add docs
    /**
     *
     * @param wordVector The Word Embeddings dictionary
     * @param synset The synset ...
     * @param word The word ...
     * @return The sense embedding of a synset
     */
    public static double[] getSenseEmbedding(WordVectors wordVector, Synset synset, String word, SenseComputation senseComputation) {
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
                allWords.add(w);

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

        return convertSenseEmbeddings(senseEmbeddings, synset, senseComputation);
    }

    public static double[] getSenseEmbedding(HashMap<String, Double[]> wordClusters, Synset synset, String word, SenseComputation senseComputation) {
        if(wordEmbeddings.containsKey(synset)){
            return ArrayUtils.toPrimitive(wordEmbeddings.get(synset));
        }

        String[] words = getSenseBag(synset, word);
        ArrayList<Double[]> senseEmbeddings = new ArrayList<>();

        // For each word in the sense bag, get the coresponding word embeddings and store them in an array
        for (String w : words) {
            if (w != null) {
                allWords.add(w);

                if (wordClusters.containsKey(w)) {
                    senseEmbeddings.add(wordClusters.get(w));
                }
            }
        }

        return convertSenseEmbeddings(senseEmbeddings, synset, senseComputation);
    }

    public static double[] convertSenseEmbeddings(ArrayList<Double[]> senseEmbeddings, Synset synset, SenseComputation senseComputation) {
        double[] senseEmbedding;
        if(senseEmbeddings.size() == 0) {
            senseEmbedding = new double[300];
            for (int i = 0; i < senseEmbedding.length; i++) {
                senseEmbedding[i] = 0d;
            }
        } else {
            senseEmbedding = senseComputation.compute(senseEmbeddings);
        }

        Double[] tmpSenseEmbedding = ArrayUtils.toObject(senseEmbedding);
        wordEmbeddings.put(synset, tmpSenseEmbedding);

        return senseEmbedding;
    }

    private static String[] getSenseBag(Synset synset, String word) {
        if(synset == null)
            return new String[0];

        SynsetType pos = synset.getType();

        if(pos == SynsetType.NOUN) {
            return NounSenseBag.getSenseBag(synset);
        } else if (pos == SynsetType.VERB) {
            return VerbSenseBag.getSenseBag(synset);
        } else if(pos == SynsetType.ADJECTIVE) {
            return AdjectiveSenseBag.getSenseBag(synset, word);
        } else if(pos == SynsetType.ADJECTIVE_SATELLITE) {
            return AdjectiveSenseBag.getSenseBag(synset, word);
        } else if(pos == SynsetType.ADVERB) {
            return AdverbSenseBag.getSenseBag(synset, word);
        }

        return new String[0];
    }
}
