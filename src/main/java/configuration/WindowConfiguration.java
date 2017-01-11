package configuration;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class WindowConfiguration {
    private double score;
    private int[] synsets;
    private String[] globalSynsets;

    public static WindowConfigurationComparator windowConfigurationComparator = new WindowConfigurationComparator();

    public WindowConfiguration(int[] synsets, double score){
        this.synsets = synsets;
        this.score = score;
    }

    public WindowConfiguration(int[] synsets, String[] globalSynsets){
        this.synsets = synsets;
        this.globalSynsets = globalSynsets;
        this.score = -1;
    }

    public double getScore(){
        return score;
    }

    public int[] getSynsets(){
        return synsets;
    }

    public String[] getGlobalSynsets(){
        return globalSynsets;
    }

    public int getLength(){
        return synsets.length;
    }

    public String getGlobalSynset(int i){
        return globalSynsets[i];
    }

    public void setGlobalIDS(int offset, int[] synset2WordIndex, int[] windowWordsSynsetStart) {
        globalSynsets = new String[getLength()];
        int wordIndex, senseIndex;

        for (int i = 0; i < getLength(); i++) {
            wordIndex = synset2WordIndex[synsets[i]];
            senseIndex = synsets[i] - windowWordsSynsetStart[wordIndex];

            globalSynsets[i] = "" + (offset + wordIndex) + "-" + senseIndex;
        }
    }

    public static boolean hasCollisions(WindowConfiguration window1, WindowConfiguration window2, int offset, int minSynsetCollision) {
        // There are less synsets than the minimum required collisions.
        if(window1.getLength() - offset < minSynsetCollision || window2.getLength() < minSynsetCollision) {
            return false;
        }

        for (int i = offset; i < window1.getLength(); i++) {
            if(!window1.getGlobalSynset(i).equals(window2.getGlobalSynset(i - offset))){
                return false;
            }
        }

        return true;
    }

    public static WindowConfiguration merge(WindowConfiguration window1, WindowConfiguration window2, int offset){
        int startAt = window1.getLength() - offset;

        int[] synsets = ArrayUtils.addAll(Arrays.copyOf(window1.getSynsets(), window1.getLength()), Arrays.copyOfRange(window2.getSynsets(), startAt, window2.getLength()));
        String[] globalSenses = ArrayUtils.addAll(Arrays.copyOf(window1.getGlobalSynsets(), window1.getLength()), Arrays.copyOfRange(window2.getGlobalSynsets(), startAt, window2.getLength()));

        return new WindowConfiguration(synsets, globalSenses);
    }
}
