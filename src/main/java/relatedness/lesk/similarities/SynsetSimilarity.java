package relatedness.lesk.similarities;

import edu.smu.tspell.wordnet.Synset;
import org.tartarus.snowball.ext.PorterStemmer;
import relatedness.SynsetRelatedness;
import utils.SynsetUtils;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class SynsetSimilarity {
    public static double similarity(Synset synset1, Synset synset2) {
        double score;
        String gloss1,gloss2;

    /* 1. Compute glosses for each synset. */
        gloss1 = SynsetUtils.getGloss(synset1);
        gloss2 = SynsetUtils.getGloss(synset2);

    /* 2. Compute the overlap score of the two glosses. */
        score = getScore(gloss1,gloss2);

        return score;
    }

    protected static double getScore(String gloss1, String gloss2) {
        if (gloss1 == null || gloss2 == null || gloss1.equals("") || gloss1.equals(""))
            return 0;

        int i,j,k;

        PorterStemmer stemmer = new PorterStemmer();
        String[] glossWords1 = gloss1.split("[^a-zA-Z\']+");
        String[] glossWords2 = gloss2.split("[^a-zA-Z\']+");

	/* 1. Eliminate stop words from each gloss. */
        glossWords1 = SynsetRelatedness.eliminateStopWordsFromWordSet(glossWords1);
        glossWords2 = SynsetRelatedness.eliminateStopWordsFromWordSet(glossWords2);

        double logMeanGlossSize = (glossWords1.length + glossWords2.length) * 0.5;
        if (logMeanGlossSize > 1.0)
            logMeanGlossSize = Math.log(logMeanGlossSize * 2.7182818);

	/* 2. Apply the stemmer on each gloss word. */
        for (i = 0; i < glossWords1.length; i++) {
            stemmer.setCurrent(glossWords1[i].toLowerCase());
            stemmer.stem();
            glossWords1[i] = stemmer.getCurrent();
        }

        for (i = 0; i < glossWords2.length; i++) {
            stemmer.setCurrent(glossWords2[i].toLowerCase());
            stemmer.stem();
            glossWords2[i] = stemmer.getCurrent();
        }

	/* 3. Compute overlap score of the two glosses. */
        double score = 0;
        int maxOverlapSize, maxOverlapIndex1, maxOverlapIndex2;
        boolean glossSizeHit;

        do {
		/* 4. Determine the maximal overlap between gloss1 and gloss2 at this moment. */
            maxOverlapIndex1 = -1;
            maxOverlapIndex2 = -1;
            maxOverlapSize = 0;

            for (i = 0; i < glossWords1.length; i++) {
                for (j = 0; j < glossWords2.length; j++) {
                    glossSizeHit = false;
                    for (k = 0; !glossSizeHit && glossWords1[i + k].equals(glossWords2[j + k]); k++)
                        if (i + k + 1 >= glossWords1.length || j + k + 1 >= glossWords2.length)
                            glossSizeHit = true;

                    if (k > maxOverlapSize) {
                        maxOverlapSize = k;
                        maxOverlapIndex1 = i;
                        maxOverlapIndex2 = j;
                    }
                }
            }

		/* 5. If there is an overlap we must remove it and increase the score. */
            if (maxOverlapSize != 0) {
			/* 6. Add the "squared overlap size" to the score. */
                score += maxOverlapSize * maxOverlapSize;

			/* 7. Remove the maximal overlap for the two glosses and reiterate until there no more overlaps. */
                String[] auxWords = new String[glossWords1.length - maxOverlapSize];
                for (i = 0; i < maxOverlapIndex1; i++)
                    auxWords[i] = glossWords1[i];

                for (i = maxOverlapIndex1 + maxOverlapSize; i < glossWords1.length; i++)
                    auxWords[i - maxOverlapSize] = glossWords1[i];

                glossWords1 = auxWords;

                auxWords = new String[glossWords2.length - maxOverlapSize];
                for (i = 0; i < maxOverlapIndex2; i++)
                    auxWords[i] = glossWords2[i];

                for (i=maxOverlapIndex2 + maxOverlapSize; i < glossWords2.length; i++)
                    auxWords[i - maxOverlapSize] = glossWords2[i];

                glossWords2 = auxWords;
            }
        } while (maxOverlapSize != 0);

        score /= logMeanGlossSize;

        return score;
    }

}
