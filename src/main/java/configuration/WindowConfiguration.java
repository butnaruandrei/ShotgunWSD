package configuration;

import configuration.operations.ConfigurationOperation;
import edu.smu.tspell.wordnet.Synset;
import org.apache.commons.lang3.ArrayUtils;
import utils.SynsetUtils;

import java.util.Arrays;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class WindowConfiguration {
    private double score;
    private int[] synsetIndex;
    private String[] globalSynsets;
    private int firstGlobalSense, lastGlobalSense;
    private String[] windowWords;
    private String[] windowWordsPOS;
    private Synset[] configurationSynsets;


    public static WindowConfigurationComparator windowConfigurationComparator = new WindowConfigurationComparator();

    public WindowConfiguration(){}

    public WindowConfiguration(int[] synsetIndex, String[] windowWords, String[] windowWordsPOS, Synset[] configurationSynsets, double score){
        this.synsetIndex = synsetIndex;
        this.windowWords = windowWords;
        this.windowWordsPOS = windowWordsPOS;
        this.configurationSynsets = configurationSynsets;
        this.score = score;
    }

    public WindowConfiguration(int[] synsetIndex, String[] windowWords, String[] windowWordsPOS, Synset[] configurationSynsets, String[] globalSynsets){
        this.synsetIndex = synsetIndex;
        this.globalSynsets = globalSynsets;this.windowWords = windowWords;
        this.windowWordsPOS = windowWordsPOS;
        this.configurationSynsets = configurationSynsets;

        this.score = -1;

        this.firstGlobalSense = Integer.parseInt(globalSynsets[0].split("-")[0]);
        this.lastGlobalSense = Integer.parseInt(globalSynsets[globalSynsets.length - 1].split("-")[0]);
    }

    public double getScore(){
        if(score == -1)
            score = SynsetUtils.computeConfigurationScore(configurationSynsets, windowWords, windowWordsPOS, globalSynsets);
        return score;
    }

    public int[] getSynsetsIndex(){
        return synsetIndex;
    }

    public String[] getGlobalSynsets(){
        return globalSynsets;
    }

    public int getLength(){
        return synsetIndex.length;
    }

    public int getLastGlobalSense(){
        return this.lastGlobalSense;
    }

    public String getGlobalSynset(int i){
        return globalSynsets[i];
    }

    public int getFirstGlobalSenseIndex() {
        return firstGlobalSense;
    }

    public String[] getWindowWords(){
        return windowWords;
    }

    public String[] getWindowWordsPOS(){
        return windowWordsPOS;
    }

    public Synset[] getConfigurationSynsets(){
        return configurationSynsets;
    }

    public void setGlobalIDS(int offset, int[] synset2WordIndex, int[] windowWordsSynsetStart) {
        globalSynsets = new String[getLength()];
        int wordIndex, senseIndex;

        for (int i = 0; i < getLength(); i++) {
            wordIndex = synset2WordIndex[synsetIndex[i]];
            senseIndex = synsetIndex[i] - windowWordsSynsetStart[wordIndex];

            globalSynsets[i] = "" + (offset + wordIndex) + "-" + senseIndex;
        }

        this.firstGlobalSense = Integer.parseInt(globalSynsets[0].split("-")[0]);
        this.lastGlobalSense = Integer.parseInt(globalSynsets[globalSynsets.length - 1].split("-")[0]);
    }

    public boolean containsGlobalSense(String index){
        int current = Integer.parseInt(index);

        return firstGlobalSense <= current && current <= lastGlobalSense;
    }

    public static boolean hasCollisions(WindowConfiguration window1, WindowConfiguration window2, int offset, int minSynsetCollision) {
        // There are less synsets than the minimum required collisions.
        if(window1.getLength() - offset < minSynsetCollision || window2.getLength() < minSynsetCollision) {
            return false;
        }

        if(window2.getLastGlobalSense() < window1.getLastGlobalSense()) {
            for (int i = 0; i < window2.getLength(); i++) {
                if (!window1.getGlobalSynset(i + offset).equals(window2.getGlobalSynset(i))) {
                    return false;
                }
            }
        } else {
            for (int i = offset; i < window1.getLength(); i++) {
                if (!window1.getGlobalSynset(i).equals(window2.getGlobalSynset(i - offset))) {
                    return false;
                }
            }
        }

        return true;
    }

    public static WindowConfiguration merge(WindowConfiguration window1, WindowConfiguration window2, int offset){
        if(window2.getLastGlobalSense() < window1.getLastGlobalSense())
            return null;

        int startAt = window1.getLength() - offset;

        int[] synsets = ArrayUtils.addAll(Arrays.copyOf(window1.getSynsetsIndex(), window1.getLength()), Arrays.copyOfRange(window2.getSynsetsIndex(), startAt, window2.getLength()));
        String[] globalSenses = ArrayUtils.addAll(Arrays.copyOf(window1.getGlobalSynsets(), window1.getLength()), Arrays.copyOfRange(window2.getGlobalSynsets(), startAt, window2.getLength()));
        String[] windowWords  = ArrayUtils.addAll(Arrays.copyOf(window1.getWindowWords(), window1.getLength()), Arrays.copyOfRange(window2.getWindowWords(), startAt, window2.getLength()));
        String[] windowWordsPOS  = ArrayUtils.addAll(Arrays.copyOf(window1.getWindowWordsPOS(), window1.getLength()), Arrays.copyOfRange(window2.getWindowWordsPOS(), startAt, window2.getLength()));
        Synset[] configurationSynsets = ArrayUtils.addAll(Arrays.copyOf(window1.getConfigurationSynsets(), window1.getLength()), Arrays.copyOfRange(window2.getConfigurationSynsets(), startAt, window2.getLength()));

        return new WindowConfiguration(synsets, windowWords, windowWordsPOS, configurationSynsets, globalSenses);
    }

    public WindowConfiguration clone() {
        WindowConfiguration newClone = new WindowConfiguration();

        newClone.score = this.score;
        newClone.synsetIndex = this.synsetIndex;
        newClone.globalSynsets = this.globalSynsets;
        newClone.firstGlobalSense = this.firstGlobalSense;
        newClone.lastGlobalSense = this.lastGlobalSense;
        newClone.windowWords = this.windowWords;
        newClone.windowWordsPOS = this.windowWordsPOS;
        newClone.configurationSynsets = this.configurationSynsets;

        return newClone;
    }
}
