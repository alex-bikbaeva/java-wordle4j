package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
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

    private final LinkedHashMap<String, String> guessesWithHints = new LinkedHashMap<>();
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

    public Map<String, String> getGuessesWithHints() {
        return new LinkedHashMap<>(guessesWithHints);
    }

    public String makeGuess(String input)
            throws InvalidWordFormatException, WordNotFoundInDictionaryException {
        if (isOver()) {
            throw new GameAlreadyFinishedException("Попытка сделать ход после завершения игры");
        }

        String guess = WordleDictionary.normalize(input);
        validateGuess(guess);

        // по ТЗ ход засчитывается, если слово прошло общую проверку ввода,
        // даже если его нет в словаре
        attemptsLeft--;

        if (!dictionary.contains(guess)) {
            log.println("Слово отсутствует в словаре: " + guess);
            throw new WordNotFoundInDictionaryException("Слово отсутствует в словаре");
        }

        String hint = buildHint(guess);
        guessesWithHints.put(guess, hint);

        if (guess.equals(answer)) {
            won = true;
        }

        log.println("Ход: " + guess + ", подсказка: " + hint + ", осталось попыток: " + attemptsLeft);
        return hint;
    }

    public String suggestWord() {
        List<String> guesses = new ArrayList<>(guessesWithHints.keySet());
        List<String> hints = new ArrayList<>(guessesWithHints.values());

        List<String> candidates = dictionary.findCandidates(guesses, hints, suggestedWords, wordLength);
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

    public static String buildHintStatic(String guess, String answer) {
        StringBuilder hint = new StringBuilder();

        boolean[] usedInAnswer = new boolean[answer.length()];

        // Сначала отмечаем точные совпадения
        char[] result = new char[guess.length()];
        for (int i = 0; i < guess.length(); i++) {
            if (guess.charAt(i) == answer.charAt(i)) {
                result[i] = EXACT_SYMBOL;
                usedInAnswer[i] = true;
            } else {
                result[i] = '\0';
            }
        }

        // Потом ищем буквы, которые есть, но не на месте
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