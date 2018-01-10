package shotgunwsd.utils;

import shotgunwsd.configuration.operations.ConfigurationOperation;
import shotgunwsd.configuration.weights.BaseWeight;
import edu.smu.tspell.wordnet.*;
import shotgunwsd.relatedness.SynsetRelatedness;

import java.util.HashMap;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class SynsetUtils {
    public static ConfigurationOperation configurationOperation;
    public static SynsetRelatedness synsetRelatedness;
    public static MatrixSimilarity matrixSimilarity;
    public static BaseWeight weightMethod;

    public static HashMap<String, Double> cacheSynsetRelatedness;

    public static double computeConfigurationScore(String[] synsetIDS, MatrixSimilarity matrixSimilarity) {
        double senseScore = configurationOperation.getInitialScore();
        double weight;

        for (int i = 0; i < synsetIDS.length - 1; i++) {
            for (int j = i + 1; j < synsetIDS.length; j++) {
                weight = weightMethod.weight(synsetIDS.length, i, j);

                senseScore = configurationOperation.applyOperation(senseScore, weight * matrixSimilarity.getSimilarity(synsetIDS[i], synsetIDS[j]));
                senseScore = configurationOperation.applyOperation(senseScore, weight * matrixSimilarity.getSimilarity(synsetIDS[j], synsetIDS[i]));
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

    public static String[] getSynsetIDs(int[] synsetsIndex, String[] windowSynsetIDs){
        String[] returnSynsets = new String[synsetsIndex.length];

        for (int i = 0; i < synsetsIndex.length; i++) {
            returnSynsets[i] = windowSynsetIDs[synsetsIndex[i]];
        }

        return returnSynsets;
    }

    public static double calculateConfigurationScore(String[] configurationSynsetIDS) {
        double senseScore = configurationOperation.getInitialScore();
        double score, weight;

        for (int i = 0; i < configurationSynsetIDS.length - 1; i++) {
            for (int j = i + 1; j < configurationSynsetIDS.length; j++) {
                score = matrixSimilarity.getSimilarity(configurationSynsetIDS[i], configurationSynsetIDS[j]);

                weight = weightMethod.weight(configurationSynsetIDS.length, i, j);

                senseScore = configurationOperation.applyOperation(senseScore, weight * score);
                senseScore = configurationOperation.applyOperation(senseScore, weight * score);
            }
        }

        return senseScore;
    }

    public static double computeConfigurationScore(Synset[] synsets, String[] words, String[] POSTags, String[] globalSynsets){

        Synset targetSynset;
        int synsetsSize = synsets.length;
        int targetPOS;

        double senseScore = configurationOperation.getInitialScore();
        String targetWord;
        String key1, key2;
        String targetGlobalSense;
        double score, weight;


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
                    // score = synsetRelatedness.computeSimilarity(targetSynset, targetWord, synsets[j], words[j]);
                    // score = matrixSimilarity.getSimilarity(targetGlobalSense, globalSynsets[j]);

                    score = matrixSimilarity.getSimilarity(targetSynset, targetWord,  synsets[j], words[j]);

                    SynsetUtils.cacheSynsetRelatedness.put(key1, score);
                }

                weight = weightMethod.weight(synsetsSize, i, j);

                senseScore = configurationOperation.applyOperation(senseScore, weight * score);
                senseScore = configurationOperation.applyOperation(senseScore, weight * score);
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

    public static String computeSynsetID(Synset synset, String word) {
        String senseKey = word + "-" + String.join("-", synset.getSenseKeys());


        if (synset.getType() == SynsetType.ADJECTIVE_SATELLITE) {
            AdjectiveSatelliteSynset adjSynset = (AdjectiveSatelliteSynset) synset;
            senseKey += "-" + String.join("-", adjSynset.getHeadSynset().getSenseKeys());
        }

        return senseKey;
    }
}
