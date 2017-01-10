import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import configuration.operations.ConfigurationOperation;
import configuration.operations.SumSquaredOperation;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import parsers.DatabaseParser;
import parsers.DocumentParser;
import parsers.FileParser;
import parsers.ParsedDocument;
import relatedness.SynsetRelatedness;
import relatedness.embeddings.WordEmbeddingRelatedness;
import relatedness.embeddings.sense.computations.GeometricMedianComputation;
import relatedness.lesk.LeskRelatedness;
import relatedness.lesk.similarities.AdjectiveSimilarity;

import java.util.ArrayList;

class ShotgunWSD {
    @Parameter(names = "-n", description = "Length of the context windows", required = true)
    private Integer n;

    @Parameter(names = "-k", description = "Number of sense configurations considered for the voting scheme", required = true)
    private Integer k;

    @Parameter(names = "-c", description = "How many sense configurations are kept per context window")
    private Integer c = 15;

    @Parameter(names = "-minSynsetCollisions", description = "Minimum number of synset collisions used to merge context windows.")
    private Integer minSynsetCollisions = 1;

    @Parameter(names = "-maxSynsetCollisions", description = "Maximum number of synset collisions used to merge context windows.")
    private Integer maxSynsetCollisions = 4;

    @Parameter(names = {"-wn", "-wnDirectory"}, description = "Path to WordNet dictionary", required = true)
    private String wnDirectory;

    @Parameter(names = {"-we", "-wePath"}, description = "Path to Word Embeddings file path", required = true)
    private String wePath;

    @Parameter(names = "-weType", description = "Model of the Word Embeddings (Glove / Google)", required = true)
    private String weType;

    @Parameter(names = "-inputType", description = "Type of the input file ( dataset / text )", required = true)
    private String inputType;

    @Parameter(names = "-input", description = "Input file path", required = true)
    private String input;

    @Parameter(names = "-output", description = "Output file path", required = true)
    private String output;

    @Parameter(names = "--help", help = true)
    private boolean help;

    public static void main(String[] args) {
        ShotgunWSD shotgunWSD = new ShotgunWSD();

        try {
            new JCommander(shotgunWSD, args);
            shotgunWSD.run();
        } catch(ParameterException e) {
            String[] helpArgs = {"--help"};
            JCommander jCommander = new JCommander(shotgunWSD, helpArgs);
            jCommander.setProgramName("ShotgunWSD");
            jCommander.usage();
        }
    }

    public void run() {
        ShotgunWSDRunner.loadWordNet(wnDirectory);
        DocumentParser fileParser = null;

        switch (inputType) {
            case "dataset":
                fileParser = new DatabaseParser(input);
                break;
            case "text":
                fileParser = new FileParser(input);
                break;
            default:
                System.out.println("Input type is invalid! " + inputType + " is not a valid type.");
                System.exit(0);
        }

        ArrayList<ParsedDocument> documents = fileParser.parse();

        ConfigurationOperation configurationOperation = SumSquaredOperation.getInstance();

        // SynsetRelatedness synsetRelatedness = WordEmbeddingRelatedness.getInstance(wePath, weType, GeometricMedianComputation.getInstance());
        SynsetRelatedness synsetRelatedness = LeskRelatedness.getInstance();

        for(ParsedDocument document : documents) {
            ShotgunWSDRunner wsdRunner = new ShotgunWSDRunner(document, n, k, configurationOperation, synsetRelatedness);
            wsdRunner.run();
        }
    }



}