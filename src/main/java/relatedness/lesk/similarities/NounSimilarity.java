package relatedness.lesk.similarities;

import edu.smu.tspell.wordnet.NounSynset;
import net.didion.jwnl.data.Synset;
import utils.SynsetUtils;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class NounSimilarity extends SynsetSimilarity {
    /* Computes the relatedness score for nouns taking into account glosses, hyponyms and meronyms. */
    public static double similarity(edu.smu.tspell.wordnet.Synset synset1, edu.smu.tspell.wordnet.Synset synset2){
        double score = 0;

        String gloss1,gloss2,
                hypoGloss1,hypoGloss2,
                meroGloss1,meroGloss2;

        NounSynset nounSynset1 = (NounSynset) synset1;
        NounSynset nounSynset2 = (NounSynset) synset2;

	/* 1. Compute glosses for each relation. */
        gloss1 = SynsetUtils.getGloss(nounSynset1);
        gloss2 = SynsetUtils.getGloss(nounSynset2);

        hypoGloss1 = SynsetUtils.getRelationGloss(nounSynset1.getHyponyms());
        hypoGloss2 = SynsetUtils.getRelationGloss(nounSynset2.getHyponyms());

        meroGloss1 = SynsetUtils.getRelationGloss(nounSynset1.getMemberMeronyms());
        meroGloss2 = SynsetUtils.getRelationGloss(nounSynset2.getMemberMeronyms());


	/* 2. Sum the scores computed for each pair of relations considered. */
        if(!gloss1.equals("") && !gloss2.equals(""))
            score += getScore(gloss1,gloss2);

        if(!gloss1.equals("") && !hypoGloss2.equals(""))
            score += getScore(gloss1,hypoGloss2);
        if(!hypoGloss1.equals("") && !gloss2.equals(""))
            score += getScore(hypoGloss1,gloss2);

        if(!gloss1.equals("") && !meroGloss2.equals(""))
            score += getScore(gloss1,meroGloss2);
        if(!meroGloss1.equals("") && !gloss2.equals(""))
            score += getScore(meroGloss1,gloss2);

        if(!hypoGloss1.equals("") && !hypoGloss2.equals(""))
            score += getScore(hypoGloss1,hypoGloss2);
        if(!meroGloss1.equals("") && !meroGloss2.equals(""))
            score += getScore(meroGloss1,meroGloss2);

        if(!meroGloss1.equals("") && !hypoGloss2.equals(""))
            score += getScore(meroGloss1,hypoGloss2);
        if(!hypoGloss1.equals("") && !meroGloss2.equals(""))
            score += getScore(hypoGloss1,meroGloss2);

        return score;
    }
}
