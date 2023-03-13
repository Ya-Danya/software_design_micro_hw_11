package ru.hse.edu;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class App {
    public static void main( String[] args ) throws InterruptedException {
        // Осуществление выбора папки для сохранения файлов по-умолчанию.
        Scanner scanner = new Scanner(System.in);
        Path path;
        boolean flag = true;
        String str_path = null;

        Path serialized_data =  Paths.get("path.json");
        // Десериализация пути для сохранения файлов.
        try(ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(serialized_data))) {
            str_path = (String)ois.readObject();
            System.out.println("Путь был восстановлен из прошлой сессии.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Не удалось получить путь для сохранения файлов из предыдущей сессии.");
        }

        // Если путь не удалось восстановить, запрашиваем у пользователя путь.
        if (str_path == null) {
            // Пока пользователь не введет корректный путь или не напишет exit, продолжаем настойчиво требовать путь.
            do {
                // Если ввод осуществляется не в первый раз, а то есть была совершена ошибка,
                // то выводится сообщение о некорректном вводе.
                if (flag) {
                    System.out.println("Введите путь по которому вы будете сохранять загружаемые файлы." +
                            " (Если вы хотите завершить работу приложения, введите exit");
                } else {
                    System.out.println("Введенный путь не существует, пожалуйста, введите корректный путь." +
                            " (Если вы хотите завершить работу приложения, введите exit");
                }
                flag = false;
                // Читаем строку из консоли, если она равна exit, завершаем работу программы.
                str_path = scanner.nextLine();
                if (str_path.equals("exit")) {
                    return;
                } else {
                    path = Paths.get(str_path);
                }
            } while (!Files.exists(path));
        }

        while (true) {
            System.out.println("Выберете действие программы (Введите help для получения списка комманд)");
            String string = scanner.nextLine();
            String[] words = string.split(" ");
            switch (words[0]){
                case "help" :
                    System.out.println("Введите select path для изменения пути сохранения файлов.");
                    System.out.println("Введите load и [url] для скачивания через пробел для того, чтобы скачать файл.");
                    System.out.println("Введите exit для того, чтобы завершить работу приложения.");
                    break;
                case "path" :
                    do {
                        System.out.println("Введите путь по которому вы будете сохранять загружаемые файлы.");
                        path = Paths.get(scanner.nextLine());
                    } while (!Files.exists(path));
                case "load":
                    if (words.length == 2) {
                        LoaderStream loaderStream = new LoaderStream();
                        loaderStream.setPath(str_path);
                        String url = words[1];
                        loaderStream.setUrl(url);
                        loaderStream.start();
                    } else {
                        for (int i = 1; i < words.length; i++) {
                            LoaderStream loaderStream = new LoaderStream();
                            loaderStream.setPath(str_path);
                            String url = words[i];
                            loaderStream.setUrl(url);
                            loaderStream.start();
                        }
                    }
                    break;
                case "exit":
                    try(ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(serialized_data))) {
                        oos.writeObject(str_path);
                    }
                    catch(Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                    return;
            }
        }

    }
}
