package edu.java.scrapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestUtils {
    private TestUtils() {
    }

    public static String readFile(String fileName) {
        try {
            Path path = Paths.get(fileName);
            if (!Files.exists(path)) {
                return Files.readString(Paths.get("scrapper/" + fileName));
            }
            return Files.readString(path);
        } catch (IOException e) {
            return "[]";
        }
    }
}
