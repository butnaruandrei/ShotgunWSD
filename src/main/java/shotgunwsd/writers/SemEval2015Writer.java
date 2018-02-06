package shotgunwsd.writers;

import edu.smu.tspell.wordnet.Synset;
import shotgunwsd.utils.SynsetUtils;

import java.io.*;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class SemEval2015Writer extends DocumentWriter {
    String outputPath;

    public SemEval2015Writer(String outputPath){
        this.outputPath = outputPath;
    }

    public void write(String documentName, Synset[] synsets, String[] lemmas, String[] wordsID){
        BufferedWriter writer = null;
        String senseKey;
        try {

            File theDir = new File(outputPath);
            if (!theDir.exists()) {
                theDir.mkdir();
            }

            FileWriter fileWriter = new FileWriter(outputPath + "\\" + documentName + ".txt");
            writer = new BufferedWriter(fileWriter);

            for (int i = 0; i < synsets.length; i++) {

                if(synsets[i] == null){
                    senseKey = "";
                } else {
                    senseKey = SynsetUtils.getSenseKey(synsets[i], lemmas[i]);
                }

                writer.write(wordsID[i] + "\t" + wordsID[i] + "\twn:" + senseKey + "\n");
            }

            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
