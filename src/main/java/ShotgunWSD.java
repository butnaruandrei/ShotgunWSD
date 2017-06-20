import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import configuration.operations.AddOperation;
import configuration.operations.LogOperation;
import configuration.operations.SumSquaredOperation;
import edu.smu.tspell.wordnet.Synset;
import parsers.*;
import relatedness.SynsetRelatedness;
import relatedness.embeddings.WordEmbeddingRelatedness;
import relatedness.embeddings.sense.computations.AverageComputation;
import relatedness.embeddings.sense.computations.GeometricMedianComputation;
import relatedness.embeddings.sense.computations.SenseComputation;
import relatedness.lesk.LeskRelatedness;
import utils.SynsetUtils;
import writers.DatabaseWriter;
import writers.DocumentWriter;
import writers.FileOutputWriter;

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

    @Parameter(names = "-outputType", description = "Type of the input file ( dataset / text )", required = true)
    private String outputType;

    @Parameter(names = "-input", description = "Input file path", required = true)
    private String input;

    @Parameter(names = "-output", description = "Output file path", required = true)
    private String output;

    @Parameter(names = "-configurationOperationName", description = "Configuration Operation Name", required = true)
    private String configurationOperationName;

    @Parameter(names = "-senseComputationMethod", description = "Sense Computation Method", required = true)
    private String senseComputationMethod;

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


    // Make as program attributes: synsetRelatedness, configurationOperation and senseComputation
    public void run() {
        ShotgunWSDRunner.loadWordNet(wnDirectory);
        DocumentParser fileParser = null;
        DocumentWriter fileWriter = null;

        switch (inputType) {
            case "dataset-semeval2007":
                fileParser = new SemEval2007Parser(input);
                break;
            case "dataset-semcor":
                fileParser = new SemCorParser(input);
                break;
            case "text":
                fileParser = new FileParser(input);
                FileParser.wnDatabase = ShotgunWSDRunner.wnDatabase;
                break;
            default:
                System.out.println("Input type is invalid! " + inputType + " is not a valid type.");
                System.exit(0);
        }

        switch (outputType) {
            case "dataset":
                fileWriter = new DatabaseWriter(output);
                break;
            case "text":
                fileWriter = new FileOutputWriter(output);
                break;
            default:
                System.out.println("Input type is invalid! " + inputType + " is not a valid type.");
                System.exit(0);
        }



        ArrayList<ParsedDocument> documents = fileParser.parse();

        SenseComputation senseComputation = null;
        if(senseComputationMethod.equals("geo"))
            senseComputation = GeometricMedianComputation.getInstance();
        else if(senseComputationMethod.equals("avg"))
            senseComputation = AverageComputation.getInstance();

        SynsetRelatedness synsetRelatedness = WordEmbeddingRelatedness.getInstance(wePath, weType, senseComputation);
        // SynsetRelatedness synsetRelatedness = LeskRelatedness.getInstance();

        if(configurationOperationName.equals("add2"))
            SynsetUtils.configurationOperation = SumSquaredOperation.getInstance();
        else if(configurationOperationName.equals("log"))
            SynsetUtils.configurationOperation = LogOperation.getInstance();
        else if(configurationOperationName.equals("add"))
            SynsetUtils.configurationOperation = AddOperation.getInstance();

        SynsetUtils.synsetRelatedness = synsetRelatedness;

        Synset[] results;
        long t = System.currentTimeMillis();
        System.out.println("[START]");
        for(ParsedDocument document : documents) {
            ShotgunWSDRunner wsdRunner = new ShotgunWSDRunner(document, n, c, k, minSynsetCollisions, maxSynsetCollisions, synsetRelatedness);
            results = wsdRunner.run();

            fileWriter.write(document.getDocID(), results, document.getWordLemmas(), document.getWordsID());
        }
        System.out.println("[STOP]" + (System.currentTimeMillis() - t));
    }



}