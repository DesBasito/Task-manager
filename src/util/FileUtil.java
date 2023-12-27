package util;

import appRun.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class FileUtil {
    private FileUtil() {
    }

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .setDateFormat("M/d/yyyy")
            .create();

    private static final Path PATH = Paths.get("data/tasks list.json");

    public static List<Task> readFile() {
        try {
            String str = Files.readString(PATH);
            return GSON.fromJson(str, new TypeToken<List<Task>>() {}.getType());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
