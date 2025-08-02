package com.yandex.app.service;

import com.yandex.app.model.*;

import java.util.*;
import java.util.stream.Collectors;


public class InMemoryTaskManager implements TaskManager {
    //Полностью переделал класс, оставил одну мапу, где-то подслушал что instandeof лучше не делать
    //Поэтому добавил таскам поле TaskType
    protected final Map<Integer, Task> tasksById = new HashMap<>();
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    protected final HistoryManager history;

    protected static int identifier = 0;

    public InMemoryTaskManager() {
        history = Managers.getDefaultHistory();
        resetID();
    }

    @Override
    public List<Task> getTaskList(TaskType type) {
        return tasksById.values()
                .stream()
                .filter(task -> task.getType() == type)
                .collect(Collectors.toList());
    }

    @Override
    public void removeTasksMap(TaskType type) {
        //Получаем лист с id задач которые нужно удалить
        List<Integer> idForRemove = tasksById.values()
                .stream()
                .filter(task -> task.getType() == type)
                .map(Task::getId)
                .toList();

        switch (type) {
            case EPIC -> {
                idForRemove.forEach(EpicId -> {
                    // Для каждого епика получаем id и удаляем сабтаски
                    Epic epic = (Epic) tasksById.get(EpicId);
                    epic.getSubtasksId().forEach(subId -> {
                        Subtask subtask = (Subtask) tasksById.get(subId);
                        prioritizedTasks.remove(subtask);
                        tasksById.remove(subId);
                        history.remove(subId);
                    });
                });
            }
            case SUBTASK -> {
                // Удаление сабтасок у эпиков, проверка статуса епика
                tasksById.values().stream()
                        .filter(task -> task.getType() == TaskType.EPIC)
                        .map(task -> (Epic) task)
                        .forEach(epic -> {
                            epic.getSubtasksId().clear();
                            checkEpicStatus(epic.getId());
                            recalculateEpicTime(epic.getId());
                            //Обнуляю епики;
                        });

            }
            case TASK -> {
            }
            default -> {
                return;
            }

        }
        //Общие действия для всех типов задач
        idForRemove.forEach(taskId -> {
            prioritizedTasks.remove(tasksById.get(taskId));
            history.remove(taskId);
            tasksById.remove(taskId);
        });
    }

    @Override
    public int createTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        if (!(TaskType.EPIC == task.getType())) {
            if (isOverlapping(task)) {
                System.out.println("Overlapping");
                return -1;
            }
        }

