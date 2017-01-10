package relatedness.lesk.similarities;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.VerbSynset;
import utils.SynsetUtils;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class VerbSimilarity extends SynsetSimilarity {
    /* Computes the relatedness score for verbs taking into account glosses, hyponyms, entailments and outcomes. */
    public static double similarity(Synset synset1, Synset synset2) {
        int i,j;
        double score = 0;
        String[] examples;

        String gloss1,gloss2,
                hypoGloss1,hypoGloss2,
                entailGloss1,entailGloss2,
                causalGloss1,causalGloss2,
                hyperGloss1, hyperGloss2;

        VerbSynset verbSynset1 = (VerbSynset) synset1;
        VerbSynset verbSynset2 = (VerbSynset) synset2;

    /* 1. Compute glosses for each relation. */
        gloss1 = SynsetUtils.getGloss(verbSynset1);
        gloss2 = SynsetUtils.getGloss(verbSynset2);

        hypoGloss1 = SynsetUtils.getRelationGloss(verbSynset1.getTroponyms());
        hypoGloss2 = SynsetUtils.getRelationGloss(verbSynset2.getTroponyms());

        entailGloss1 = SynsetUtils.getRelationGloss(verbSynset1.getEntailments());
        entailGloss2 = SynsetUtils.getRelationGloss(verbSynset2.getEntailments());

        causalGloss1 = SynsetUtils.getRelationGloss(verbSynset1.getOutcomes());
        causalGloss2 = SynsetUtils.getRelationGloss(verbSynset2.getOutcomes());

        hyperGloss1 = SynsetUtils.getRelationGloss(verbSynset1.getHypernyms());
        hyperGloss2 = SynsetUtils.getRelationGloss(verbSynset2.getHypernyms());


    /* 2. Sum the scores computed for each pair of relations considered. */
        if(!gloss1.equals("") && !gloss2.equals(""))
            score += getScore(gloss1,gloss2);
        if(!hypoGloss1.equals("") && !hypoGloss2.equals(""))
            score += getScore(hypoGloss1,hypoGloss2);

        if(!gloss1.equals("") && !hypoGloss2.equals(""))
            score += getScore(gloss1,hypoGloss2);
        if(!hypoGloss1.equals("") && !gloss2.equals(""))
            score += getScore(hypoGloss1,gloss2);

        if(!gloss1.equals("") && !entailGloss2.equals(""))
            score += getScore(gloss1,entailGloss2);
        if(!entailGloss1.equals("") && !gloss2.equals(""))
            score += getScore(entailGloss1,gloss2);

        if(!gloss1.equals("") && !causalGloss2.equals(""))
            score += getScore(gloss1,causalGloss2);
        if(!causalGloss1.equals("") && !gloss2.equals(""))
            score += getScore(causalGloss1,gloss2);

        if(!gloss1.equals("") && !hyperGloss2.equals(""))
            score += getScore(gloss1,hyperGloss2);
        if(!hyperGloss1.equals("") && !gloss2.equals(""))
            score += getScore(hyperGloss1,gloss2);

        return score;
    }
}
