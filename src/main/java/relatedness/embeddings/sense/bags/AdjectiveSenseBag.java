package relatedness.embeddings.sense.bags;

import edu.smu.tspell.wordnet.*;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class AdjectiveSenseBag extends SynsetSenseBag {
    public static String[] getSenseBag(Synset synset, String word) {
        String senseBag = "";

        senseBag += getAdjSenseBag(synset, word);

        if(synset.getType() == SynsetType.ADJECTIVE_SATELLITE) {
            AdjectiveSynset headSynset = ((AdjectiveSatelliteSynset) synset).getHeadSynset();
            senseBag += getSynsetBag(headSynset);
        }

        return extractWordsFromSenseBag(senseBag);
    }

    public static String getAdjSenseBag(Synset synset, String word) {
        String senseBag = "";

        AdjectiveSynset adjSynset = (AdjectiveSynset) synset;
        senseBag += getSynsetBag(adjSynset);

        AdjectiveSynset[] relateds = adjSynset.getRelated();
        for (AdjectiveSynset related : relateds)
            senseBag += getSynsetBag(related);

        NounSynset[] attributes = adjSynset.getAttributes();
        for (NounSynset attribute : attributes)
            senseBag += getSynsetBag(attribute);

        AdjectiveSynset[] similars = adjSynset.getSimilar();
        for (AdjectiveSynset similar : similars)
            senseBag += getSynsetBag(similar);

        WordSense[] antonyms = adjSynset.getAntonyms(word);
        for (WordSense antonym : antonyms)
            senseBag += getSynsetBag(antonym.getSynset());

        WordSense[] pertainyms = adjSynset.getPertainyms(word);
        for (WordSense pertainym : pertainyms)
            senseBag += getSynsetBag(pertainym.getSynset());

        return senseBag;
    }
}
