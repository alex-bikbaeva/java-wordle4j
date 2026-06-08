package ru.yandex.practicum;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class WordleDictionary {

    private final List<String> words;
    private final Set<String> wordSet;
    private final Random random = new Random();

    public WordleDictionary(List<String> words) {
        this.words = new ArrayList<>(words);
        this.wordSet = new HashSet<>(words);
    }

    public List<String> getWords() {
        return new ArrayList<>(words);
    }

    public boolean contains(String word) {
        return wordSet.contains(word);
    }

    public String getRandomWord() {
        return words.get(random.nextInt(words.size()));
    }

    public List<String> getWordsOfLength(int length) {
        List<String> result = new ArrayList<>();
        for (String word : words) {
            if (word.length() == length) {
                result.add(word);
            }
        }
        return result;
    }

    public static String normalize(String word) {
        return word.trim()
                .toLowerCase()
                .replace('ё', 'е');
    }

    public static boolean isRussianWord(String word) {
        return word.matches("[а-я]+");
    }


    public boolean matchesHistory(String candidate, List<String> guesses, List<String> hints) {
        for (int i = 0; i < guesses.size(); i++) {
            String expectedHint = WordleGame.buildHintStatic(guesses.get(i), candidate);
            if (!expectedHint.equals(hints.get(i))) {
                return false;
            }
        }
        return true;
    }

    public List<String> findCandidates(List<String> guesses, List<String> hints, Set<String> excludedWords, int length) {
        List<String> result = new ArrayList<>();
        for (String candidate : words) {
            if (candidate.length() != length) {
                continue;
            }
            if (excludedWords.contains(candidate)) {
                continue;
            }
            if (matchesHistory(candidate, guesses, hints)) {
                result.add(candidate);
            }
        }
        return result;
    }
}