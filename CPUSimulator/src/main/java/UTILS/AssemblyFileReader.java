package UTILS;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AssemblyFileReader {
    public static String readAssemblyFromFile(String filePath) {
        assert filePath != null && !filePath.isBlank(): "Filepath must not be null or empty";
        assert Files.exists(Paths.get(filePath)) : "No file found at '" + filePath + "'";

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert !sb.isEmpty() : "File content must not be empty";

        return sb.toString();
    }
}
