import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        System.out.println("Проверка на пустой объект");
        printAllTasks(taskManager);

        addTask();
        addEpic();
        addSubtask();
    }

    public static void addTask() {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        final int task1Id = taskManager.addNewTask(task1);

        // Проверка массивов
        assert (taskManager.getEpics().isEmpty());
        assert (taskManager.getSubtasks().isEmpty());
        assert (taskManager.getTasks().size() == 1);

        final Task task = taskManager.getTask(task1Id);
        final String newTaskName = "%%%%$$#%#";

        task.setName(newTaskName);

        taskManager.updateTask(task);

        assert (taskManager.getTask(task1Id).getName() == newTaskName);

        assert (task == task1);

//        taskManager.deleteAllTasks();
        taskManager.deleteTask(task1Id);

        // Проверка массивов
        assert (taskManager.getEpics().isEmpty());
        assert (taskManager.getSubtasks().isEmpty());
        assert (taskManager.getTasks().isEmpty());
    }

    public static void addEpic() {
        TaskManager taskManager = new TaskManager();

        final Epic epic = new Epic("Эпик 1", "Описание 1");
        final int epicId = taskManager.addNewEpic(epic);

        // Проверка массивов
        assert (taskManager.getEpics().size() == 1);
        assert (taskManager.getSubtasks().isEmpty());
        assert (taskManager.getTasks().isEmpty());

        final Epic epic1 = taskManager.getEpic(epicId);
        final String newName = "safafasa";

        assert (epic1.getStatus() == Status.NEW);
        assert (epic1 == epic);

        epic1.setName(newName);
        taskManager.updateEpic(epic1);

        final Epic epic2 = taskManager.getEpic(epicId);
        assert (epic2.getName() == newName);

//        taskManager.deleteAllEpics();
        taskManager.deleteEpic(epicId);

        // Проверка массивов
        assert (taskManager.getEpics().isEmpty());
        assert (taskManager.getSubtasks().isEmpty());
        assert (taskManager.getTasks().isEmpty());

    }

    public static void addSubtask() {
        TaskManager taskManager = new TaskManager();

        final Epic epic1 = new Epic("Эпик 1", "Описание 1");
        final Epic epic2 = new Epic("Эпик 2", "Описание 2");
        final int epic1Id = taskManager.addNewEpic(epic1);
        final int epic2Id = taskManager.addNewEpic(epic2);

        assert (taskManager.getEpics().size() == 2);
        assert (taskManager.getSubtasks().isEmpty());
        assert (taskManager.getTasks().isEmpty());


        final Subtask subtask1 = new Subtask("Саб1", "1", Status.NEW, epic1Id);
        final Subtask subtask2 = new Subtask("Саб2", "2", Status.NEW, epic2Id);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        assert (taskManager.getEpics().size() == 2);
        assert (taskManager.getSubtasks().size() == 2);
        assert (taskManager.getTasks().isEmpty());

        assert (epic1.getStatus() == Status.NEW);
        assert (epic2.getStatus() == Status.NEW);

        subtask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);

        assert (epic1.getStatus() == Status.IN_PROGRESS);
        assert (epic2.getStatus() == Status.NEW);

        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);

        assert (epic1.getStatus() == Status.DONE);
        assert (epic2.getStatus() == Status.NEW);

        final Subtask subtask3 = new Subtask("Саб3", "3", Status.NEW, epic1Id);
        taskManager.addNewSubtask(subtask3);

        assert (epic1.getStatus() == Status.IN_PROGRESS);
        assert (epic2.getStatus() == Status.NEW);

//        taskManager.deleteAllSubtasks();
//
//        assert (taskManager.getEpics().size() == 2);
//        assert (taskManager.getSubtasks().isEmpty());
//        assert (taskManager.getTasks().isEmpty());
//
//        assert (epic1.getStatus() == Status.NEW);
//        assert (epic2.getStatus() == Status.NEW);

        taskManager.deleteAllEpics();

        assert (taskManager.getEpics().isEmpty());
        assert (taskManager.getSubtasks().isEmpty());
        assert (taskManager.getTasks().isEmpty());

    }

    public static void printAllTasks(TaskManager taskManager) {
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
        System.out.println(taskManager.getTasks());
    }
}
