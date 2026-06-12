package ru.yandex.practicum;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Wordle {

    public static void main(String[] args) {
        try (PrintWriter log = new PrintWriter(new FileWriter("wordle.log", StandardCharsets.UTF_8));
             Scanner scanner = new Scanner(System.in)) {

            WordleDictionaryLoader loader = new WordleDictionaryLoader();
            WordleDictionary dictionary = loader.load("dictionary.txt", log);
            WordleGame game = new WordleGame(dictionary, log);

            System.out.println("Игра началась!");
            System.out.println("Нужно угадать слово из " + game.getWordLength() + " букв.");
            System.out.println("У вас " + game.getAttemptsLeft() + " попыток.");
            System.out.println("Введите слово. Пустая строка — получить подсказку.");

            while (!game.isOver()) {
                System.out.print("> ");
                String input = scanner.nextLine();

                try {
                    if (input.isBlank()) {
                        String suggestion = game.suggestWord();
                        if (suggestion.isEmpty()) {
                            System.out.println("Подсказка недоступна.");
                        } else {
                            System.out.println("Подсказка: " + suggestion);
                        }
                        continue;
                    }

                    String normalized = WordleDictionary.normalize(input);

                    String hint = game.makeGuess(normalized);
                    System.out.println(normalized);
                    System.out.println(hint);

                } catch (InvalidWordFormatException | WordNotFoundInDictionaryException e) {
                    System.out.println(e.getMessage());
                }
            }

            if (game.isWon()) {
                System.out.println("Поздравляем, вы угадали слово!");
            } else {
                System.out.println("Попытки закончились.");
            }

            System.out.println("Загаданное слово: " + game.getAnswer());

        } catch (DictionaryLoadException e) {
            System.out.println("Не удалось запустить игру: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Не удалось создать лог-файл.");
        } catch (Exception e) {
            // общий catch для неожиданных ошибок
            System.out.println("Произошла внутренняя ошибка приложения.");
        }
    }
}