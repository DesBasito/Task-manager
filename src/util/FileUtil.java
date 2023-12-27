package util;

import appRun.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class FileUtil {
    private FileUtil() {
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = Paths.get("data/tasks.json");
    private static final Path PATH2 = Paths.get("data/tasks.json");

    public static Task[] readFile() {
        try {
            String str = Files.readString(PATH);
            return GSON.fromJson(str, Task[].class);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return new Task[0];
        }
    }
    public static Task[] takeFile() {
        try {
            String str = Files.readString(PATH2);
            return GSON.fromJson(str, Task[].class);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return new Task[0];
        }
    }
}
