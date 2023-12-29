package appRun;

import state.Status;
import util.FileUtil;

import java.time.Instant;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class TaskManager {
    private List<Task> tasks;

    public TaskManager() {
        this.tasks = loadFromJson();
    }

    public void showAllTasks() {
        for (Task task : tasks) {
            task.displayTask();
        }
    }

    public void addNewTask(Task task) {
        tasks.add(task);
    }

    public void changeTask(String nameOfTask, String change){
        for(Task task: tasks){
            if(task.getTitle().equalsIgnoreCase(nameOfTask)){
                try {
                    switch (change){
                        case "d", "D":
                            System.out.print("Введите новое описание задачи: ");
                            String str = new Scanner(System.in).nextLine();
                            task.setDescription(str);
                            break;
                        case "s", "S":
                            if(task.getStatus() != Status.DONE){
                                String line = new Scanner(System.in).nextLine();
                                System.out.println("На какой статус вы хотите поменять задачу?" +
                                        "(Type 'n' for new, 'p' - for In Progress): ");
//                                task.setStatus();
                            }
                    }
                }
            }
        }
        //todo Изменить статус или описание задачи
    }

    public void deleteTask(String name) {
        tasks.removeIf(e -> e.getTitle().equalsIgnoreCase(name));
    }

    public void saveToJson() {
        FileUtil.writeFile(tasks);
    }

    public List<Task> loadFromJson() {
        return FileUtil.readFile();
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
}
