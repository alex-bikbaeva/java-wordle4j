package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

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
        assertEquals(5, game.getAttemptsLeft());
    }

    @Test
    void shouldThrowForInvalidFormat() {
        assertThrows(InvalidWordFormatException.class, () -> game.makeGuess("abcde"));
        assertEquals(6, game.getAttemptsLeft());
    }

    @Test
    void shouldBuildHint() throws Exception {
        String hint = game.makeGuess("котка");
        assertEquals("+++^-", hint);
    }

    @Test
    void shouldUpdateCorrectPositionsAfterGuess() throws Exception {
        game.makeGuess("котка");

        List<Character> correctPositions = game.getCorrectPositions();

        assertEquals('к', correctPositions.get(0));
        assertEquals('о', correctPositions.get(1));
        assertEquals('т', correctPositions.get(2));
        assertNull(correctPositions.get(3));
        assertNull(correctPositions.get(4));
    }

    @Test
    void shouldUpdatePresentLettersAfterGuess() throws Exception {
        game.makeGuess("котка");

        Set<Character> presentLetters = game.getPresentLetters();

        assertTrue(presentLetters.contains('к'));
        assertTrue(presentLetters.contains('о'));
        assertTrue(presentLetters.contains('т'));
    }

    @Test
    void shouldUpdateAbsentLettersAfterGuess() throws Exception {
        game.makeGuess("котка");

        Set<Character> absentLetters = game.getAbsentLetters();

        assertTrue(absentLetters.contains('а'));
        assertFalse(absentLetters.contains('к'));
    }

    @Test
    void shouldSuggestCandidate() throws Exception {
        game.makeGuess("котка");

        String suggestion = game.suggestWord();

        assertNotNull(suggestion);
        assertFalse(suggestion.isBlank());
        assertEquals(5, suggestion.length());
    }

    @Test
    void shouldReturnEmptySuggestionWhenNoCandidatesLeft() throws Exception {
        WordleDictionary smallDictionary = new WordleDictionary(List.of("котик"));
        WordleGame localGame = new WordleGame(smallDictionary, "котик", 6, new PrintWriter(System.out, true));

        String suggestion = localGame.suggestWord();
        assertEquals("котик", suggestion);

        String secondSuggestion = localGame.suggestWord();
        assertEquals("", secondSuggestion);
    }
}