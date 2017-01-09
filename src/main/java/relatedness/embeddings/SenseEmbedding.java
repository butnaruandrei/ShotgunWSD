package relatedness.embeddings;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import org.apache.commons.lang.ArrayUtils;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import relatedness.embeddings.sense.bags.AdjectiveSenseBag;
import relatedness.embeddings.sense.bags.AdverbSenseBag;
import relatedness.embeddings.sense.bags.NounSenseBag;
import relatedness.embeddings.sense.bags.VerbSenseBag;
import relatedness.embeddings.sense.computations.SenseComputation;

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

        senseEmbedding = senseComputation.compute(senseEmbeddings);

        tmpSenseEmbedding = new Double[senseEmbedding.length];
        for (int i = 0; i < tmpSenseEmbedding.length; i++) {
            tmpSenseEmbedding[i] = senseEmbedding[i];
        }
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
