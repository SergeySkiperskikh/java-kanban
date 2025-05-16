package com.yandex.app.service;

import com.yandex.app.model.*;

import java.util.Scanner;

public class MainMenu {
    private static MainMenu instance;
    private static TaskManager taskManager;
    private static Scanner scanner;

    private MainMenu() {
        taskManager = TaskManager.getInstance();
        scanner = new Scanner(System.in);
    }

    public static synchronized MainMenu getInstance() {
        if (instance == null) {
            instance = new MainMenu();
        }
        return instance;
    }

    public void choseTask() {
        while (true) {
            printMenu();
            String command = scanner.nextLine();
            switch (command) {
                case "1":
                    printEpicMenu();
                    choseMenu(TaskType.EPIC);
                    break;
                case "2":
                    printTaskMenu();
                    choseMenu(TaskType.SUBTASK);
                    break;
                case "3":
                    printTaskMenu();
                    choseMenu(TaskType.TASK);
                    break;
                case "4":
                    System.exit(0);
                    break;
                default:
                    System.out.println("Такой команды нет");
                    break;
            }
        }
    }

    protected static void choseMenu(TaskType taskType) {
        String taskDescription;
        String taskName;
        Task task = null;

        String command = scanner.nextLine();
        int id;
        switch (command) {
            case "1":
                System.out.println(taskManager.getTaskList(taskType));
                break;
            case "2":
                taskManager.removeTasksMap(taskType);
                break;
            case "3":
                System.out.println("Введите идентификатор задачи");
                id = Integer.parseInt(scanner.nextLine());
                System.out.println(taskManager.getTask(taskType, id));
                break;
            case "4":
                System.out.println("Напишите название задачи");
                taskName = scanner.nextLine();
                System.out.println("Опишите задачу");
                taskDescription = scanner.nextLine();

                if (taskType.equals(TaskType.EPIC)) {
                    task = new Epic(taskName, taskDescription);
                }
                if (taskType.equals(TaskType.TASK)) {
                    task = new Task(taskName, taskDescription, Status.NEW);
                }
                if (taskType.equals(TaskType.SUBTASK)) {
                    System.out.println("укажите ID epic");
                    id = Integer.parseInt(scanner.nextLine());
                    if (taskManager.getTask(TaskType.EPIC, id) == null) {
                        System.out.println("Такого епика нет");
                        break;
                    }
                    task = new Subtask(taskName, taskDescription, Status.NEW, id);
                }
                if (task != null) {
                    taskManager.createTask(task, taskType);
                }
                break;
            case "5":
                int comm = 0;
                System.out.println("Введите id задачи, которую нужно обновить");
                id = Integer.parseInt(scanner.nextLine());
                if (taskManager.getTask(taskType, id) == null) {
                    System.out.println("Такой задачи нет");
                    break;
                }
                System.out.println("Напишите название задачи");
                taskName = scanner.nextLine();
                System.out.println("Опишите задачу");
                taskDescription = scanner.nextLine();

                if (taskType.equals(TaskType.EPIC)) {
                    task = new Epic(taskName, taskDescription);
                }

                if (taskType.equals(TaskType.TASK)) {
                    System.out.println("Задача выполнена? 1 - да, 2 - нет");
                    comm = Integer.parseInt(scanner.nextLine());
                    if (comm == 1) {
                        task = new Task(taskName, taskDescription, Status.DONE);
                    }
                    if (comm == 2) {
                        task = new Task(taskName, taskDescription, Status.NEW);
                    }

                }

                if (taskType.equals(TaskType.SUBTASK)) {
                    System.out.println("Задача выполнена? 1 - да, 2 - нет");
                    comm = Integer.parseInt(scanner.nextLine());
                    if (comm == 1) {
                        task = new Subtask(taskName, taskDescription, Status.DONE, id);
                    }
                    if (comm == 2) {
                        task = new Subtask(taskName, taskDescription, Status.NEW, id);
                    }
                }

                if (task != null) {
                    taskManager.updateTask(task, taskType, id);
                }

                break;
            case "6": // rework for epic
                System.out.println("Введите id задачи");
                id = Integer.parseInt(scanner.nextLine());
                taskManager.removeTask(taskType, id);
                break;
            case "7":
                System.out.println("Введите id eпика");
                id = Integer.parseInt(scanner.nextLine());
                if (taskManager.getTask(TaskType.EPIC, id) == null) {
                    System.out.println("Такой задачи нет");
                    break;
                }
                System.out.println(taskManager.getSubtaskList(id));
                break;
            default:
                System.out.println("Такой команды нет");
                break;
        }
    }

    private static void printMenu() {
        System.out.println("Выберите тип задачи");
        System.out.println("1 = Epic");
        System.out.println("2 = Subtask");
        System.out.println("3 = Task");
        System.out.println("4 = выйти");
    }

    private static void printTaskMenu() {
        System.out.println("1 = Получить список всех задач");
        System.out.println("2 = Удалить все задачи");
        System.out.println("3 = Получить задачу по идентефикатору");
        System.out.println("4 = Создвть задачу");
        System.out.println("5 = Обновить задачу");
        System.out.println("6 = Удалить задачу по идентификатору");
    }

    private static void printEpicMenu() {
        printTaskMenu();
        System.out.println("7 = Получить список всех подзадач");
    }
}
