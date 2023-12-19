package com.example.demo;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Component
public class TextExtractor {

    private final ITesseract tess;

    public TextExtractor() {
        this.tess = new Tesseract();
        tess.setLanguage("rus");

        tess.setPageSegMode(1);


        // Здесь ты можешь задать дополнительные настройки Tess4J, если необходимо


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

    public void extractAndSaveTextFromPDF(String pdfPath, String outputPath) {
        try {
            PDDocument document = Loader.loadPDF(new File(pdfPath));
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            for (int page = 0; page < document.getNumberOfPages(); ++page) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
                String imagePath = outputPath + "/page_" + (page + 1) + ".png";
                ImageIO.write(bim, "png", new File(imagePath));
                String extractedText = tess.doOCR(new File(imagePath));
                writeTextToFile(extractedText, outputPath + "/text_page_" + (page + 1) + ".txt");
            }

            document.close();
        } catch (IOException | TesseractException e) {
            System.err.println("Ошибка при обработке PDF: " + e.getMessage());
        }
    }
}
