package utils;

import configuration.operations.ConfigurationOperation;
import edu.smu.tspell.wordnet.*;
import relatedness.SynsetRelatedness;

import java.util.HashMap;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class SynsetUtils {
    public static ConfigurationOperation configurationOperation;
    public static SynsetRelatedness synsetRelatedness;

    public static HashMap<String, Double> cacheSynsetRelatedness;

    public static double computeConfigurationScore(int[] synsets, double[][] synsetPairScores) {
        double senseScore = configurationOperation.getInitialScore();

        for (int i = 0; i < synsets.length - 1; i++) {
            for (int j = i + 1; j < synsets.length; j++) {
                senseScore = configurationOperation.applyOperation(senseScore, synsetPairScores[synsets[i]][synsets[j]]);
                senseScore = configurationOperation.applyOperation(senseScore, synsetPairScores[synsets[j]][synsets[i]]);
            }
        }

        return senseScore;
    }

    public static Synset[] getSynsets(int[] synsetsIndex, Synset[] windowWordsSynsets){
        Synset[] returnSynsets = new Synset[synsetsIndex.length];

        for (int i = 0; i < synsetsIndex.length; i++) {
            returnSynsets[i] = windowWordsSynsets[synsetsIndex[i]];
        }

        return returnSynsets;
    }

    public static double computeConfigurationScore(Synset[] synsets, String[] words, String[] POSTags, String[] globalSynsets){

        Synset targetSynset;
        int synsetsSize = synsets.length;
        int targetPOS;

        double senseScore = configurationOperation.getInitialScore();
        String targetWord;
        String key1, key2;
        String targetGlobalSense;
        double score;


        for (int i = 0; i < synsetsSize - 1; i++) {
            targetSynset = synsets[i];
            targetWord = words[i];
            targetGlobalSense = globalSynsets[i];

            for (int j = i + 1; j < synsetsSize; j++) {
                key1 = targetGlobalSense + "||" +  globalSynsets[j];
                key2 = globalSynsets[j] + "||" + targetGlobalSense;

                if(SynsetUtils.cacheSynsetRelatedness.containsKey(key1)){
                    score = SynsetUtils.cacheSynsetRelatedness.get(key1);
                } else if(SynsetUtils.cacheSynsetRelatedness.containsKey(key2)){
                    score = SynsetUtils.cacheSynsetRelatedness.get(key2);
                } else {
                    score = synsetRelatedness.computeSimilarity(targetSynset, targetWord, synsets[j], words[j]);
                    SynsetUtils.cacheSynsetRelatedness.put(key1, score);
                }

                senseScore = configurationOperation.applyOperation(senseScore, score);
                senseScore = configurationOperation.applyOperation(senseScore, score);
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

    public static String getDefinition(Synset synset){
        return synset.getDefinition();
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

    public static String getSenseKey(Synset synset, String lemma){
        String[] allSenseKeys = synset.getSenseKeys();
        String senseKey = extractSenseKey(allSenseKeys, lemma);

        if(senseKey.isEmpty())
            return allSenseKeys[0];

        if(synset.getType() == SynsetType.ADJECTIVE_SATELLITE) {
            AdjectiveSatelliteSynset adjSynset = (AdjectiveSatelliteSynset) synset;
            String headSenseKey = adjSynset.getHeadSynset().getSenseKeys()[0];

            String headLemma = headSenseKey.split("%")[0];
            String headID = headSenseKey.split(":")[2];

            if(senseKey.length() > 0)
                senseKey = senseKey.substring(0, senseKey.length() - 1) + headLemma + ":" + headID;
        }

        return senseKey;
    }

    public static String extractSenseKey(String[] senseKeys, String lemma) {
        String senseKey = "";

        if(senseKeys.length == 0)
            return senseKey;

        for(String tmpSenseKey : senseKeys) {
            if(tmpSenseKey.contains(lemma.toLowerCase())) {
                senseKey = tmpSenseKey;
                break;
            }
        }

        return senseKey;
    }
}
