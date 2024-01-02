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
    private static final Scanner sc = new Scanner(System.in);
    private List<Task> tasks;

    public TaskManager() throws ParseException {
        this.tasks = loadFromJson();
    }

    public void runApp() throws ParseException, CustomException {
        boolean brake = false;
        while (!brake){
            displayMenu();
            int num = num("Action: ",5);
            switch (num){
                case 1 -> {showAllTasks();}
                case 2 -> {createNewTask();}
                case 3 ->{
                    System.out.print("Enter name of the task: ");
                    String nameOfTask = sc.nextLine().strip();
                    System.out.print("Enter what do u want to change (s - status, d - description): ");
                    String change = sc.nextLine().strip();
                    changeTask(nameOfTask,change);
                }
                case 4 ->{
                    String name = sc.nextLine().strip();
                    deleteTask(name);
                }
                case 5 ->{brake = true;}
            }
        }


    }

    public void showAllTasks() {
        tasks.forEach(Task::displayTask);
    }

    private void addNewTask(String title, String description, String completionDate, String createdDate, Priority priority) throws ParseException {
        tasks.add(new Task(title, description, completionDate, createdDate, priority));
    }

    private void changeTask(String nameOfTask, String change) throws CustomException {
        for(Task task: tasks){
            if(task.getTitle().equalsIgnoreCase(nameOfTask)){
                try {
                    switch (change) {
                        case "d", "D" -> changeDescription(task);
                        case "s", "S" -> changeStatus(task);
                    }
                } catch (RuntimeException | CustomException e){
                    changeTask(nameOfTask, change);
                }
            }
        }
    }

    public void deleteTask(String name) {
        tasks.removeIf(e -> e.getTitle().equalsIgnoreCase(name));
    }

    private void saveToJson() {
        FileUtil.writeFile(tasks);
    }

    private List<Task> loadFromJson() throws ParseException {
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
                        saveToJson(); // либо реализуем сохранения в самих методах, либо же переносим сохранение в интерфейс
                        // Далее вызвать меню
                        loadFromJson();
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
                        task.setTitle(task.getTitle() + "*");
                })
                .toList();
    }

    private void sortTasksByPriority() {
        var result = tasks.stream()
                .sorted(Comparator.comparing(Task::getPriority))
                .toList();
    }

    private void sortTasksByCreationDate() {
        var result = tasks.stream()
                .sorted(Comparator.comparing(Task::getCreatedDate))
                .toList();
    }

    private void sortTasksByDescription() {
        var result = tasks.stream()
                .sorted(Comparator.comparing(Task::getDescription))
                .toList();
    }

    private void displayMenu() {
        System.out.println("""
        ===== Task Manager Menu =====
        1. Show all tasks
        2. Add a new task
        3. Change task status(s) or Description(d)
        4. Delete a task
        5. Save and exit
       ==============================
       """);
    }

    private void changeDescription(Task task) throws CustomException {
        System.out.print("Введите новое описание задачи: ");
        String str = new Scanner(System.in).nextLine();
        task.getStatus().changeDescription(task, str);
    }

    private void changeStatus(Task task) throws CustomException {
        if(task.getStatus() != Status.DONE){
            String line = new Scanner(System.in).nextLine();
            System.out.println("На какой статус вы хотите поменять задачу?" +
                    "(Type 'p' - for In Progress and 'd' - for done): ");
            switch (line) {
                case "p", "P" -> task.setStatus(task.getStatus().changeToIN_PROGRESS(task));
                case "d", "D" -> task.setStatus(task.getStatus().changeToDONE(task));
            }
        } else{
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

    private Priority choosePriority(){
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
