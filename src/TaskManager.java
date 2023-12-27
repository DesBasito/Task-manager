import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class TaskManager {
    private List<Task> tasks;

    public TaskManager() {
        this.tasks = new ArrayList<>();
    }


    // конструктор, геттеры и сеттеры

    public void displayMenu() {
        // Вывести меню взаимодействия с пользователем
    }

    public void showAllTasks() {
        // Вывести все задачи
    }

    public void addNewTask() {
        // Добавить новую задачу
    }

    public void changeTask() {
        // Изменить статус или описание задачи
    }

    public void deleteTask() {
        // Удалить задачу
    }

    public void saveToJson() {
        // Сохранить задачи в JSON файл
    }

    public void loadFromJson() {
        // Загрузить задачи из JSON файла
    }

    public void markOverdueTasks() {
        // Пометить просроченные задачи
    }

    public void sortTasksByPriority() {
        // Отсортировать задачи по приоритету
    }

    public void sortTasksByCreationDate() {
        // Отсортировать задачи по дате создания
    }

    public void sortTasksByDescription() {
        // Отсортировать задачи по описанию
    }
}
