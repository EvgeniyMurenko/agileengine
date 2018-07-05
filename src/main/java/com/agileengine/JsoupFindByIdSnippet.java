package com.agileengine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JsoupFindByIdSnippet {

    private static Logger LOGGER = LoggerFactory.getLogger(JsoupFindByIdSnippet.class);

    private static String CHARSET_NAME = "utf8";

    public static void main(String[] args) {

        // Jsoup requires an absolute file path to resolve possible relative paths in HTML,
        // so providing InputStream through classpath resources is not a case
        String resourcePath = "./samples/sample-0-origin.html";
        String targetElementId = "make-everything-ok-button";

        //for test other page
        List<String> otherResourcePath = new ArrayList<String>(){{
            add("./samples/sample-1-evil-gemini.html");
            add("./samples/sample-2-container-and-clone.html");
            add("./samples/sample-3-the-escape.html");
            add("./samples/sample-4-the-mash.html");
        }};

        Element mainElement = findElementById(new File(resourcePath), targetElementId);

        if (mainElement != null){
            LOGGER.info("Target element attrs: [{}]", mainElement.attributes());

            for (String path: otherResourcePath) {
                findSameElement(new File(path), mainElement);
            }
        }else {
            LOGGER.warn("Target not found");
        }

    }

    private static void findSameElement(File htmlFile, Element mainElement) {
        try {
            Document doc  = getDocument(htmlFile);
            Elements links = doc.getAllElements();

            int index = 0;
            int maxCount = 0;

            for (int i = 0; i < links.size(); i++) {
                int count = 0;
                for (Attribute attr : links.get(i).attributes()){
                    if (mainElement.attributes().asList().contains(attr)){
                        count ++;
                    }
                }

                if (count > maxCount){
                    index = i;
                }
            }

            LOGGER.info("Target element attrs: [{}]", links.get(index).attributes());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static Element findElementById(File htmlFile, String targetElementId) {
        try {
            Document doc = getDocument(htmlFile);

            Element element = doc.getElementById(targetElementId);

            if (element != null){
                return element;
            }

            return null;

        } catch (IOException e) {
            LOGGER.error("Error reading [{}] file", htmlFile.getAbsolutePath(), e);
            return null;
        }
    }

    private static Document getDocument(File htmlFile) throws IOException {
        return Jsoup.parse(
                        htmlFile,
                        CHARSET_NAME,
                        htmlFile.getAbsolutePath());
    }

}