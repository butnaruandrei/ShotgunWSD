package relatedness.embeddings.sense.bags;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class NounSenseBag extends SynsetSenseBag {
    public static String[] getSenseBag(Synset synset) {
        String senseBag = "";

        NounSynset nounSynset = (NounSynset) synset;
        senseBag += getSynsetBag(nounSynset);

        NounSynset[] hyponyms = nounSynset.getHyponyms();
        for (NounSynset hyponym : hyponyms)
            senseBag += getSynsetBag(hyponym);

        NounSynset[] meronyms = nounSynset.getMemberMeronyms();
        for (NounSynset meronym : meronyms)
            senseBag += getSynsetBag(meronym);

        return extractWordsFromSenseBag(senseBag);
    }
}
