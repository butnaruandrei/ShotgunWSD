package configuration;

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

    public double getScore(){
        return score;
    }

    public int getLength(){
        return synsets.length;
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
}
