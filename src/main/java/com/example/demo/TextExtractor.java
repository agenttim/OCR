package com.example.demo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.tess4j.Tesseract;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;

@Component
public class TextExtractor {

    private final ITesseract tess;

    @Value("${cache.path}")
    private String cachePath;

    public TextExtractor() {
        this.tess = new Tesseract();
        tess.setLanguage("rus+eng");
        tess.setPageSegMode(1);
        tess.setOcrEngineMode(1);

    }

    public void extractAndSaveText(String imagePath, String outputPath) {
        try {
            String extractedText = tess.doOCR(new File(imagePath));
            writeTextToFile(extractedText, outputPath);
        } catch (TesseractException e) {
            // Замени e.printStackTrace() на логирование
            System.err.println("Ошибка при обработке изображения: " + e.getMessage());
        }
    }

    private void writeTextToFile(String text, String outputPath) {
        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write(text);
            System.out.println("Результат успешно записан в файл: " + outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String extractText(String imagePath) {
        try {
            return tess.doOCR(new File(imagePath));
        } catch (TesseractException e) {
            System.err.println("Ошибка при обработке изображения: " + e.getMessage());
            return "Ошибка при обработке изображения";
        }
    }

    public String extractTextFromPDF(String pdfPath) {
        try {
            PDDocument document = Loader.loadPDF(new File(pdfPath));
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            List<String> results = new ArrayList<>();

            for (int page = 0; page < document.getNumberOfPages(); ++page) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
                String extractedText = tess.doOCR(bim);
                results.add(extractedText);
            }

            document.close();
            return String.join("\n", results);
        } catch (IOException | TesseractException e) {
            System.err.println("Ошибка при обработке PDF: " + e.getMessage());
            return "Ошибка при обработке PDF: " + e.getMessage();
        }
    }

    public String extractTextFromPDF(String pdfPath, String cachePath) {
        try {
            PDDocument document = Loader.loadPDF(new File(pdfPath));
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            List<String> results = new ArrayList<>();

            for (int page = 0; page < document.getNumberOfPages(); ++page) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 600, ImageType.BINARY);
                String imagePath = cachePath + "/page_" + (page + 1) + ".png";
                ImageIO.write(bim, "png", new File(imagePath));
                String extractedText = tess.doOCR(bim);
                results.add(extractedText);
            }

            document.close();
            return String.join("\n", results);
        } catch (IOException | TesseractException e) {
            System.err.println("Ошибка при обработке PDF: " + e.getMessage());
            return "Ошибка при обработке PDF: " + e.getMessage();
        }
    }
}
