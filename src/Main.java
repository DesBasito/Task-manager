import Exceptions.CustomException;
import appRun.TaskManager;

import java.text.ParseException;

public class Main {
    public static void main(String[] args) throws ParseException, CustomException {
        TaskManager manager = new TaskManager();
        manager.runApp();

    }
}
