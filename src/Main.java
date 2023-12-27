import appRun.Task;
import util.FileUtil;

public class Main {
    private static final Task[] tasks = FileUtil.readFile();

    public static void main(String[] args) {
        for (Task task : tasks) {
            task.displayTask();
        }
    }
}
