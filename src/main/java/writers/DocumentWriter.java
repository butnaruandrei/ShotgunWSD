package writers;

import edu.smu.tspell.wordnet.Synset;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public abstract class DocumentWriter {
    public abstract void write(String documentName, Synset[] synsets, String[] lemmas, String[] wordsID);
}
