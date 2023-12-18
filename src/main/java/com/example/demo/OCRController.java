package com.example.demo;

import com.example.demo.TextExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OCRController {

    private final TextExtractor textExtractor;

    @Autowired
    public OCRController(TextExtractor textExtractor) {
        this.textExtractor = textExtractor;
    }

    @PostMapping("/extract-text")
    public String extractTextAndSave(@RequestParam("imagePath") String imagePath,
                                     @RequestParam("outputPath") String outputPath) {
        textExtractor.extractAndSaveText(imagePath, outputPath);
        return "Результат успешно записан в файл: " + outputPath;
    }
}
