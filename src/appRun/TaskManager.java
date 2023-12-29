package appRun;

import state.Status;
import util.FileUtil;

import java.util.*;

public class TaskManager {
    private List<Task> tasks;

    public TaskManager() {
        this.tasks = FileUtil.readFile();
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
                try(){
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
        //todo Сохранить задачи в JSON файл
    }

    public void loadFromJson() {
        //todo Загрузить задачи из JSON файла
    }

    public void markOverdueTasks() {
        //todo Пометить просроченные задачи
    }

    public void sortTasksByPriority() {
        //todo Отсортировать задачи по приоритету
    }

    public void sortTasksByCreationDate() {
        //todo Отсортировать задачи по дате создания
    }

    public void sortTasksByDescription() {
        //todo Отсортировать задачи по описанию
    }

    private void changeStatus(){

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
