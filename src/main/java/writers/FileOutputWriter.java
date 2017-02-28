package writers;

import edu.smu.tspell.wordnet.Synset;
import utils.SynsetUtils;

import java.io.*;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class FileOutputWriter extends DocumentWriter {
    String outputPath;

    public FileOutputWriter(String outputPath) {
        this.outputPath = outputPath;
    }

    public void write(String documentName, Synset[] synsets, String[] lemmas, String[] wordsID){
        BufferedWriter writer = null;
        String gloss;
        try {

            File theDir = new File(outputPath);
            if (!theDir.exists()) {
                theDir.mkdir();
            }

            FileWriter fileWriter = new FileWriter(outputPath + "\\" + documentName + ".txt");
            writer = new BufferedWriter(fileWriter);

            for (int i = 0; i < synsets.length; i++) {

                if(synsets[i] == null){
                    gloss = "";
                } else {
                    gloss = SynsetUtils.getDefinition(synsets[i]);
                }

                writer.write("Word Index: " + wordsID[i] + " Lemma: \"" + lemmas[i] + "\"\n Definition: " + gloss + "\n\n");
            }

            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
