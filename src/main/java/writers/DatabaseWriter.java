package writers;

import edu.smu.tspell.wordnet.Synset;
import utils.SynsetUtils;

import java.io.*;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class DatabaseWriter extends DocumentWriter {
    String outputPath;

    public DatabaseWriter(String outputPath){
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

                writer.write(documentName + " " + wordsID[i] + " " + senseKey + "\n");
            }

            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
