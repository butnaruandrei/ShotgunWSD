package utils;

import configuration.operations.ConfigurationOperation;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.WordSense;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class SynsetUtils {
    public static double computeConfigurationScore(int[] synsets, double[][] synsetPairScores, ConfigurationOperation configurationOperation) {
        double senseScore = configurationOperation.getInitialScore();

        for (int i = 0; i < synsets.length - 1; i++) {
            for (int j = i + 1; j < synsets.length; j++) {
                senseScore = configurationOperation.applyOperation(senseScore, synsetPairScores[synsets[i]][synsets[j]]);
                senseScore = configurationOperation.applyOperation(senseScore, synsetPairScores[synsets[j]][synsets[i]]);
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

    public static String getGloss(Synset synset) {
        String[] examples;
        String gloss;

        gloss = synset.getDefinition();
        examples = synset.getUsageExamples();
        for (String example : examples) gloss += " " + example;

        return gloss;
    }

    public static String getRelationGloss(Synset[] synsets) {
        String gloss = "";

        for(Synset synset : synsets)
            gloss += " " + getGloss(synset);

        return gloss;
    }

    public static String getRelationGloss(WordSense[] senses) {
        String gloss = "";

        for(WordSense sense : senses)
            gloss += " " + getGloss(sense.getSynset());

        return gloss;
    }

}
