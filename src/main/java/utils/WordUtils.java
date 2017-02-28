package utils;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.stanford.nlp.simple.Sentence;
import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class WordUtils {
    /**
     * Extract from WordNet Database only the synsets that contains the lemma of a word
     * @param wnDatabase WordNet Database
     * @param word The word that we want to extract its synsets
     * @param synsetType POS Tag of the word
     * @return Array of Synsets
     */
    public static Synset[] extractSynsets(WordNetDatabase wnDatabase, String word, SynsetType synsetType) {
        // Extract the senses for the word
        Synset[] tmpSynsets = getSynsetsFromWord(wnDatabase, word, synsetType);

        if(tmpSynsets.length == 0) {
            tmpSynsets = getSynsetsFromWord(wnDatabase, new Sentence(word).lemma(0), synsetType);
        }

        ArrayList<Synset> synsets = new ArrayList<>();
        // Keep only the senses that the lemma is match
        for(Synset tmpSynset : tmpSynsets){
            String[] senseKeys = tmpSynset.getSenseKeys();
            for (int j = 0; j < senseKeys.length; j++) {
                if(senseKeys[j].contains(word.replace(" ", "_"))){
                    synsets.add(tmpSynset);
                    break;
                }

            }
        }

        Synset[] resultSynsets;

        if(synsets.isEmpty()) {
            resultSynsets = tmpSynsets;
        } else {
            resultSynsets = new Synset[synsets.size()];
            resultSynsets = synsets.toArray(resultSynsets);
        }

        return resultSynsets;
    }

    /**
     * Extract from WordNet Database the synsets of a word
     * @param wnDatabase WordNet Database
     * @param word The word that we want to extract its synsets
     * @param synsetType POS Tag of the word
     * @return Array of Synsets
     */
    public static Synset[] getSynsetsFromWord(WordNetDatabase wnDatabase, String word, SynsetType synsetType) {
        Synset[] tmpSynsets;

        if(synsetType == null)
            tmpSynsets = wnDatabase.getSynsets(word);
        else {
            tmpSynsets = wnDatabase.getSynsets(word, synsetType);

            // If the synset type is an adjective, check for adjective satellite too
            if (synsetType == SynsetType.ADJECTIVE) {
                tmpSynsets = (Synset[])ArrayUtils.addAll(tmpSynsets, wnDatabase.getSynsets(word, SynsetType.ADJECTIVE_SATELLITE));
            }
        }

        return tmpSynsets;
    }

    public static boolean inWordNet(WordNetDatabase wnDatabase, String word, SynsetType synsetType) {
        return wnDatabase.getSynsets(word, synsetType).length > 0;
    }
}
