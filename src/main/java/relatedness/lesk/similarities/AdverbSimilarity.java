package relatedness.lesk.similarities;

import edu.smu.tspell.wordnet.AdverbSynset;
import edu.smu.tspell.wordnet.Synset;
import utils.SynsetUtils;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class AdverbSimilarity extends SynsetSimilarity {

    /* Computes the relatedness score for adverbs taking into account only glosses.
     * Since WordNet structure is not well developed for adverbs, WSD for adverbs is unreliable. */
    public static double similarity(Synset synset1, String word1, Synset synset2, String word2) {
        double score = 0;

        String gloss1, gloss2,
                topicsGloss1, topicsGloss2,
                antoGloss1, antoGloss2,
                pertainymsGloss1, pertainymsGloss2;

        AdverbSynset adverbSynset1 = (AdverbSynset) synset1;
        AdverbSynset adverbSynset2 = (AdverbSynset) synset2;

	/* 1. Compute glosses for each synset. */
        gloss1 = SynsetUtils.getGloss(adverbSynset1);
        gloss2 = SynsetUtils.getGloss(adverbSynset2);

        topicsGloss1 = SynsetUtils.getRelationGloss(adverbSynset1.getTopics());
        topicsGloss2 = SynsetUtils.getRelationGloss(adverbSynset2.getTopics());

        antoGloss1 = SynsetUtils.getRelationGloss(adverbSynset1.getAntonyms(word1));
        antoGloss2 = SynsetUtils.getRelationGloss(adverbSynset2.getAntonyms(word2));

        pertainymsGloss1 = SynsetUtils.getRelationGloss(adverbSynset1.getPertainyms(word1));
        pertainymsGloss2 = SynsetUtils.getRelationGloss(adverbSynset2.getPertainyms(word2));

	/* 2. Compute the overlap score of the two glosses. */
        if(!gloss1.equals("") && !gloss2.equals(""))
            score += getScore(gloss1,gloss2);

        if(!gloss1.equals("") && !topicsGloss2.equals(""))
            score += getScore(gloss1,topicsGloss2);
        if(!topicsGloss1.equals("") && !gloss2.equals(""))
            score += getScore(topicsGloss1,gloss2);

        if(!gloss1.equals("") && !antoGloss2.equals(""))
            score += getScore(gloss1,antoGloss2);
        if(!antoGloss1.equals("") && !gloss2.equals(""))
            score += getScore(antoGloss1,gloss2);

        if(!gloss1.equals("") && !pertainymsGloss2.equals(""))
            score += getScore(gloss1,pertainymsGloss2);
        if(!pertainymsGloss1.equals("") && !gloss2.equals(""))
            score += getScore(pertainymsGloss1,gloss2);

        return score;
    }
}
