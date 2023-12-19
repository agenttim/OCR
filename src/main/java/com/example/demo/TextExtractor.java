package com.example.demo;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Component
public class TextExtractor {

    private final ITesseract tess;

    public TextExtractor() {
        this.tess = new Tesseract();
        tess.setLanguage("rus");
        tess.setTessVariable("textord_tabfind_find_tables", "1");
        tess.setTessVariable("textord_tabfind_vertical_text", "1");
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
            // Замени e.printStackTrace() на логирование
            System.err.println("Ошибка при обработке изображения: " + e.getMessage());
            return "Ошибка при обработке изображения";
        }
    }
}
