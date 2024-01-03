import Exceptions.CustomException;

import java.text.ParseException;

public class Main {
    public static void main(String[] args) throws ParseException, CustomException {
        TaskManager manager = new TaskManager();
        manager.runApp();

    }
}
