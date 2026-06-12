package ru.yandex.practicum;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WordleDictionaryTest {

    @Test
    void shouldNormalizeWord() {
        String normalized = WordleDictionary.normalize(" ЁЛКА ");
        assertEquals("елка", normalized);
    }

    @Test
    void shouldContainKnownWord() {
        WordleDictionary dictionary = new WordleDictionary(List.of("кот", "дом", "елка"));
        assertTrue(dictionary.contains("дом"));
        assertFalse(dictionary.contains("мир"));
    }

    @Test
    void shouldReturnWordsOfRequiredLength() {
        WordleDictionary dictionary = new WordleDictionary(List.of("кот", "дом", "елка", "мир"));
        List<String> words = dictionary.getWordsOfLength(3);
        assertEquals(3, words.size());
        assertTrue(words.contains("кот"));
        assertTrue(words.contains("дом"));
        assertTrue(words.contains("мир"));
    }
}