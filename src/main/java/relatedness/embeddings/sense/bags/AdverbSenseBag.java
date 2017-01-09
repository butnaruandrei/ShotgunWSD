package relatedness.embeddings.sense.bags;

import edu.smu.tspell.wordnet.AdverbSynset;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordSense;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class AdverbSenseBag extends SynsetSenseBag {
    public static String[] getSenseBag(Synset synset, String word) {
        String senseBag = "";

        AdverbSynset adverbSynset = (AdverbSynset) synset;
        senseBag += getSynsetBag(adverbSynset);

        NounSynset[] topics = adverbSynset.getTopics();
        for (NounSynset topic : topics)
            senseBag += getSynsetBag(topic);

        WordSense[] pertainyms = adverbSynset.getPertainyms(word);
        for (WordSense pertainym : pertainyms)
            senseBag += getSynsetBag(pertainym.getSynset());

        WordSense[] antonyms = adverbSynset.getAntonyms(word);
        for (WordSense antonym : antonyms)
            senseBag += getSynsetBag(antonym.getSynset());

        return extractWordsFromSenseBag(senseBag);
    }
}
