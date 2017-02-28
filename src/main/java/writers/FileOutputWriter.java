package writers;

import edu.smu.tspell.wordnet.Synset;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class FileOutputWriter extends DocumentWriter {
    String outputPath;

    public FileOutputWriter(String outputPath) {
        this.outputPath = outputPath;
    }

    public void write(String documentName, Synset[] synsets, String[] lemmas, String[] wordsID){

    }
}
