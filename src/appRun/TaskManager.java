package appRun;

import Exceptions.CustomException;
import state.Priority;
import state.Status;
import util.FileUtil;

import java.text.ParseException;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

public class TaskManager {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    private static final Scanner sc = new Scanner(System.in);
    private List<Task> tasks;

    public TaskManager() throws ParseException, CustomException {
        this.tasks = loadFromJson();
    }

    public void runApp() throws ParseException, CustomException {
        markOverdueTasks();
        boolean brake = false;
        while (!brake) {
            displayMenu();
            int num = num("Action: ", 5);
            switch (num) {
                case 1 -> {
                    showAllTasks();
                }
                case 2 -> {
                    createNewTask();
                }
                case 3 -> {
                    System.out.print("Enter name of the task: ");
                    String nameOfTask = sc.nextLine().strip();
                    System.out.print("Enter what do u want to change (s - status, d - description): ");
                    String change = sc.nextLine().strip();
                    changeTask(nameOfTask, change);
                }
                case 4 -> {
                    String name = sc.nextLine().strip();
                    deleteTask(name);
                }
                case 5 -> {
                    sortedList();
                }
                case 6->{brake = true;}
            }
        }


    }

    private void sortedList(){
        System.out.println("""
                1. Sorted tasks by priority
                2. Sorted tasks by creation date
                3. Sorted tasks by description
                """);
        int num = num("choice: ",3);
        switch (num){
            case 1 -> {sortTasksByPriority();}
            case 2 -> {sortTasksByCreationDate();}
            case 3 -> {sortTasksByDescription();}
        }
    }

    public void showAllTasks() {
        tasks.forEach(Task::displayTask);
    }

    private void addNewTask(String title, String description, String completionDate, String createdDate, Priority priority) throws ParseException {
        tasks.add(new Task(title, description, completionDate, createdDate, priority));
    }

    private void changeTask(String nameOfTask, String change) throws CustomException {
        for (Task task : tasks) {
            if (task.getTitle().equalsIgnoreCase(nameOfTask)) {
                try {
                    switch (change) {
                        case "d", "D" -> changeDescription(task);
                        case "s", "S" -> changeStatus(task);
                    }
                    // todo saveToJson(); for saving changes in Json;
                } catch (RuntimeException | CustomException e) {
                    changeTask(nameOfTask, change);
                }
            }
        }
    }

    public void deleteTask(String name) {
        tasks.removeIf(e -> e.getTitle().equalsIgnoreCase(name));
    }

    public void saveToJson() {
        FileUtil.writeFile(tasks);
    }

    private List<Task> loadFromJson() throws ParseException, CustomException {
        try {
            return FileUtil.readFile();
        } catch (IOException e) {
            System.out.println("""
                    There are no tasks. Would you like to create new tasks?
                     1. Yes
                     2. No, exit
                    """);
            System.out.println("--> ");
            String answer = sc.nextLine().trim();
            while (true) {
                switch (answer) {
                    case "1" -> {
                        createNewTask();
                        saveToJson(); //todo либо реализуем сохранения в самих методах, либо же переносим сохранение в интерфейс
                        // Далее вызвать меню
                        loadFromJson();
                        runApp();
                    }
                    case "2" -> {
                        System.out.println("Shutting down...");
                        return null;
                    }
                    default -> System.out.println("Answer isn't correct, try again...");
                }
            }
        }
    }

    private void markOverdueTasks() {
        tasks = tasks.stream()
                .peek(task -> {
                    if (task.getStatus() == Status.IN_PROGRESS
                            && task.getCompletionDate().before(Date.from(Instant.now())))
                        task.setTitle(RED+task.getTitle() + " *"+RESET);
                    else task.setTitle(GREEN+task.getTitle()+RESET);
                })
                .toList();
    }

    private void sortTasksByPriority() {
        var result = tasks.stream()
                .sorted(Comparator.comparing(Task::getPriority))
                .toList();
        result.forEach(Task::displayTask);
    }

    private void sortTasksByCreationDate() {
        var result = tasks.stream()
                .sorted(Comparator.comparing(Task::getCreatedDate))
                .toList();
        result.forEach(Task::displayTask);
    }

    private void sortTasksByDescription() {
        var result = tasks.stream()
                .sorted(Comparator.comparing(c -> c.getDescription().getBytes().length))
                .toList();
        result.forEach(Task::displayTask);
    }

    private void displayMenu() {
        System.out.println("""
                 ===== Task Manager Menu =====
                 1. Show all tasks
                 2. Add a new task
                 3. Change task status(s) or Description(d)
                 4. Delete a task
                 5. Display the tasks by sorting.
                 6. Save and exit
                ==============================
                """);
    }

    private void changeDescription(Task task) throws CustomException {
        System.out.print("Введите новое описание задачи: ");
        String str = new Scanner(System.in).nextLine();
        task.getStatus().changeDescription(task, str);
    }

    private void changeStatus(Task task) throws CustomException {
        if (task.getStatus() != Status.DONE) {
        System.out.println("На какой статус вы хотите поменять задачу?" +
                "(Type 'p' - for In Progress and 'd' - for done): ");
            String line = new Scanner(System.in).nextLine();
            switch (line) {
                case "p", "P" -> task.setStatus(task.getStatus().changeToIN_PROGRESS(task));
                case "d", "D" -> task.setStatus(task.getStatus().changeToDONE(task));
            }
            saveToJson();
        } else {
            System.out.println("You can't change the task that is done.");
        }
    }

    private void createNewTask() throws ParseException {
        System.out.print("Enter the name of the title: ");
        String title = sc.nextLine().strip();
        String description = sc.nextLine();
        String completionDate = sc.nextLine();
        String createdDate = sc.nextLine();
        Priority priority = choosePriority();
        addNewTask(title, description, completionDate, createdDate, priority);

    }

    private Priority choosePriority() {
        System.out.println("What priority of a task do you want to choose?" +
                " ('L' - for low, 'M' - for medium, 'H' - for high): ");
        String str = sc.nextLine().toLowerCase().strip();
        Priority priority = null;
        switch (str) {
            case "l" -> {
                priority = Priority.LOW;
                return priority;
            }
            case "m" -> {
                priority = Priority.MEDIUM;
                return priority;
            }
            case "h" -> {
                priority = Priority.HIGH;
                return priority;
            }
        }
        System.out.println("You entered wrong letter");
        choosePriority();
        return priority;
    }

    private int num(String prompt, int max) {
        System.out.print(prompt);
        try {
            String str = new Scanner(System.in).nextLine();
            if (str.isEmpty() || str.isBlank()) {
                throw new NoSuchElementException(String.format("%s :", "field cannot be empty"));
            }
            if (Integer.parseInt(str) < 1 || Integer.parseInt(str) > max) {
                throw new IllegalArgumentException(String.format("%s :", "r u serious?: "));
            }
            return Integer.parseInt(str);
        } catch (NoSuchElementException | NumberFormatException e) {
            return num(e.getMessage() + " ", max);
        } catch (IllegalArgumentException e) {
            return num(e.getMessage(), max);
        }
    }

}
