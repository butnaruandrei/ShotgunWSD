package relatedness.lesk;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import relatedness.SynsetRelatedness;
import relatedness.lesk.similarities.*;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class LeskRelatedness extends SynsetRelatedness {
    static LeskRelatedness instance = null;

    protected LeskRelatedness(){}
    public static LeskRelatedness getInstance() {
        if(instance == null) {
            instance = new LeskRelatedness();
        }

        return instance;
    }
    public Object[] computeSynsetRepresentations(Synset[] windowWordsSynsets, String[] windowWords, int[] synset2WordIndex){
        return windowWordsSynsets;
    }

    public double computeSimilarity(Object[] synsetRepresentations, String[] windowWords, int[] synset2WordIndex, int i, int j){
        Synset[] synsets = (Synset[]) synsetRepresentations;
        SynsetType pos1, pos2;

        pos1 = synsets[i].getType();
        pos2 = synsets[j].getType();

        if(pos1 == SynsetType.NOUN && pos2 == SynsetType.NOUN) {
            return NounSimilarity.similarity(synsets[i], synsets[j]);
        } else if(pos1 == SynsetType.VERB && pos2 == SynsetType.VERB) {
            return VerbSimilarity.similarity(synsets[i], synsets[j]);
        } else if(AdjectiveSimilarity.synsetTypeAdjective(pos1, pos2)) {
            return AdjectiveSimilarity.similarity(synsets[i], windowWords[synset2WordIndex[i]], synsets[j], windowWords[synset2WordIndex[j]]);
        } else if(pos1 == SynsetType.ADVERB && pos2 == SynsetType.ADVERB) {
            return AdverbSimilarity.similarity(synsets[i], windowWords[synset2WordIndex[i]], synsets[j], windowWords[synset2WordIndex[j]]);
        } else {
            return SynsetSimilarity.similarity(synsets[i], synsets[j]);
        }

    }
}