        task.setId(++identifier);
        //При создании Епика, я не добавляю его в Set, так как он без подзадач
        //и у него инициализированы поля по умолчанию.
        switch (task.getType()) {
            case EPIC -> tasksById.put(identifier, task);
            case TASK -> {
                tasksById.put(identifier, task);
                prioritizedTasks.add(task);
            }
            case SUBTASK -> {
                Subtask subtask = (Subtask) task;
                Epic epic = (Epic) tasksById.get(subtask.getEpicId());
                if (epic == null) {
                    throw new IllegalArgumentException("Epic not found");
                }
                tasksById.put(identifier, subtask);
                //добавляем сабтаску в епик
                epic.addSubtask(subtask);
                //Так как добавилась новая подзадача у епика, я обновляю его в множестве
                checkEpicStatus(epic.getId());
                recalculateEpicTime(epic.getId());
            }
            default -> {
                identifier--;
                return -1;
            }
        }
        return identifier;
    }

    @Override
    public Task getTask(int id) {
        Task task = tasksById.get(id);
        if (task == null) {
            throw new IllegalArgumentException("Task not found");
        }
        history.add(task);
        return task;
    }

    @Override
    public void removeTask(int id) {
        Task task = tasksById.get(id);
        if (task == null) {
            return;
        }

        switch (task.getType()) {
            case EPIC -> {
                Epic epic = (Epic) task;
                epic.getSubtasksId().forEach(subId -> {
                    prioritizedTasks.remove(tasksById.get(subId));
                    tasksById.remove(subId);
                    history.remove(subId);
                });
            }
            case SUBTASK -> {
                Subtask subtask = (Subtask) task;
                Epic epic = (Epic) tasksById.get(subtask.getEpicId());
                if (epic != null) {
                    //Не забываю пересчитать время епика
                    epic.removeSubtask(id);
                    checkEpicStatus(epic.getId());
                    recalculateEpicTime(epic.getId());
                }

            }
            case TASK -> {}
            default -> {
                return;
            }
        }
        //Общая логика для всех типов задач
        tasksById.remove(id);
        prioritizedTasks.remove(task);
        history.remove(id);
    }

    @Override
    public void updateTask(Task task, int id) {
        Task oldTask = tasksById.get(id);
        if (oldTask == null) {
            throw new IllegalArgumentException("Incorrect ID");
        }

        task.setId(id);

        switch (task.getType()) {
            case EPIC -> {
                Epic oldEpic = (Epic) oldTask;
                Epic newEpic = (Epic) task;
                //Задаем время выполнения Епику
                newEpic.setDuration(oldEpic.getDuration());
                newEpic.setStartTime(oldEpic.getStartTime());
                newEpic.setEndTime(oldEpic.getEndTime());
                newEpic.setSubtasksId(oldEpic.getSubtasksId());

                tasksById.put(id, newEpic);
                checkEpicStatus(id);
                //обновляем Епик в множестве
                prioritizedTasks.remove(oldEpic);
                prioritizedTasks.add(newEpic);
            }
            case TASK -> {
                if (isOverlapping(task)) {
                    System.out.println("Overlapping");
                    return;
                }

                tasksById.put(id, task);
                prioritizedTasks.remove(oldTask);
                prioritizedTasks.add(task);
            }
            case SUBTASK -> {
                if (isOverlapping(task)) {
                    System.out.println("Overlapping");
                    return;
                }

                Subtask oldSubtask = (Subtask) oldTask;
                Subtask newSubtask = (Subtask) task;

                newSubtask.setEpicId(oldSubtask.getEpicId());
                tasksById.put(id, newSubtask);
                checkEpicStatus(newSubtask.getEpicId());
                //пересчитываем епик
                recalculateEpicTime(newSubtask.getEpicId());
            }
            default -> {
            }
        }
    }

    @Override
    public boolean isOverlapping(Task newTask) {
        //Я исключаю Епики, потому что главное чтобы не было пересечений с сабтасками
        // Если у епика есть окно между двумя сабтасками, то должна быть возможность вставлять таски в это окно
        // и при Апдейте сабтаски не нужно проверять что ее интервалы были интервалами епика
        return tasksById.values()
                .stream()
                .filter(task -> task.getId() != (newTask.getId()))
                .filter(task -> task.getType() != TaskType.EPIC)
                .anyMatch(task -> !task.getEndTime().isBefore(newTask.getStartTime())
                        && !task.getStartTime().isAfter(newTask.getEndTime())
                );
    }

    @Override
    public void checkEpicStatus(int epicId) {
        Epic epic = (Epic) tasksById.get(epicId);
        if (epic == null) {
            throw new IllegalArgumentException("Epic not found");
        }

        if (epic.getSubtasksId().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        Status epicStatus = Status.IN_PROGRESS;
        boolean epicStatusIsDone = false;
        boolean epicStatusIsNew = false;

        for (Subtask sub : getSubtaskList(epicId)) {
            if (sub.getStatus().equals(Status.DONE)) {
                epicStatusIsDone = true;
            }
            if (sub.getStatus().equals(Status.NEW)) {
                epicStatusIsNew = true;
            }
        }
        if (epicStatusIsDone && !epicStatusIsNew) {
            epicStatus = Status.DONE;
        }

        if (!epicStatusIsDone && epicStatusIsNew) {
            epicStatus = Status.NEW;
        }

        epic.setStatus(epicStatus);
    }

    @Override
    public List<Subtask> getSubtaskList(int epicId) {
        Epic epic = (Epic) tasksById.get(epicId);
        if (epic == null) {
            throw new IllegalArgumentException("Epic not found");
        }

        return epic.getSubtasksId()
                .stream()
                .map(id -> (Subtask) tasksById.get(id))
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    public void recalculateEpicTime(Integer epicId) {
        Epic epic = (Epic) tasksById.get(epicId);
        if (epic == null) {
            throw new IllegalArgumentException("Epic not found");
        }
        prioritizedTasks.remove(epic);
        epic.recalculateTime(getSubtaskList(epicId));
        //Если значения по умолчанию не добавляю в множество
        if (epic.getStartTime().equals(Epic.DEFAULT_START_TIME) | epic.getEndTime().equals(Epic.DEFAULT_END_TIME)) {
            return;
        }
        prioritizedTasks.add(epic);
    }

    public static void resetID() {
        identifier = 0;
    }
}
