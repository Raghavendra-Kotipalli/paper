package com.learning.paper.service;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaperService {
    private RestTemplate restTemplate;

    public PaperService(RestTemplate restTemplate1) {
        this.restTemplate = restTemplate1;
    }

    public byte[] generatePdfFromImages(byte[][] images) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        for (byte[] imageBytes : images) {
            ImageData imageData = ImageDataFactory.create(imageBytes);
            Image image = new Image(imageData);
            document.add(image);
        }

        document.close();
        return byteArrayOutputStream.toByteArray();
    }

    public byte[] getImageInBytes(String url) throws IOException {
        try {
            byte[] imageBytes = restTemplate.getForObject(url, byte[].class);
            if (imageBytes != null) {
                return imageBytes;
            } else {
                throw new IOException("Failed to download image from " + url);
            }
        }catch (IOException e) {
            System.out.println("Failed to download image from " + url);
        }
        return null;
    }

    public void downloadImage(String url, String destinationFile) throws IOException {
        try {
            byte[] imageBytes = restTemplate.getForObject(url, byte[].class);
            if (imageBytes != null) {
                try (InputStream in = new ByteArrayInputStream(imageBytes);
                     FileOutputStream out = new FileOutputStream(destinationFile)) {
                    IOUtils.copy(in, out);
                }
            } else {
                throw new IOException("Failed to download image from " + url);
            }
        }catch (IOException e) {
            System.out.println("Failed to download image from " + url);
        }
    }

    public List<String> extractLinks(String htmlString) {
        List<String> links = new ArrayList<>();
        org.jsoup.nodes.Document document = Jsoup.parse(htmlString);
        Elements linkElements = document.select("owl-item");

        for (Element linkElement : linkElements) {
            links.add(linkElement.attr("highres"));
        }
        return links;
    }

    public String fetchHtmlContent(String url) {
        extractLinks(restTemplate.getForObject(url, String.class));
        return restTemplate.getForObject(url, String.class);
    }

    public String getHTMLContentByURL(String url) {
        try {
//            /* Fetch and parse the HTML document from the URL */
//            org.jsoup.nodes.Document document = Jsoup.connect(url).get();
//
//            // Get all 'h1' tags and convert them into objects
//            Elements h1Tags = document.select("img");
//            for (Element h1 : h1Tags) {
//                System.out.println("H1: " + h1.text());
//            }

            WebClient webClient = new WebClient();

            // Disable JavaScript and CSS (for performance)
            webClient.getOptions().setJavaScriptEnabled(false);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setDownloadImages(true);

            // Fetch the HTML page from a URL
            HtmlPage page = webClient.getPage(url);

            // Get the page's title
            String title = page.getTitleText();
            System.out.println("Title: " + title);

            // Extract all paragraph content
//            System.out.println("Body: " + page.getBody().asText());

            // Close the web client
            webClient.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
