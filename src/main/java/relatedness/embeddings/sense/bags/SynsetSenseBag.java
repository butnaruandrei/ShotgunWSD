package relatedness.embeddings.sense.bags;

import edu.smu.tspell.wordnet.Synset;
import relatedness.SynsetRelatedness;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class SynsetSenseBag {
    protected static String getSenseKeysBag(Synset synset) {
        String senseBag = "";

        String[] senseKeys = synset.getSenseKeys();
        for(String senseKey : senseKeys) {
            senseBag += senseKey.split("%")[0] + " ";
        }

        return senseBag;
    }

    protected static String getSynsetBag(Synset synset) {
        String senseBag = " ";
        String[] examples;

        senseBag += getSenseKeysBag(synset);
        senseBag += synset.getDefinition();
        examples = synset.getUsageExamples();

        for (String example1 : examples)
            senseBag += " " + example1;

        return senseBag;
    }

    protected static String[] extractWordsFromSenseBag(String senseBag){
        String[] words;

        senseBag = senseBag.replace("\"", "").replaceAll(" +", " ");

        words = senseBag.trim().split("[^a-zA-Z\']+");
        words = SynsetRelatedness.eliminateStopWordsFromWordSet(words);
        words = new HashSet<>(Arrays.asList(words)).toArray(new String[words.length]); // remove duplicate words

        return words;
    }
}
