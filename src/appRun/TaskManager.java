package appRun;

import Exceptions.CustomException;
import state.Priority;
import state.Status;
import util.FileUtil;

import java.text.ParseException;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
    public static final String BLACK = "\u001B[30m";
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
            int num = num("Action: ", 7);
            switch (num) {
                case 1 -> showAllTasks();
                case 2 -> createNewTask();
                case 3 -> {
                    System.out.print("Enter name of the task: ");
                    String nameOfTask = sc.nextLine().strip();
                    System.out.print("Enter what do u want to change " +
                            "\n(s - status, d - description, pr - priority): ");
                    String change = sc.nextLine().strip().toLowerCase();
                    changeTask(nameOfTask, change);
//                    saveToJson();
                }
                case 4 -> {
                    System.out.print("Enter the name of the title: ");
                    String name = sc.nextLine().strip();
                    deleteTask(name);
                }
                case 5 -> sortedList();
                case 6 -> {
                    saveToJson();
                    brake = true;
                }
                case 7 -> {
                    searchTasks();
                }
            }
        }
    }

    private void searchTasks() {
        System.out.println("""
                1. Search tasks by keyword
                2. Search tasks by date
                3. Search tasks by priority
                4. Go back.
                """);
        int searchOption = num("Choose search option: ", 4);

        switch (searchOption) {
            case 1 -> {
                System.out.print("Enter keyword to search: ");
                String key = sc.nextLine().strip();
                searchByKey(key);
            }
            case 2 -> {
                System.out.print("Enter date to search (M/d/yyyy): ");
                String date = sc.nextLine().strip();
                searchByDate(date);
            }
            case 3 -> {
                System.out.println("Choose priority to search:");
                Priority priority = choosePriority();
                searchByPriority(priority);
            }
            case 4->{
                System.out.println(BLACK+"Going back to black 🎸"+RESET);
            }
        }
    }

    private void searchByKey(String key) {
        List<Task> matchingTasks = tasks.stream()
                .filter(task -> task.getTitle().contains(key) || task.getDescription().contains(key))
                .toList();
        if (matchingTasks.isEmpty()) {
            System.out.println(YELLOW+"No matching tasks found."+RESET);
        } else {
            System.out.println(CYAN+"Matching tasks:"+RESET);
            matchingTasks.forEach(Task::displayTask);
        }
    }

    private void searchByDate(String date) {
        try {
            Date searchDate = new SimpleDateFormat("M/d/yyyy").parse(date);
            List<Task> matchingTasks = tasks.stream()
                    .filter(task -> task.getCreatedDate().equals(searchDate) || task.getCompletionDate().equals(searchDate))
                    .toList();

            if (matchingTasks.isEmpty()) {
                System.out.println(YELLOW+"No matching tasks found."+RESET);
            } else {
                System.out.println(CYAN+"Matching tasks:"+RESET);
                matchingTasks.forEach(Task::displayTask);
            }
        } catch (ParseException e) {
            System.out.println(RED+"Invalid date format. Please use the format M/d/yyyy."+RESET);
        }
    }

    private void searchByPriority(Priority priority) {
        List<Task> matchingTasks = tasks.stream()
                .filter(task -> task.getPriority() == priority)
                .toList();

        if (matchingTasks.isEmpty()) {
            System.out.println(YELLOW+"No matching tasks found."+RESET);
        } else {
            System.out.println(CYAN+"Matching tasks:"+RESET);
            matchingTasks.forEach(Task::displayTask);
        }
    }

    private void sortedList() {
        System.out.println(PURPLE + """
                1. Sorted tasks by priority
                2. Sorted tasks by creation date
                3. Sorted tasks by description
                """ + RESET);
        int num = num("choice: ", 3);
        switch (num) {
            case 1 -> sortTasksByPriority();
            case 2 -> sortTasksByCreationDate();
            case 3 -> sortTasksByDescription();
        }
    }

    public void showAllTasks() {
        tasks.forEach(Task::displayTask);
    }

    private void addNewTask(String title, String description, String completionDate, Priority priority) throws ParseException {
        try {
            List<Task> updatedTasks = new ArrayList<>(tasks);
            updatedTasks.add(new Task(title, description, completionDate, priority));
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
                tasks = new ArrayList<>(tasks);
                tasks.remove(task);
                System.out.println(CYAN + "Task successfully deleted!" + RESET);
            } else {
                System.out.println(RED + "You can only delete tasks that are in the 'NEW' status." + RESET);
            }
        } else {
            System.out.println(YELLOW + "There is no task with this name..." + RESET);
        }
    }

    public void saveToJson() {
        FileUtil.writeFile(tasks);
    }

    private List<Task> loadFromJson() throws ParseException, CustomException {
        try {
            List<Task> loadedTasks = FileUtil.readFile();
            if (loadedTasks.isEmpty()) {
                System.out.println(YELLOW + """
                    There are no tasks. Would you like to create new tasks?
                     1. Yes
                     2. No, exit
                     -->
                    """ + RESET);
                String answer = sc.nextLine().trim();
                switch (answer) {
                    case "1" -> {
                        createNewTask();
                        saveToJson();
                        return loadedTasks;
                    }
                    case "2" -> {
                        System.out.println(BLUE + "Shutting down..." + RESET);
                        return loadedTasks;
                    }
                    default -> {
                        System.out.println(RED + "Answer isn't correct, try again..." + RESET);
                        return loadFromJson(); // Return statement added here
                    }
                }
            }
            return loadedTasks;
        } catch (IOException e) {
            System.out.println(RED + "You don't have any tasks, create one, press 2: " + RESET);
            return new ArrayList<>();
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
                .sorted(Comparator.comparing(Task::getDescription))
                .toList();
        result.forEach(Task::displayTask);
    }

    private void displayMenu() {
        System.out.println(PURPLE + """
                 ===== Task Manager Menu =====
                 1. Show all tasks
                 2. Add a new task
                 3. Change task Status(s), Description(d) or Priority(pr)
                 4. Delete a task
                 5. Display the tasks by sorting.
                 6. Save and exit
                 7. Search tasks
                ==============================
                """ + RESET);
    }

    private void changeDescription(Task task) {
        try {
            System.out.print("Enter the new description for the task: ");
            String newDescription = sc.nextLine().strip();
            task.getStatus().changeDescription(task, newDescription);
        } catch (CustomException e) {
            System.out.println(e.getMessage());
        }
    }


    private void changeStatus(Task task) throws CustomException {
        if (task.getStatus() != Status.DONE) {
            System.out.println("На какой статус вы хотите поменять задачу?" +
                    "(Type 'p' - for In Progress and 'd' - for done): ");
            String line = new Scanner(System.in).nextLine().toLowerCase();
            switch (line) {
                case "p" -> task.setStatus(task.getStatus().changeToIN_PROGRESS(task));
                case "d" -> {
                    task.setStatus(task.getStatus().changeToDONE(task));
                    setRating(task);
                }
            }
        } else {
            System.out.println(RED + "You can't change the task that is done." + RESET);
        }
    }

    private void createNewTask() throws ParseException {
        if(tasks == null){
            tasks = new ArrayList<>();
        }
        System.out.print("Enter the name of the title: ");
        String title = sc.nextLine().strip();
        tasks.forEach(o -> {
            if (o.getTitle().contains(title)) {
                System.out.println(RED + "this name is already defined!" + RESET);
                try {
                    createNewTask();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        System.out.print("Enter the description of the title: ");
        String description = sc.nextLine();
        System.out.print("Enter the completion date: ");
        String completionDate = sc.nextLine();
        Priority priority = choosePriority();
        addNewTask(title, description, completionDate, priority);
    }

    private Priority choosePriority() {
        System.out.println("What priority of a task do you want to choose?" +
                " ('L' - for low, 'M' - for medium, 'H' - for high): ");
        String str = sc.nextLine().toLowerCase().strip();
        Priority priority;
        switch (str) {
            case "l" -> priority = Priority.LOW;
            case "m" -> priority = Priority.MEDIUM;
            case "h" -> priority = Priority.HIGH;
            default -> {
                System.out.println(RED + "You entered wrong letter" + RESET);
                return choosePriority();
            }
        }
        return priority;
    }

    private void setRating(Task task) throws CustomException {
            System.out.println("On a scale of 1 to 5, how would you rate the completion of this task?");
            int rating = num("Rating: ", 5);
            switch (rating) {
                case 1, 2, 3, 4, 5 -> task.getStatus().setRating(task, rating);
                default -> System.out.println("Invalid rating. Please choose a rating between 1 and 5.");
            }
//            saveToJson();
    }


    private int num(String prompt, int max) {
        System.out.print(prompt);
        try {
            String str = new Scanner(System.in).nextLine();
            if (str.isEmpty() || str.isBlank()) {
                throw new NoSuchElementException(String.format(RED + "%s :" + RESET, "field cannot be empty"));
            }
            if (Integer.parseInt(str) < 1 || Integer.parseInt(str) > max) {
                throw new IllegalArgumentException(String.format(RED + "%s :" + RESET, "choose only available numbers: "));
            }
            return Integer.parseInt(str);
        } catch (NoSuchElementException | NumberFormatException e) {
            return num(e.getMessage() + " ", max);
        } catch (IllegalArgumentException e) {
            return num(e.getMessage(), max);
        }
    }

}
