import configuration.WindowConfiguration;
import configuration.WindowConfigurationByLegthAndValueComparator;
import configuration.operations.ConfigurationOperation;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import it.unimi.dsi.fastutil.Hash;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import parsers.ParsedDocument;
import relatedness.SynsetRelatedness;
import utils.POSUtils;
import utils.SynsetUtils;
import utils.WordUtils;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class ShotgunWSDRunner {
    private ParsedDocument document;
    public static WordVectors wordVectors;
    public static WordNetDatabase wnDatabase;

    private int minWindowSize;
    private int maxWindowSize;
    private int numberConfigs;
    private int numberOfVotes;
    private int minSynsetCollisions;
    private int maxSynsetCollisions;

    private SynsetRelatedness synsetRelatedness;

    // TODO check if we can remove this threshold
    private long maxSynsetCombinationNumber = 1000000000; // The maximum number of possible synset combinations that a context window can have

    public static void loadWordNet(String wnDirectory) {
        System.setProperty("wordnet.database.dir", wnDirectory);
        ShotgunWSDRunner.wnDatabase = WordNetDatabase.getFileInstance();
    }

    /**
     * @param document      The document that we want to desambiguate
     * @param minWindowSize Min length of the context windows
     * @param maxWindowSize Max length of the context windows
     * @param numberConfigs How many sense configurations are kept per context window
     * @param numberOfVotes Number of sense configurations considered for the voting scheme
     */
    public ShotgunWSDRunner(ParsedDocument document, int minWindowSize, int maxWindowSize, int numberConfigs, int numberOfVotes, int minSynsetCollisions, int maxSynsetCollisions, SynsetRelatedness synsetRelatedness) {
        this.document = document;
        this.minWindowSize = minWindowSize;
        this.maxWindowSize = maxWindowSize;
        this.numberConfigs = numberConfigs;
        this.numberOfVotes = numberOfVotes;
        this.minSynsetCollisions = minSynsetCollisions;
        this.maxSynsetCollisions = maxSynsetCollisions;

        this.synsetRelatedness = synsetRelatedness;

        SynsetUtils.cacheSynsetRelatedness = new HashMap<>();


    }

    public Synset[] run() {
        Hashtable<Integer, List<WindowConfiguration>> documentWindowSolutions;

        if(Automation.backupDocumentWindowSolutions.containsKey(document.getDocID())) {
            documentWindowSolutions = Automation.clone(Automation.backupDocumentWindowSolutions.get(document.getDocID()));
        } else {
            documentWindowSolutions = computeWindows();
            Automation.backupDocumentWindowSolutions.put(document.getDocID(), Automation.clone(documentWindowSolutions));
        }

        mergeWindowSolutions(documentWindowSolutions);

        String[] senseVotes = voteSenses(documentWindowSolutions);

        String[] senses = selectSenses(documentWindowSolutions, senseVotes);

        String[] finalSenses = detectMostUsedSenses(senses);

        Synset[] convertedSynsets = convertFinalSynsets(finalSenses);

        return convertedSynsets;
    }

    /**
     * Generates all possible window configurations for the document, and computes the disambiguation locally for those windows
     */
    private Hashtable<Integer, List<WindowConfiguration>> computeWindows() {
        String[] windowWords, windowWordsPOS;
        long combinations = 0;
        List<WindowConfiguration> windowSolutions, joinedWindowSolutions;
        Hashtable<Integer, List<WindowConfiguration>> documentWindowSolutions = new Hashtable<>();

        for (int windowSize = minWindowSize; windowSize <= maxWindowSize; windowSize++) {
            for (int wordIndex = 0; wordIndex <= document.wordsLength() - windowSize; wordIndex++) {
                windowWords = Arrays.copyOfRange(document.getWords(), wordIndex, wordIndex + windowSize);
                windowWordsPOS = Arrays.copyOfRange(document.getWordPos(), wordIndex, wordIndex + windowSize);

                combinations = SynsetUtils.numberOfSynsetCombination(wnDatabase, windowWords, windowWordsPOS);
                while (combinations > maxSynsetCombinationNumber) {
                    windowWords = Arrays.copyOfRange(windowWords, 0, windowWords.length - 2);
                    windowWordsPOS = Arrays.copyOfRange(windowWordsPOS, 0, windowWordsPOS.length - 2);
                    combinations = SynsetUtils.numberOfSynsetCombination(wnDatabase, windowWords, windowWordsPOS);
                }

                System.out.println("Start Local ShotgunWSD from word " + wordIndex + "; Number of combinations: " + combinations);
                ShotgunWSDLocal localWSD = new ShotgunWSDLocal(wordIndex, windowWords, windowWordsPOS, numberConfigs, synsetRelatedness);
                localWSD.run(wnDatabase);
                windowSolutions = localWSD.getWindowSolutions();

                if(documentWindowSolutions.containsKey(wordIndex)) {
                    joinedWindowSolutions = documentWindowSolutions.get(wordIndex);
                    joinedWindowSolutions.addAll(windowSolutions);

                    documentWindowSolutions.put(wordIndex, joinedWindowSolutions);
                } else {
                    documentWindowSolutions.put(wordIndex, windowSolutions);
                }

            }
        }

        return documentWindowSolutions;
    }

    /**
     * Merges window configurations that have in common suffixes and prefixes
     * @param documentWindowSolutions
     * @return
     */
    private Hashtable<Integer, List<WindowConfiguration>> mergeWindowSolutions(Hashtable<Integer, List<WindowConfiguration>> documentWindowSolutions) {
        Hashtable<Integer, List<WindowConfiguration>> mergedWindows = null;

        for (int synsetCollisions = maxSynsetCollisions; synsetCollisions >= minSynsetCollisions; synsetCollisions--) {
            mergedWindows = mergeWindows(documentWindowSolutions, document.wordsLength(), synsetCollisions);
        }

        return mergedWindows;
    }

    private Hashtable<Integer, List<WindowConfiguration>> mergeWindows(Hashtable<Integer, List<WindowConfiguration>> documentWindowSolutions, int numberOfWords, int synsetCollisions){
        List<WindowConfiguration> configList1, configList2;
        WindowConfiguration window1, window2, mergedWindow;

        boolean collided = false;

        // l - the l word from the document
        for (int l = 0; l < numberOfWords - 1; l++) {
            if (documentWindowSolutions.containsKey(l)) {
                configList1 = documentWindowSolutions.get(l);

                // i - the i window of the l word
                for (int i = 0; i < configList1.size(); i++) {
                    window1 = configList1.get(i);

                    // j - the j word of the i window OR the (l + j) word of the document
                    for (int j = 0; j < window1.getLength() - synsetCollisions; j++) {
                        if(documentWindowSolutions.containsKey(j + l + 1)) {
                            configList2 = documentWindowSolutions.get(j + l + 1);

                            // k - the index of the window, of the (l + j) word from the document, we want to merge with
                            for (int k = 0; k < configList2.size(); k++) {
                                collided = false;
                                window2 = configList2.get(k);

                                if(WindowConfiguration.hasCollisions(window1, window2, j + 1, synsetCollisions)) {
                                    mergedWindow = WindowConfiguration.merge(window1, window2, j + 1);

                                    if (mergedWindow != null) {
                                        collided = true;
                                        configList1.add(mergedWindow);
                                    }

                                    configList2.remove(k);
                                    k--;
                                }
                            }
                        }
                    }

//                    if(collided)
//                        configList1.remove(i);
                }
            }
        }

        return documentWindowSolutions;
    }

    /**
     *
     * @param indexedWordSenses
     * @return
     */
    public String[] voteSenses(Hashtable<Integer, List<WindowConfiguration>> indexedWordSenses) {
        Hashtable<Integer, HashMap<String, Double>> senseIndexedCounts = new Hashtable<>();
        List<WindowConfiguration> tmpIndexedList;
        HashMap<String, Double> tmp;
        String globalSynset;
        int idx;
        double weight;

        List<WindowConfiguration> allWindows = new ArrayList<>();
        indexedWordSenses.values().forEach(allWindows::addAll);

        Collections.sort(allWindows, (a1, a2) -> Integer.compare(a2.getLength(), a1.getLength()));

        WindowConfigurationByLegthAndValueComparator windowComparator = new WindowConfigurationByLegthAndValueComparator();

        for (int l = 0; l < document.wordsLength(); l++) {
            String keyStart = Integer.toString(l);
            int noOfWindows = 0;

            tmpIndexedList = allWindows.stream().filter(w -> w.containsGlobalSense(keyStart)).collect(Collectors.toCollection(ArrayList::new));
            tmpIndexedList = extractWSDWindows(tmpIndexedList);

            try {
                Collections.sort(tmpIndexedList, windowComparator);
            } catch (Exception e) {
                System.out.println("sda");
            }

            for (WindowConfiguration wsd : tmpIndexedList) {
                if (noOfWindows == numberOfVotes)
                    break;

                if (wsd.containsGlobalSense(keyStart)) {
                    weight = Math.log(wsd.getLength());
                    noOfWindows++;

                    globalSynset = wsd.getGlobalSynset(l - wsd.getFirstGlobalSenseIndex());
                    idx = Integer.parseInt(globalSynset.split("-")[0]);

                    if (senseIndexedCounts.containsKey(idx)) {
                        tmp = senseIndexedCounts.get(idx);

                        if (tmp.containsKey(globalSynset)) {
                            tmp.put(globalSynset, tmp.get(globalSynset) + weight);
                        } else {
                            tmp.put(globalSynset, weight);
                        }
                    } else {
                        tmp = new HashMap<>();
                        tmp.put(globalSynset, weight);
                    }

                    senseIndexedCounts.put(idx, tmp);

                }
            }

        }

        String[] results = new String[document.wordsLength()];
        double[] max = new double[document.wordsLength()];
        double val;

        for (int i = 0; i < document.wordsLength(); i++) {
            if (senseIndexedCounts.containsKey(i)) {
                tmp = senseIndexedCounts.get(i);

                for (String key : tmp.keySet()) {
                    val = tmp.get(key);

                    if (val > max[i]) {
                        results[i] = key;
                        max[i] = val;
                    } else if (val == max[i]) {
                        results[i] = null;
                    }
                }
            }
        }

        return results;
    }

    public List<WindowConfiguration> extractWSDWindows(List<WindowConfiguration> allWSDWindows){
        List<WindowConfiguration> returnSenses = new ArrayList<>();

        int tmpSize = 0;
        for (WindowConfiguration allWSDWindow : allWSDWindows) {
            if (tmpSize != allWSDWindow.getLength()) {
                if (returnSenses.size() >= numberOfVotes)
                    break;

                tmpSize = allWSDWindow.getLength();
            }

            returnSenses.add(allWSDWindow);
        }

        return returnSenses;
    }

    private String[] selectSenses(Hashtable<Integer, List<WindowConfiguration>> documentWindowSolutions, String[] senseVotes) {
        String[] finalSynsets = new String[document.wordsLength()];
        int[] synsetWindowSize = new int[document.wordsLength()];
        double[] synsetWindowScore = new double[document.wordsLength()];
        int tmpListSize;
        List<WindowConfiguration> tmpList;
        WindowConfiguration wsd;
        int maxLength;
        String lngth;

        for (int i = 0; i < document.wordsLength(); i++) {
            tmpList = documentWindowSolutions.get(i);

            if(tmpList != null) {
                tmpListSize = tmpList.size();

                if (tmpListSize > 0) {
                    Collections.sort(tmpList, new Comparator<WindowConfiguration>(){
                        public int compare(WindowConfiguration a1, WindowConfiguration a2) {
                            return a2.getLength() - a1.getLength(); // assumes you want biggest to smallest
                        }
                    });

                    maxLength = tmpList.get(0).getLength();

                    for (int j = 0; j < tmpListSize; j++) {
                        wsd = tmpList.get(j);

                        if(wsd.getLength() < maxLength)
                            break;

                        for (int k = 0; k < wsd.getLength(); k++) {
                            if(senseVotes == null || senseVotes[i + k] == null){
                                if(finalSynsets[i + k] == null ||
                                        wsd.getLength() > synsetWindowSize[i + k] ||
                                        wsd.getLength() == synsetWindowSize[i + k] && wsd.getScore() > synsetWindowScore[i + k]){

                                    finalSynsets[i + k] = wsd.getGlobalSynset(k);
                                    synsetWindowSize[i + k] = wsd.getLength();
                                    synsetWindowScore[i + k] = wsd.getScore();
                                }
                            } else {
                                finalSynsets[i + k] = senseVotes[i + k];
                            }
                        }
                    }
                }
            }
        }

        return finalSynsets;
    }

    public Synset[] convertFinalSynsets(String[] finalSenses){
        int wordIndex, senseIndex;
        String[] split;
        Synset[] synsets = new Synset[finalSenses.length];

        for (int i = 0; i < finalSenses.length; i++) {
            if(finalSenses[i] == null){
                synsets[i] = null;
            } else {
                split = finalSenses[i].split("-");

                wordIndex = Integer.parseInt(split[0]);
                senseIndex = Integer.parseInt(split[1]);

                synsets[i] = getSynset(wordIndex, senseIndex);
            }
        }

        return synsets;
    }

    public Synset getSynset(int wordIndex, int senseIndex) {
        Synset[] tmpSynsets;
        Synset synset;


        tmpSynsets = WordUtils.getSynsetsFromWord(wnDatabase, document.getWord(wordIndex), POSUtils.asSynsetType(document.getWordPos(wordIndex)));

        if(tmpSynsets.length == 0) {
            synset = null;
        } else {
            synset = tmpSynsets[senseIndex];
        }

        return synset;
    }

    private String[] detectMostUsedSenses(String[] senses) {
        // For each word in the document, count how many times it appears with each sense
        HashMap<String, HashMap<String, Integer>> wordSenseCount = new HashMap<>();
        HashMap<String, Integer> tmpWordSenseCount;
        String tmpSynsetIndex;

        for (int i = 0; i < document.wordsLength(); i++) {
            if(senses[i] != null) {
                tmpSynsetIndex = senses[i].split("-")[1];

                if (wordSenseCount.containsKey(document.getWord(i) + "||" + document.getWordPos(i))) {
                    tmpWordSenseCount = wordSenseCount.get(document.getWord(i) + "||" + document.getWordPos(i));

                    if (tmpWordSenseCount.containsKey(tmpSynsetIndex)) {
                        tmpWordSenseCount.put(tmpSynsetIndex, tmpWordSenseCount.get(tmpSynsetIndex) + 1);
                    } else {
                        tmpWordSenseCount.put(tmpSynsetIndex, 1);
                    }
                } else {
                    tmpWordSenseCount = new HashMap<>();
                    tmpWordSenseCount.put(tmpSynsetIndex, 1);

                    wordSenseCount.put(document.getWord(i) + "||" + document.getWordPos(i), tmpWordSenseCount);
                }
            }
        }

        // Remove words that appears only with one sense in the whole document
        String key;
        for (int i = 0; i < document.wordsLength(); i++) {
            key = document.getWord(i) + "||" + document.getWordPos(i);

            if(wordSenseCount.containsKey(key) && wordSenseCount.get(key).keySet().size() == 1){
                wordSenseCount.remove(key);
            }
        }

        HashMap<String, String> finalWordSenseCount = new HashMap<>();

        int maxCount;
        String senseIdx;
        boolean remove;

        for(String wordSenseKey : wordSenseCount.keySet()) {
            maxCount = -1;
            senseIdx = "";
            remove = false;

            for(String senseCount : wordSenseCount.get(wordSenseKey).keySet()) {
                if(wordSenseCount.get(wordSenseKey).get(senseCount) > maxCount) {
                    remove = false;
                    maxCount = wordSenseCount.get(wordSenseKey).get(senseCount);
                    senseIdx = senseCount;
                } else if(wordSenseCount.get(wordSenseKey).get(senseCount) == maxCount) {
                    remove = true;
                }
            }

            if(!remove) {
                finalWordSenseCount.put(wordSenseKey, senseIdx);
            }

        }

        String[] results = new String[document.wordsLength()];

        for (int i = 0; i < document.wordsLength(); i++) {
            if(finalWordSenseCount.containsKey(document.getWord(i) + "||" + document.getWordPos(i)))
                results[i] = Integer.toString(i) + "-" + finalWordSenseCount.get(document.getWord(i) + "||" + document.getWordPos(i));
            else
                results[i] = senses[i];
        }

        return results;
    }
}