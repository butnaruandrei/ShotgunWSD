package relatedness.embeddings.sense.bags;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.VerbSynset;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class VerbSenseBag extends SynsetSenseBag {
    public static String[] getSenseBag(Synset synset) {
        String senseBag = "";

        VerbSynset verbSynset = (VerbSynset) synset;
        senseBag += getSynsetBag(verbSynset);

        VerbSynset[] troponyms = verbSynset.getTroponyms();
        for (VerbSynset troponym : troponyms)
            senseBag += getSynsetBag(troponym);

        VerbSynset[] entailments = verbSynset.getEntailments();
        for (VerbSynset entailment : entailments)
            senseBag += getSynsetBag(entailment);

        VerbSynset[] outcomes = verbSynset.getOutcomes();
        for (VerbSynset outcome : outcomes)
            senseBag += getSynsetBag(outcome);

        VerbSynset[] hypernyms = verbSynset.getHypernyms();
        for (VerbSynset hypernym : hypernyms)
            senseBag += getSynsetBag(hypernym);

        return extractWordsFromSenseBag(senseBag);
    }
}
