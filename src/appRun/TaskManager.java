package appRun;

import java.util.*;

public class TaskManager {
    private List<Task> tasks;

    public TaskManager() {
        this.tasks = new ArrayList<>();
    }

    public void showAllTasks() {
        //todo Вывести все задачи
    }

    public void addNewTask() {
        //todo Добавить новую задачу
    }

    public void changeTask() {
        //todo Изменить статус или описание задачи
    }

    public void deleteTask() {
        //todo Удалить задачу
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
