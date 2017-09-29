package relatedness.kernel;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import relatedness.kernel.kmeans.CosineDistance;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class MainTest {
    public static void main(String[] args) {
        String wePath = "H:\\GoogleNews-vectors-negative300.bin",
                weType = "Google",
                wnDirectory = "C:\\Users\\butna\\Desktop\\dizertatie\\WSD-GS\\dict2.1";

        KernelRelatedness rk = KernelRelatedness.getInstance(wePath, weType, 40, new CosineDistance());

        System.setProperty("wordnet.database.dir", wnDirectory);
        WordNetDatabase wnDatabase = WordNetDatabase.getFileInstance();

        Synset synset1 = wnDatabase.getSynsets("car", SynsetType.NOUN)[0];
        Synset synset2 = wnDatabase.getSynsets("car", SynsetType.NOUN)[1];

        Synset synset3 = wnDatabase.getSynsets("printer", SynsetType.NOUN)[0];
        Synset synset4 = wnDatabase.getSynsets("scanner", SynsetType.NOUN)[0];

        double r1 = rk.computeSimilarity(synset1, "car", synset2, "car");
        double r2 = rk.computeSimilarity(synset1, "car", synset3, "printer");
        double r3 = rk.computeSimilarity(synset4, "scanner", synset3, "printer");

        System.out.println(r1);
        System.out.println(r2);
        System.out.println(r3);
    }
}
