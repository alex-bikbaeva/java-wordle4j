package ru.yandex.practicum;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class WordleDictionaryLoader {

    public WordleDictionary load(String filename, PrintWriter log) throws DictionaryLoadException {
        List<String> words = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String normalized = WordleDictionary.normalize(line);
                if (!normalized.isBlank() && WordleDictionary.isRussianWord(normalized)) {
                    words.add(normalized);
                }
            }
        } catch (IOException e) {
            log.println("Ошибка чтения словаря: " + e.getMessage());
            throw new DictionaryLoadException("Не удалось загрузить словарь", e);
        }

        if (words.isEmpty()) {
            log.println("Словарь пуст.");
            throw new DictionaryLoadException("Словарь пуст");
        }

        return new WordleDictionary(words);
    }
}