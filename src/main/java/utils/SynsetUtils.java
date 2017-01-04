package utils;

import edu.smu.tspell.wordnet.WordNetDatabase;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class SynsetUtils {
    public static double computeConfigurationScore(int[] synsets, double[][] synsetPairScores) {
        double senseScore = 0;

        for (int i = 0; i < synsets.length - 1; i++) {
            for (int j = i + 1; j < synsets.length; j++) {
                senseScore += Math.pow(synsetPairScores[synsets[i]][synsets[j]], 2);
                senseScore += Math.pow(synsetPairScores[synsets[j]][synsets[i]], 2);
            }
        }

        return senseScore;
    }

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
