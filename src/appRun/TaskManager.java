package appRun;

import Exceptions.CustomException;
import state.Priority;
import state.Status;
import util.FileUtil;

import java.io.IOException;
import java.text.ParseException;
import java.time.Instant;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class TaskManager {
    private static final Scanner sc = new Scanner(System.in);
    private List<Task> tasks;

    public TaskManager() throws ParseException {
        this.tasks = loadFromJson();
    }

    public void showAllTasks() {
        for (Task task : tasks) {
            task.displayTask();
        }
    }

    public void addNewTask(String title, String description, String completionDate, String createdDate, Priority priority) throws ParseException {
        tasks.add(new Task(title, description, completionDate, createdDate, priority));
    } {

    }

    public void changeTask(String nameOfTask, String change) throws CustomException {
        for(Task task: tasks){
            if(task.getTitle().equalsIgnoreCase(nameOfTask)){
                try {
                    switch (change){
                        case "d", "D":
                            changeDescription(task);
                            break;
                        case "s", "S":
                           changeStatus(task);
                           break;
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

    public void saveToJson() {
        FileUtil.writeFile(tasks);
    }

    public List<Task> loadFromJson() throws ParseException {
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
                    case "1":
                        createNewTask();
                        saveToJson(); // либо реализуем сохранения в самих методах, либо же переносим сохранение в интерфейс
                        // Далее вызвать меню
                        loadFromJson();
                        break;
                    case "2":
                        System.out.println("Shutting down...");
                        return null;
                    default:
                        System.out.println("Answer isn't correct, try again...");
                        break;
                }
            }
        }
    }

    public void markOverdueTasks() {
        tasks = tasks.stream()
                .peek(task -> {
                    if (task.getStatus() == Status.IN_PROGRESS
                            && task.getCompletionDate().before(Date.from(Instant.now())))
                        task.setTitle(task.getTitle() + "*");
                })
                .toList();
    }

    public void sortTasksByPriority() {
        var result = tasks.stream()
                .sorted(Comparator.comparing(Task::getPriority))
                .toList();
    }

    public void sortTasksByCreationDate() {
        var result = tasks.stream()
                .sorted(Comparator.comparing(Task::getCreatedDate))
                .toList();
    }

    public void sortTasksByDescription() {
        var result = tasks.stream()
                .sorted(Comparator.comparing(Task::getDescription))
                .toList();
    }

    public void displayMenu() {
        System.out.println("===== Task Manager Menu =====");
        System.out.println("1. Show all tasks");
        System.out.println("2. Add a new task");
        System.out.println("3. Change task status or description");
        System.out.println("4. Delete a task");
        System.out.println("5. Save and exit");
        System.out.println("=============================");
    }

    public void changeDescription(Task task) throws CustomException {
        System.out.print("Введите новое описание задачи: ");
        String str = new Scanner(System.in).nextLine();
        task.getStatus().changeDescription(task, str);
    }

    public void changeStatus(Task task) throws CustomException {
        if(task.getStatus() != Status.DONE){
            String line = new Scanner(System.in).nextLine();
            System.out.println("На какой статус вы хотите поменять задачу?" +
                    "(Type 'p' - for In Progress and 'd' - for done): ");
            switch(line){
                case "p","P":
                    task.setStatus(task.getStatus().changeToIN_PROGRESS(task));
                    break;
                case"d", "D":
                    task.setStatus(task.getStatus().changeToDONE(task));
            }
        } else{
            System.out.println("You can't change the task that is done.");
        }
    }

    public void createNewTask() throws ParseException {
        System.out.print("Enter the name of the title: ");
        String title = sc.nextLine();
        String description = sc.nextLine();
        String completionDate = sc.nextLine();
        String createdDate = sc.nextLine();
        Priority priority = choosePriority();
        addNewTask(title, description, completionDate, createdDate, priority);

    }

    private Priority choosePriority(){
        System.out.println("What priority of a task do you want to choose?" +
                " ('L' - for low, 'M' - for medium, 'H' - for high): ");
        String str = sc.nextLine().toLowerCase();
        Priority priority = null;
        switch (str){
            case "l":
                priority = Priority.LOW;
                return priority;
            case "m":
                priority =Priority.MEDIUM;
                return priority;
            case "h":
                priority = Priority.HIGH;
                return priority;
        }
        System.out.println("You entered wrong letter");
        choosePriority();
        return priority;
    }


}
