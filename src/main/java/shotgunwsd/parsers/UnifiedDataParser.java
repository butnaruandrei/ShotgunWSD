package shotgunwsd.parsers;

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

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class UnifiedDataParser extends DocumentParser {
    private String filePath;
    ArrayList<ParsedDocument> documents;

    DocumentBuilderFactory dbFactory;
    DocumentBuilder dBuilder;

    Document dataset;

    public UnifiedDataParser(String filePath) {
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

    private void loadDataset() {
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
        if (dataset == null)
            return documents;

        dataset.getDocumentElement().normalize();

        ArrayList<String> docWords, docWordsLemma, docWordsPOS, docWordsID;
        String docID;
        NodeList textList = dataset.getElementsByTagName("text");

        // For each document
        for (int i = 0; i < textList.getLength(); i++) {
            Node text = textList.item(i);
            Element textElem = (Element) text;
            docID = textElem.getAttribute("id");

            NodeList sentenceList = text.getChildNodes();

            docWords = new ArrayList<>();
            docWordsPOS = new ArrayList<>();
            docWordsLemma = new ArrayList<>();
            docWordsID = new ArrayList<>();

            // For each sentence
            for (int j = 0; j < sentenceList.getLength(); j++) {
                Node sentence = sentenceList.item(j);


                if ("sentence".equals(sentence.getNodeName())) {

                    NodeList instanceList = sentence.getChildNodes();

                    // Get the words
                    for (int k = 0; k < instanceList.getLength(); k++) {
                        Node instance = instanceList.item(k);

                        if("wf".equals(instance.getNodeName())) {
                            Element inst = (Element) instance;
                            if(acceptPOS(inst.getAttribute("pos"))){
                                docWords.add(inst.getAttribute("lemma").replace("_", " ").toLowerCase());
                                docWordsPOS.add(fixPOS(inst.getAttribute("pos")));
                                docWordsLemma.add(inst.getAttribute("lemma").replace("_", " ").toLowerCase());
                                docWordsID.add("");
                            }
                        }else if ("instance".equals(instance.getNodeName())) {
                            Element inst = (Element) instance;

                            docWords.add(inst.getAttribute("lemma").replace("_", " ").toLowerCase());
                            docWordsPOS.add(fixPOS(inst.getAttribute("pos")));
                            docWordsLemma.add(inst.getAttribute("lemma").replace("_", " ").toLowerCase());
                            docWordsID.add(inst.getAttribute("id"));

                        }
                    }
                }
            }

            documents.add(new ParsedDocument(docID, docWords, docWordsPOS, docWordsLemma, docWordsID));
        }

        return documents;
    }

    private static String fixPOS(String pos) {
        switch (pos) {
            case "NOUN":
                return "NN";
            case "VERB":
                return "VB";
            case "ADJ":
                return "JJ";
            case "ADV":
                return "RB";
        }

        return "NN";
    }

    private static boolean acceptPOS(String pos) {
        switch (pos) {
            case "NOUN":
                return true;
            case "VERB":
                return true;
            case "ADJ":
                return true;
            case "ADV":
                return true;
        }

        return false;
    }
}