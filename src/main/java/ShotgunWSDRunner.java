import edu.smu.tspell.wordnet.WordNetDatabase;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import parsers.ParsedDocument;

import java.io.File;
import java.io.IOException;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class ShotgunWSDRunner {
    private ParsedDocument document;
    public static WordVectors wordVectors;
    public static WordNetDatabase wnDatabase;

    public static void loadWordEmbeddings(String wePath, String weType) {
        try {
            switch (weType) {
                case "Google":
                    ShotgunWSDRunner.wordVectors = WordVectorSerializer.loadGoogleModel(new File(wePath), true);
                    break;
                case "Glove":
                    ShotgunWSDRunner.wordVectors = WordVectorSerializer.loadTxtVectors(new File(wePath));
                    break;
                default:
                    System.out.println("Word Embeddings type is invalid! " + weType + " is not a valid type. Please use Google or Glove model.");
                    System.exit(0);
            }
        } catch (IOException e) {
            System.out.println("Could not find Word Embeddings file in " + wePath);
        }
    }

    public static void loadWordNet(String wnDirectory) {
        System.setProperty("wordnet.database.dir", wnDirectory);
        ShotgunWSDRunner.wnDatabase = WordNetDatabase.getFileInstance();
    }

    public ShotgunWSDRunner(ParsedDocument document) {
        this.document = document;
    }

    public void run() {

    }
}
