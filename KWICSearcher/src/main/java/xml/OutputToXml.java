package xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.example.CorpusBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class OutputToXml {
    private static ArrayList<String[]> outputArray;
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    CorpusBuilder cb = new CorpusBuilder();

    public void createXmlWithInput(String output, String filePath, int id) {
        try {
            // Create a DocumentBuilderFactory
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            // Create a DocumentBuilder
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            // Create a new Document
            Document document = documentBuilder.newDocument();

            // Create root element
            Element root = document.createElement("outputs");
            document.appendChild(root);

            // Create output element
            Element outputElement = document.createElement("searchResult");
            root.appendChild(outputElement);

            // Set the id attribute
            outputElement.setAttribute("id", String.valueOf(id)); // "1" since that's the only case


            // Create a Transformer to write the document to a file

            Transformer transformer = transformerFactory.newTransformer();




            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(filePath));

            // Transform the DOM Object to an XML File
            transformer.transform(domSource, streamResult);

            processOutput(output); // Use of first argument
            appendTokenInfo(filePath);


        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

    private void appendTokenInfo(String filePath){
        try {

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new File(filePath));

            // Get the root element
            NodeList nodeList = document.getElementsByTagName("searchResult");
            Element root = (Element) nodeList.item(0);


            int tokenIndex = 1;
            int posTagIndex = 2;
            int lemmaIndex = 3;
            int run = 0;
            int result = 0;
            int idx = 1;
            while (result < outputArray.size() / 3) {
                Element resultElement = document.createElement("result");
                root.appendChild(resultElement);
                String target = outputArray.get(0)[0];
                // Set the id attribute for each result
                resultElement.setAttribute(target, String.valueOf(idx));
                for (int i = 0; i < outputArray.get(tokenIndex).length; i++) {
                    //Create the element node <tokenInfo>
                    Element tokenInfoElement = document.createElement("tokenInfo");
                    resultElement.appendChild(tokenInfoElement);
                    //Create the element node <token>
                    Element tokenElement = document.createElement("token");
                    //Create and append the text node
                    tokenElement.appendChild(document.createTextNode(outputArray.get(tokenIndex)[run]));
                    tokenInfoElement.appendChild(tokenElement);

                    //Create the element node <posTag>
                    Element posTagElement = document.createElement("posTag");
                    //Create and append the text node
                    posTagElement.appendChild(document.createTextNode(outputArray.get(posTagIndex)[run]));
                    tokenInfoElement.appendChild(posTagElement);

                    //Create the element node <lemma>
                    Element lemmaElement = document.createElement("lemma");
                    //Create and append the text node
                    lemmaElement.appendChild(document.createTextNode(outputArray.get(lemmaIndex)[run]));
                    tokenInfoElement.appendChild(lemmaElement);
                    run++;
                }
                tokenIndex+=3;
                posTagIndex+=3;
                lemmaIndex+=3;
                run = 0;
                result++;
                idx++;
            }
            Transformer transformer = transformerFactory.newTransformer();

            // Add Indentation for every parts
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(filePath));

            // Transform the DOM Object to an XML File
            transformer.transform(domSource, streamResult);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }
    private void processOutput(String nlpOutput) {

        String[] tmpArray;
        String[] tokenInfoArray;
        outputArray = new ArrayList<>();

        Scanner scanner = new Scanner(nlpOutput);
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            // filter out empty lines
            if(!line.trim().isEmpty()) {
                tmpArray = line.split("\\s+");
                tokenInfoArray = new String[tmpArray.length - 1];

                for (int i = 1; i < tmpArray.length; i++) {
                    tokenInfoArray[i-1] = tmpArray[i];
                }
                outputArray.add(tokenInfoArray);
            }


        }
        scanner.close();

    }

}

