package parsers;

import edu.smu.tspell.wordnet.Synset;
import edu.stanford.nlp.simple.Sentence;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class SemCorParser extends DocumentParser {
    private String filePath;
    ArrayList<ParsedDocument> documents;

    DocumentBuilderFactory dbFactory;
    DocumentBuilder dBuilder;

    Document dataset;

    public SemCorParser(String filePath) {
        this.filePath = filePath;
        this.documents = new ArrayList<>();

        this.dbFactory = DocumentBuilderFactory.newInstance();
        loadDocumentBuilder(dbFactory);
    }

    private void loadDocumentBuilder(DocumentBuilderFactory dbFactory) {
        try {
            this.dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void loadDataset(){
        try {
            dataset = dBuilder.parse(new File(filePath));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("File not found at path " + filePath);
            System.exit(0);
        }
    }

    public ArrayList<ParsedDocument> parse() {
        loadDataset();
        if(dataset == null)
            return documents;

        dataset.getDocumentElement().normalize();

        ArrayList<String> docWords, docWordsLemma, docWordsPOS, docWordsID;
        String[] documentWords, documentPOS;

        String docID, headID, headText, headLemma, headStem, headPOS;
        String[] subHeadText;

        Pattern p = Pattern.compile("d\\d+\\.s(\\d+)\\.t(\\d+)");
        Matcher m;

        NodeList contextList = dataset.getElementsByTagName("context");

        // For each context
        for (int i = 0; i < contextList.getLength(); i++) {
            Node text = contextList.item(i);
            Element textElem = (Element) text;
            docID = textElem.getAttribute("filename");

            NodeList sentenceList = text.getChildNodes();

            docWords = new ArrayList<>();
            docWordsPOS = new ArrayList<>();
            docWordsLemma = new ArrayList<>();
            docWordsID = new ArrayList<>();

            // For each sentence
            for (int j = 0; j < sentenceList.getLength(); j++) {
                Node sentence = sentenceList.item(j);

                if ("s".equals(sentence.getNodeName())) {
                    NodeList instanceList = sentence.getChildNodes();

                    // Get the words
                    for (int k = 0; k < instanceList.getLength(); k++) {
                        Node instance = instanceList.item(k);

                        if ("wf".equals(instance.getNodeName())) {
                            Element inst = (Element) instance;

                            if(inst.getAttribute("cmd").equals("done")) {
                                headText = inst.getTextContent();
                                if(inst.getAttribute("lemma").equals("UNKNOWN") && (headText.contains("-") || headText.contains("/"))) {
                                    subHeadText = headText.split("-");
                                    for (int l = 0; l < subHeadText.length; l++) {
                                        docWords.add(subHeadText[l]);
                                        docWordsPOS.add("ALL");
                                        docWordsLemma.add(new Sentence(subHeadText[l]).lemma(0));
                                        docWordsID.add(inst.getAttribute("id"));
                                    }
                                } else {
                                    // docWords.add(instance.getChildNodes().item(0).getNodeValue().toLowerCase());
                                    docWords.add(inst.getAttribute("lemma").replace("_", " ").toLowerCase());
                                    docWordsPOS.add(fixPOS(inst.getAttribute("pos")));
                                    docWordsLemma.add(inst.getAttribute("lemma"));
                                    docWordsID.add(inst.getAttribute("id"));
                                }
                            }
                        }
                    }
                }
            }

            if(docWords.size() >= 1) {
                documents.add(new ParsedDocument(docID, docWords, docWordsPOS, docWordsLemma, docWordsID));
            }

        }

        return documents;
    }

    private static String fixPOS(String pos) {
        if (pos.charAt(0) == 'n' || pos.charAt(0) == 'N') { //Noun values: NN, NNS, NNP
            return "NN";
        } else if (pos.charAt(0) == 'v' || pos.charAt(0) == 'V') { //Verb values: VB, VBD, VBN, VBZ, VBP, VBG, MD
            return "VB";
        } else if (pos.charAt(0) == 'a' || pos.charAt(0) == 'J') { //Adjectice values: JJ, JJR
            return "JJ";
        } else if (pos.charAt(0) == 'r' || pos.charAt(0) == 'R') { //Adverb values: RB, RBR
            return "RB";
        }

        return "NN";
    }
}
