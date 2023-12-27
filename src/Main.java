import appRun.Task;
import util.FileUtil;

import java.util.List;

public class Main {
    private static final List<Task> tasks = FileUtil.readFile();

    public static void main(String[] args) {
        for (Task task : tasks) {
            task.displayTask();
        }
    }
}
