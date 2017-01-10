package relatedness.lesk.similarities;

import edu.smu.tspell.wordnet.AdjectiveSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import utils.SynsetUtils;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class AdjectiveSimilarity extends SynsetSimilarity {
    public static double similarity(Synset synset1, String word1, Synset synset2, String word2) {
        double score = 0;

        String gloss1,gloss2,
                alsoGloss1,alsoGloss2,
                attrGloss1,attrGloss2,
                antoGloss1,antoGloss2,
                simGloss1,simGloss2,
                pertainymsGloss1,pertainymsGloss2;

        AdjectiveSynset adjSynset1 = (AdjectiveSynset) synset1;
        AdjectiveSynset adjSynset2 = (AdjectiveSynset) synset2;

	/* 1. Compute glosses for each relation. */
        gloss1 = SynsetUtils.getGloss(adjSynset1);
        gloss2 = SynsetUtils.getGloss(adjSynset2);

        alsoGloss1 = SynsetUtils.getRelationGloss(adjSynset1.getRelated());
        alsoGloss2 = SynsetUtils.getRelationGloss(adjSynset2.getRelated());

        attrGloss1 = SynsetUtils.getRelationGloss(adjSynset1.getAttributes());
        attrGloss2 = SynsetUtils.getRelationGloss(adjSynset2.getAttributes());

        simGloss1 = SynsetUtils.getRelationGloss(adjSynset1.getSimilar());
        simGloss2 = SynsetUtils.getRelationGloss(adjSynset2.getSimilar());

        antoGloss1 = SynsetUtils.getRelationGloss(adjSynset1.getAntonyms(word1));
        antoGloss2 = SynsetUtils.getRelationGloss(adjSynset2.getAntonyms(word2));

        pertainymsGloss1 = SynsetUtils.getRelationGloss(adjSynset1.getPertainyms(word1));
        pertainymsGloss2 = SynsetUtils.getRelationGloss(adjSynset2.getPertainyms(word2));

	/* 2. Sum the scores computed for each pair of relations considered. */
        if(!gloss1.equals("") && !gloss2.equals(""))
            score += getScore(gloss1,gloss2);

        if(!gloss1.equals("") && !alsoGloss2.equals(""))
            score += getScore(gloss1,alsoGloss2);
        if(!alsoGloss1.equals("") && !gloss2.equals(""))
            score += getScore(alsoGloss1,gloss2);

        if(!gloss1.equals("") && !attrGloss2.equals(""))
            score += getScore(gloss1,attrGloss2);
        if(!attrGloss1.equals("") && !gloss2.equals(""))
            score += getScore(attrGloss1,gloss2);

        if(!gloss1.equals("") && !simGloss2.equals(""))
            score += getScore(gloss1,simGloss2);
        if(!simGloss1.equals("") && !gloss2.equals(""))
            score += getScore(simGloss1,gloss2);

        if(!gloss1.equals("") && !antoGloss2.equals(""))
            score += getScore(gloss1,antoGloss2);
        if(!antoGloss1.equals("") && !gloss2.equals(""))
            score += getScore(antoGloss1,gloss2);

        if(!gloss1.equals("") && !pertainymsGloss2.equals(""))
            score += getScore(gloss1, pertainymsGloss2);
        if(!pertainymsGloss1.equals("") && !gloss2.equals(""))
            score += getScore(pertainymsGloss1, gloss2);

        return score;
    }

    public static boolean synsetTypeAdjective(SynsetType pos1, SynsetType pos2) {
        return pos1 == SynsetType.ADJECTIVE && pos2 == SynsetType.ADJECTIVE ||
                pos1 == SynsetType.ADJECTIVE_SATELLITE && pos2 == SynsetType.ADJECTIVE_SATELLITE ||
                pos1 == SynsetType.ADJECTIVE && pos2 == SynsetType.ADJECTIVE_SATELLITE ||
                pos1 == SynsetType.ADJECTIVE_SATELLITE && pos2 == SynsetType.ADJECTIVE;
    }
}
