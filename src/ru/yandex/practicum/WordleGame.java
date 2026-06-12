package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WordleGame {

    public static final int DEFAULT_ATTEMPTS = 6;
    public static final char EXACT_SYMBOL = '+';
    public static final char PRESENT_SYMBOL = '^';
    public static final char ABSENT_SYMBOL = '-';

    private final WordleDictionary dictionary;
    private final String answer;
    private final int wordLength;
    private final PrintWriter log;

    private int attemptsLeft;
    private boolean won;

    private final Set<Character> absentLetters = new HashSet<>();
    private final Set<Character> presentLetters = new HashSet<>();
    private final List<Character> correctPositions = new ArrayList<>();
    private final List<Set<Character>> forbiddenPositions = new ArrayList<>();
    private final Set<String> suggestedWords = new HashSet<>();

    public WordleGame(WordleDictionary dictionary, PrintWriter log) {
        this(dictionary, dictionary.getRandomWord(), DEFAULT_ATTEMPTS, log);
    }

    public WordleGame(WordleDictionary dictionary, String answer, int attemptsLeft, PrintWriter log) {
        this.dictionary = dictionary;
        this.answer = WordleDictionary.normalize(answer);
        this.wordLength = this.answer.length();
        this.attemptsLeft = attemptsLeft;
        this.log = log;
        this.won = false;

        for (int i = 0; i < wordLength; i++) {
            correctPositions.add(null);
            forbiddenPositions.add(new HashSet<>());
        }

        log.println("Создана игра. Ответ длиной " + wordLength + ", попыток: " + attemptsLeft);
    }

    public int getAttemptsLeft() {
        return attemptsLeft;
    }

    public boolean isWon() {
        return won;
    }

    public boolean isOver() {
        return won || attemptsLeft <= 0;
    }

    public String getAnswer() {
        return answer;
    }

    public int getWordLength() {
        return wordLength;
    }

    public Set<Character> getAbsentLetters() {
        return new HashSet<>(absentLetters);
    }

    public Set<Character> getPresentLetters() {
        return new HashSet<>(presentLetters);
    }

    public List<Character> getCorrectPositions() {
        return new ArrayList<>(correctPositions);
    }

    public String makeGuess(String input)
            throws InvalidWordFormatException, WordNotFoundInDictionaryException {
        if (isOver()) {
            throw new GameAlreadyFinishedException("Попытка сделать ход после завершения игры");
        }

        String guess = WordleDictionary.normalize(input);
        validateGuess(guess);

        attemptsLeft--;

        if (!dictionary.contains(guess)) {
            log.println("Слово отсутствует в словаре: " + guess);
            throw new WordNotFoundInDictionaryException("Слово отсутствует в словаре");
        }

        String hint = buildHint(guess);
        updateKnownLetters(guess, hint);

        if (guess.equals(answer)) {
            won = true;
        }

        log.println("Ход: " + guess + ", подсказка: " + hint + ", осталось попыток: " + attemptsLeft);
        return hint;
    }

    public String suggestWord() {
        List<String> candidates = dictionary.findCandidates(
                absentLetters,
                presentLetters,
                correctPositions,
                forbiddenPositions,
                suggestedWords,
                wordLength
        );

        if (candidates.isEmpty()) {
            log.println("Подсказка не найдена: кандидатов не осталось.");
            return "";
        }

        String suggestion = candidates.get(0);
        suggestedWords.add(suggestion);
        log.println("Выдана подсказка: " + suggestion);
        return suggestion;
    }

    private void validateGuess(String guess) throws InvalidWordFormatException {
        if (guess.isBlank()) {
            throw new InvalidWordFormatException("Пустая строка не является словом");
        }
        if (!WordleDictionary.isRussianWord(guess)) {
            throw new InvalidWordFormatException("Нужно ввести слово русскими буквами");
        }
        if (guess.length() != wordLength) {
            throw new InvalidWordFormatException("Слово должно содержать " + wordLength + " букв");
        }
    }

    private String buildHint(String guess) {
        return buildHintStatic(guess, answer);
    }

    private void updateKnownLetters(String guess, String hint) {
        Set<Character> confirmedInThisGuess = new HashSet<>();

        // сначала обрабатываем точные и частичные совпадения
        for (int i = 0; i < guess.length(); i++) {
            char letter = guess.charAt(i);
            char symbol = hint.charAt(i);

            if (symbol == EXACT_SYMBOL) {
                presentLetters.add(letter);
                confirmedInThisGuess.add(letter);
                correctPositions.set(i, letter);
            } else if (symbol == PRESENT_SYMBOL) {
                presentLetters.add(letter);
                confirmedInThisGuess.add(letter);
                forbiddenPositions.get(i).add(letter);
            }
        }

        // потом аккуратно добавляем отсутствующие буквы
        for (int i = 0; i < guess.length(); i++) {
            char letter = guess.charAt(i);
            char symbol = hint.charAt(i);

            if (symbol == ABSENT_SYMBOL) {
                if (!confirmedInThisGuess.contains(letter) && !presentLetters.contains(letter)) {
                    absentLetters.add(letter);
                }
            }
        }
    }

    public static String buildHintStatic(String guess, String answer) {
        StringBuilder hint = new StringBuilder();

        boolean[] usedInAnswer = new boolean[answer.length()];
        char[] result = new char[guess.length()];

        for (int i = 0; i < guess.length(); i++) {
            if (guess.charAt(i) == answer.charAt(i)) {
                result[i] = EXACT_SYMBOL;
                usedInAnswer[i] = true;
            } else {
                result[i] = '\0';
            }
        }

        for (int i = 0; i < guess.length(); i++) {
            if (result[i] == EXACT_SYMBOL) {
                continue;
            }

            char current = guess.charAt(i);
            boolean found = false;

            for (int j = 0; j < answer.length(); j++) {
                if (!usedInAnswer[j] && answer.charAt(j) == current) {
                    found = true;
                    usedInAnswer[j] = true;
                    break;
                }
            }

            result[i] = found ? PRESENT_SYMBOL : ABSENT_SYMBOL;
        }

        for (char c : result) {
            hint.append(c);
        }

        return hint.toString();
    }
}