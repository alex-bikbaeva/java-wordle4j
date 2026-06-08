package ru.yandex.practicum;

import org.junit.jupiter.api.Test;

import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.*;

public class WordleDictionaryLoaderTest {

    @Test
    void shouldThrowWhenFileDoesNotExist() {
        WordleDictionaryLoader loader = new WordleDictionaryLoader();
        assertThrows(DictionaryLoadException.class,
                () -> loader.load("file_that_does_not_exist.txt", new PrintWriter(System.out, true)));
    }
}