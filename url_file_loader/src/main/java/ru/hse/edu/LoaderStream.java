package ru.hse.edu;

import java.io.*;
import java.net.URI;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LoaderStream extends Thread implements Serializable {
    private static String path;
    private String url;

    public void setPath(String path) {
        LoaderStream.path = path;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void loadUrl() {
        //noinspection SynchronizeOnNonFinalField
        synchronized (url) {
            System.out.println("url = " + url);
            try (InputStream in = URI.create(this.url).toURL().openStream()) {
                Files.copy(in, Paths.get(path + "\\" + url.substring(url.lastIndexOf('/') + 1)));
            } catch (FileAlreadyExistsException fileAlreadyExistsException) {
                // Отлавливаем исключения, возникающие в следствие сохранения файла с таким же именем.
                System.out.println("Файл с таким именем уже существует.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void run() {
        loadUrl();
    }
}
