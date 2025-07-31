package org.example;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import opennlp.tools.lemmatizer.*;
import opennlp.tools.postag.*;
import opennlp.tools.sentdetect.*;
import opennlp.tools.tokenize.*;


public class CorpusBuilder {
    /**
     * Create a CorpusBuilder which generates POS tags and Lemmas for text.
     * @param text The text which should be annotated.
     */
    private String aText;
    private int leftNeighbor = 0;
    private int rightNeighbor = 0;
    private final scraper.WikiScraper scraper = new scraper.WikiScraper();
    private int count = 0;

    public CorpusBuilder() {
        aText = null;
    }

    public CorpusBuilder(String text){

        aText = text;
    }


    /**
     * Returns the text of this CorpusBuilder
     * @return The text of this CorpusBuilder
     */
    public String getText() {

        return aText;
    }

    /**
     * Return an array with the sentences of the CorpusBuilder
     * @return An array with the sentences of the CorpusBuildr
     */
    public String[] getSentences() {
        String[] sentences = null;
        try (InputStream modelIn = Files.newInputStream(Paths.get("KWICSearcher_Group4/src/main/resources/OpenNLPModel/opennlp-en-ud-ewt-sentence-1.0-1.9.3.bin"))){
            SentenceModel model = new SentenceModel(modelIn);
            SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
            sentences = sentenceDetector.sentDetect(getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sentences;
    }

    /**
     * Return a List of List with the tokens/words of the text of CorpusBuilder. The first list holds the words of the
     * first sentence, the second list holds the words of the second sentence and so on.
     * @return A List of List the tokens/words of the text of the CorpusBuilder.
     */
    public List<List<String>> getTokens() {
        List<List<String>> tokens = new ArrayList<>();

        try (InputStream modelIn = Files.newInputStream(Paths.get("KWICSearcher_Group4/src/main/resources/OpenNLPModel/opennlp-en-ud-ewt-tokens-1.0-1.9.3.bin"))) {
            TokenizerModel model = new TokenizerModel(modelIn);
            Tokenizer tokenizer = new TokenizerME(model);

            for (String sentence : getSentences()) {
                String[] tokenArray = tokenizer.tokenize(sentence);
                List<String> tokenList = new ArrayList<>(Arrays.asList(tokenArray));
                tokens.add(tokenList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tokens;
    }

    /**
     * Return a List of List with the POS tags of the text of CorpusBuilder. The first list holds the POS tags of the
     * first sentence, the second list holds the POS tags of the second sentence and so on.
     * @return A List of List with the POS tags of the text of CorpusBuilder.
     */
    public List<List<String>> getPosTags() {
        List<List<String>> posTags = new ArrayList<>();
        try (InputStream modelIn = Files.newInputStream(Paths.get("KWICSearcher_Group4/src/main/resources/OpenNLPModel/opennlp-en-ud-ewt-pos-1.0-1.9.3.bin"))) {
            POSModel model = new POSModel(modelIn);
            POSTaggerME tagger = new POSTaggerME(model);

            for (List<String> tokenList : getTokens()) {
                String[] tokensArray = tokenList.toArray(new String[0]);
                String[] tagsArray = tagger.tag(tokensArray);

                List<String> tagsList = new ArrayList<>();
                Collections.addAll(tagsList, tagsArray);
                posTags.add(tagsList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return posTags;
    }

    /**
     * Return a List of List with the Lemmas of the text of CorpusBuilder. The first list holds the lemmas of the
     * first sentence, the second list holds the Lemmas of the second sentence and so on.
     * @return A List of List with the Lemmas of the text of CorpusBuilder.
     * @return
     */
    public List<List<String>> getLemmas() {
        List<List<String>> lemmas = new ArrayList<>();
        List<List<String>> tokens = getTokens();
        List<List<String>> posTags = getPosTags();

        try (InputStream modelIn = Files.newInputStream(Paths.get("KWICSearcher_Group4/src/main/resources/OpenNLPModel/en-lemmatizer.bin"))) {
            LemmatizerModel model = new LemmatizerModel(modelIn);
            LemmatizerME lemmatizer = new LemmatizerME(model);

            for (int i = 0; i < tokens.size(); i++) {
                List<String> st = tokens.get(i);
                List<String> tmpPos = posTags.get(i);
                String[] tmpLemmas = lemmatizer.lemmatize(st.toArray(new String[0]), tmpPos.toArray(new String[0]));
                lemmas.add(Arrays.asList(tmpLemmas));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lemmas;
    }

    public void setUrl(String reUrl) {
        aText = scraper.scrapeToString(reUrl);
    }
    public void setText(String text) { aText = text; }

    public void setLeftNeighbor(int number) {
        leftNeighbor = number;
    }

    public void setRightNeighbor(int number) {
        rightNeighbor = number;
    }

    public void resetCount(){
        count = 0;
    }

    /**
     * Scrape the Wikipedia page and save the content to a file
     *
     * @param target   the target word for searching
     * @param left    the number of left neighbor(s) of the target word
     * @param right    the number of right neighbor(s) of the target word
     * @param caseSensitive to decide whether the searching is case-sensitive or not
     * @return String the searching result
     */
    public String search(String target, int left, int right, boolean caseSensitive) throws IOException {
        setLeftNeighbor(left);
        setRightNeighbor(right);

        List<List<String>> lemmas = getLemmas();
        List<List<String>> tokens = getTokens();
        List<List<String>> posTags = getPosTags();
        StringBuilder finalResult = new StringBuilder();
        finalResult.append("Target: ").append(target).append("\n\n");
        for (int i = 0; i < tokens.size(); i++) {
            List<String> tokenList = tokens.get(i);
            List<String> posList = posTags.get(i);
            List<String> lemmaList = lemmas.get(i);

            for (int k = 0; k < tokenList.size(); k++) {
                boolean match = caseSensitive ? tokenList.get(k).equals(target) : tokenList.get(k).equalsIgnoreCase(target);
                if (match) {
                    count +=1;
                    int firstIdx = Math.max(0, k - leftNeighbor);
                    int lastIdx = Math.min(tokenList.size(), k + rightNeighbor + 1);

                    StringBuilder result1 = new StringBuilder("Token:  ");
                    StringBuilder result2 = new StringBuilder("POSTag: ");
                    StringBuilder result3 = new StringBuilder("Lemma:  ");

                    for (int j = firstIdx; j < lastIdx; j++) {
                        result1.append(String.format("%-15s", tokenList.get(j)));
                        result2.append(String.format("%-15s", posList.get(j)));
                        result3.append(String.format("%-15s", lemmaList.get(j)));
                    }
                    finalResult.append(result1).append("\n")
                            .append(result2).append("\n")
                            .append(result3).append("\n\n");
                }
            }
        }
        return finalResult.toString();
    }

    public int countResult() {
        return count;
    }


}
