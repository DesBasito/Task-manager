package appRun;

import Exceptions.CustomException;
import appRun.Task;
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
                    System.out.print("Enter what do u want to change " +
                            "\n(s - status, d - description, pr - priority): ");
                    String change = sc.nextLine().strip().toLowerCase();
                    changeTask(nameOfTask, change);
                    saveToJson();
                }
                case 4 -> {
                    System.out.print("Enter the name of the title: ");
                    String name = sc.nextLine().strip();
                    deleteTask(name);
                }
                case 5 -> {
                    sortedList();
                }
                case 6 -> {
                    saveToJson();
                    brake = true;
                }
            }
        }


    }

    private void sortedList() {
        System.out.println("""
                1. Sorted tasks by priority
                2. Sorted tasks by creation date
                3. Sorted tasks by description
                """);
        int num = num("choice: ", 3);
        switch (num) {
            case 1 -> {
                sortTasksByPriority();
            }
            case 2 -> {
                sortTasksByCreationDate();
            }
            case 3 -> {
                sortTasksByDescription();
            }
        }
    }

    public void showAllTasks() {
        tasks.forEach(Task::displayTask);
    }

    private void addNewTask(String title, String description, String completionDate, String createdDate, Priority priority) throws ParseException {
        try {
            List<Task> updatedTasks = new ArrayList<>(tasks);
            updatedTasks.add(new Task(title, description, completionDate, createdDate, priority));
            tasks = updatedTasks;
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            createNewTask();
        }
    }

    private void changeTask(String nameOfTask, String change) throws CustomException {
        for (Task task : tasks) {
            if (task.getTitle().contains(nameOfTask)) {
                try {
                    switch (change) {
                        case "d" -> changeDescription(task);
                        case "s" -> changeStatus(task);
                        case "pr" -> task.setPriority(choosePriority());
                    }
                } catch (RuntimeException | CustomException e) {
                    System.out.println(e.getMessage());
                    changeTask(nameOfTask, change);
                }
            }
        }
    }

    private void deleteTask(String name) {
        Optional<Task> taskToRemove = tasks.stream()
                .filter(task -> task.getTitle().contains(name))
                .findFirst();

        if (taskToRemove.isPresent()) {
            Task task = taskToRemove.get();
            if (task.getStatus() == Status.NEW) {
                tasks = new ArrayList<>(tasks);  // Convert to a mutable list
                tasks.remove(task);
                System.out.println(CYAN + "Task successfully deleted!" + RESET);
            } else {
                System.out.println(RED + "You can only delete tasks that are in the 'NEW' status."+RESET);
            }
        } else {
            System.out.println(YELLOW+"There is no task with this name..."+RESET);
        }
    }

    public void saveToJson() {
        FileUtil.writeFile(tasks);
    }

    private List<Task> loadFromJson() throws ParseException, CustomException {
        try {
            return FileUtil.readFile();
        } catch (IOException e) {
            System.out.println(YELLOW+"""
                    There are no tasks. Would you like to create new tasks?
                     1. Yes
                     2. No, exit
                     -->
                    """ +RESET);
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
        tasks = new ArrayList<>(tasks);  // Преобразовываю в изменяемый список
        tasks = tasks.stream()
                .peek(task -> {
                    if (task.getStatus() == Status.IN_PROGRESS
                            && task.getCompletionDate().before(Date.from(Instant.now())))
                        task.setTitle(RED + task.getTitle() + " (overdue task)" + RESET);
                    else task.setTitle(GREEN + task.getTitle() + RESET);
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
                 3. Change task Status(s), Description(d) or Priority(pr)
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
            String line = new Scanner(System.in).nextLine().toLowerCase();
            switch (line) {
                case "p" -> task.setStatus(task.getStatus().changeToIN_PROGRESS(task));
                case "d" -> task.setStatus(task.getStatus(). changeToDONE(task));
            }
        } else {
            System.out.println("You can't change the task that is done.");
        }
    }

    private void createNewTask() throws ParseException {
        System.out.print("Enter the name of the title: ");
        String title = sc.nextLine().strip();
        System.out.print("Enter the description of the title: ");
        String description = sc.nextLine();
        System.out.print("Enter the completion date: ");
        String completionDate = sc.nextLine();
        System.out.print("Enter the created date: ");
        String createdDate = sc.nextLine();
        Priority priority = choosePriority();
        addNewTask(title, description, completionDate, createdDate, priority);
    }

    private Priority choosePriority() {
        System.out.println("What priority of a task do you want to choose?" +
                " ('L' - for low, 'M' - for medium, 'H' - for high): ");
        String str = sc.nextLine().toLowerCase().strip();
        Priority priority;
        switch (str) {
            case "l" -> {
                priority = Priority.LOW;
            }
            case "m" -> {
                priority = Priority.MEDIUM;
            }
            case "h" -> {
                priority = Priority.HIGH;
            }
            default -> {
                System.out.println("You entered wrong letter");
                return choosePriority();
            }
        }
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
