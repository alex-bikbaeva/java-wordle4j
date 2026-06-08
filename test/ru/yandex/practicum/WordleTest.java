package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WordleTest {

    private WordleDictionary dictionary;
    private WordleGame game;

    @BeforeEach
    void setUp() {
        dictionary = new WordleDictionary(List.of("котик", "котка", "домик", "топик", "ротик"));
        game = new WordleGame(dictionary, "котик", 6, new PrintWriter(System.out, true));
    }

    @Test
    void shouldDecreaseAttemptsAfterValidGuess() throws Exception {
        game.makeGuess("домик");
        assertEquals(5, game.getAttemptsLeft());
    }

    @Test
    void shouldWinWhenWordGuessed() throws Exception {
        String hint = game.makeGuess("котик");
        assertEquals("+++++", hint);
        assertTrue(game.isWon());
        assertTrue(game.isOver());
    }

    @Test
    void shouldThrowWhenWordNotInDictionary() {
        assertThrows(WordNotFoundInDictionaryException.class, () -> game.makeGuess("лампа"));
        assertEquals(5, game.getAttemptsLeft()); // ход засчитан
    }

    @Test
    void shouldThrowForInvalidFormat() {
        assertThrows(InvalidWordFormatException.class, () -> game.makeGuess("abcde"));
        assertEquals(6, game.getAttemptsLeft()); // это невалидный ввод, ход не засчитан
    }

    @Test
    void shouldBuildHint() throws Exception {
        String hint = game.makeGuess("котка");
        assertEquals("+++^-", hint);
    }

    @Test
    void shouldSuggestCandidate() throws Exception {
        game.makeGuess("котка");
        String suggestion = game.suggestWord();
        assertNotNull(suggestion);
        assertFalse(suggestion.isBlank());
    }
}