package ru.yandex.practicum;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class WordleDictionary {

    private final List<String> words;
    private final Random random = new Random();

    public WordleDictionary(List<String> words) {
        this.words = new ArrayList<>(words);
    }

    public List<String> getWords() {
        return new ArrayList<>(words);
    }

    public boolean contains(String word) {
        return words.contains(word);
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

    public List<String> findCandidates(Set<Character> absentLetters,
                                       Set<Character> presentLetters,
                                       List<Character> correctPositions,
                                       List<Set<Character>> forbiddenPositions,
                                       Set<String> excludedWords,
                                       int length) {
        List<String> result = new ArrayList<>();

        for (String candidate : getWordsOfLength(length)) {
            if (excludedWords.contains(candidate)) {
                continue;
            }

            boolean rejected = false;

            // 1. Слово не должно содержать буквы, которых точно нет
            for (Character absent : absentLetters) {
                if (candidate.indexOf(absent) >= 0) {
                    rejected = true;
                    break;
                }
            }
            if (rejected) {
                continue;
            }

            // 2. Слово должно содержать все буквы, которые точно есть
            for (Character present : presentLetters) {
                if (candidate.indexOf(present) < 0) {
                    rejected = true;
                    break;
                }
            }
            if (rejected) {
                continue;
            }

            // 3. В известных позициях должны стоять известные буквы
            for (int i = 0; i < correctPositions.size(); i++) {
                Character correct = correctPositions.get(i);
                if (correct != null && candidate.charAt(i) != correct) {
                    rejected = true;
                    break;
                }
            }
            if (rejected) {
                continue;
            }

            // 4. Буквы не должны стоять в запрещённых позициях
            for (int i = 0; i < forbiddenPositions.size(); i++) {
                if (forbiddenPositions.get(i).contains(candidate.charAt(i))) {
                    rejected = true;
                    break;
                }
            }
            if (rejected) {
                continue;
            }

            result.add(candidate);
        }

        return result;
    }
}