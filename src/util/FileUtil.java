package util;

import appRun.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileUtil {
    private FileUtil() {
    }

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .setDateFormat("dd.MM.yyyy") // Updated date format pattern
            .create();

    private static final Path PATH = Paths.get("data/tasksList.json");

    public static List<Task> readFile() throws IOException {
        String str = Files.readString(PATH);
        return GSON.fromJson(str, new TypeToken<List<Task>>() {}.getType());
    }

    public static void writeFile(List<Task> tasks) {
        String json = GSON.toJson(tasks);
        try {
            Files.writeString(PATH, json);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
