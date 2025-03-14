package com.example;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.File;

public class App {
    public static void main(String[] args) {
       File file = new File("/app/myapp/example.pdf");
       try {
	    PDDocument document = Loader.loadPDF(file);
	    PDFTextStripper textStripper = new PDFTextStripper();
	    String text = textStripper.getText(document);
            System.out.println("Extrated Text:");
            System.out.println(text);
	    document.close();
        } catch (IOException e) {
            System.err.println("Error loading or processing the PDF file.");
	    e.printStackTrace();
        }
    }
}
