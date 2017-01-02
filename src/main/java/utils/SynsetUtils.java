package utils;

import edu.smu.tspell.wordnet.WordNetDatabase;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class SynsetUtils {

    // TODO check if we need to remove this
    public static long numberOfSynsetCombination(WordNetDatabase wnDatabase, String[] documentWindow, String[] documentPOS){
        long combinations = 1;
        int length;

        for (int i = 0; i < documentWindow.length; i++) {
            length = WordUtils.extractSynsets(wnDatabase, documentWindow[i], POSUtils.asSynsetType(documentPOS[i])).length;

            combinations *= length == 0 ? 1 : length;
        }

        return combinations;
    }

}
