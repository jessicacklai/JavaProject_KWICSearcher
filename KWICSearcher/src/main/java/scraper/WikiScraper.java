package scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class WikiScraper {
    /**
     * Method to scrape the content of a Wikipedia page and return it as a string
     *
     * @param url URL of the Wikipedia page to scrape
     * @return String containing the scraped content
     */
    public String scrapeToString(String url) {
        try {
            // Connect to the URL and parse the HTML into a Document
            Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36" +
                    " (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36").header("Accept-Language", "*").referrer("http://www.google.com").get();
            String OutputString = "";

            // Get the title of the Wikipedia page
            //String title = doc.title();
            // System.out.println("Title: " + title);
            //title = title.replace(" - Wikipedia", "");
            //OutputString += title + "\n";

            // scrape div with id bodyContent
            Element bodyContent = doc.selectFirst("div#bodyContent");
            OutputString += scrapeBodyContent(bodyContent);
            // OutputString = removeCitations(OutputString);
            return OutputString;

        } catch (IOException e) {
            // todo: think of the better error handling
            e.printStackTrace();
            System.out.println("Error: Unable to connect to the URL or retrieve the page content.");
            return null;
        }
    }

    // Method to scrape the body content of a Wikipedia page
    private String scrapeBodyContent(Element bodyContent) {
        if (bodyContent == null) {
            System.out.println("Error: Unable to find the page content.");
            return "";
        }
        List<String> titlesToEscape = new ArrayList<>();
        titlesToEscape.add("references");
        titlesToEscape.add("externallinks");
        titlesToEscape.add("seealso");
        titlesToEscape.add("notes");
        StringBuilder bodyContentString = new StringBuilder();
        // select titles and p in their correct order note that there p without titles and titles with several p below
        Elements tables = bodyContent.select("table");

        // Remove the <table> element along with its children
        tables.remove();
        Elements elementsToScrape = bodyContent.select("h1, h2, h3, h4, h5, h6, p");
        elementsToScrape.select("sup").remove();


        // Loop through the selected elements
        for (Element element : elementsToScrape) {
            String tagName = element.tagName();
            if (tagName.equals("p")) {
                bodyContentString.append(element.text()).append("\n");
            }
        }
        return bodyContentString.toString();
    }


}
