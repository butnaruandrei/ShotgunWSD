package relatedness;

import edu.smu.tspell.wordnet.Synset;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public abstract class SynsetRelatedness {
    public abstract Object[] computeSynsetRepresentations(Synset[] windowWordsSynsets, String[] windowWords, int[] synset2WordIndex);
    public abstract double computeSimilarity(Object[] synsetRepresentations, int i, int j);
}
