package shotgunwsd.relatedness.lesk;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import shotgunwsd.relatedness.SynsetRelatedness;
import shotgunwsd.relatedness.lesk.similarities.*;

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

        return computeSimilarity(synsets[i], windowWords[synset2WordIndex[i]], synsets[j], windowWords[synset2WordIndex[j]]);
    }

    public double computeSimilarity(Synset synset1, String word1, Synset synset2, String word2){
        SynsetType pos1 = null, pos2 = null;

        if(synset1 == null || synset2 == null) {
            return 0d;
        }

        try {
            pos1 = synset1.getType();
            pos2 = synset2.getType();
        } catch(Exception e){
            System.out.printf("ok");
        }

        if(pos1 == SynsetType.NOUN && pos2 == SynsetType.NOUN) {
            return NounSimilarity.similarity(synset1, synset2);
        } else if(pos1 == SynsetType.VERB && pos2 == SynsetType.VERB) {
            return VerbSimilarity.similarity(synset1, synset2);
        } else if(AdjectiveSimilarity.synsetTypeAdjective(pos1, pos2)) {
            return AdjectiveSimilarity.similarity(synset1, word1, synset2, word2);
        } else if(pos1 == SynsetType.ADVERB && pos2 == SynsetType.ADVERB) {
            return AdverbSimilarity.similarity(synset1, word1, synset2, word2);
        } else {
            return SynsetSimilarity.similarity(synset1, synset2);
        }
    }
}
