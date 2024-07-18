import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

        System.out.println("Проверка на пустой объект");
        printAllTasks(inMemoryTaskManager);

        addTask();
        addEpic();
        addSubtask();
    }

    public static void addTask() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        final int task1Id = inMemoryTaskManager.addNewTask(task1);

        // Проверка массивов
        assert (inMemoryTaskManager.getEpics().isEmpty());
        assert (inMemoryTaskManager.getSubtasks().isEmpty());
        assert (inMemoryTaskManager.getTasks().size() == 1);

        final Task task = inMemoryTaskManager.getTask(task1Id);
        final String newTaskName = "%%%%$$#%#";

        task.setName(newTaskName);

        inMemoryTaskManager.updateTask(task);

        assert (inMemoryTaskManager.getTask(task1Id).getName() == newTaskName);

        assert (task == task1);

//        taskManager.deleteAllTasks();
        inMemoryTaskManager.deleteTask(task1Id);

        // Проверка массивов
        assert (inMemoryTaskManager.getEpics().isEmpty());
        assert (inMemoryTaskManager.getSubtasks().isEmpty());
        assert (inMemoryTaskManager.getTasks().isEmpty());
    }

    public static void addEpic() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

        final Epic epic = new Epic("Эпик 1", "Описание 1");
        final int epicId = inMemoryTaskManager.addNewEpic(epic);

        // Проверка массивов
        assert (inMemoryTaskManager.getEpics().size() == 1);
        assert (inMemoryTaskManager.getSubtasks().isEmpty());
        assert (inMemoryTaskManager.getTasks().isEmpty());

        final Epic epic1 = inMemoryTaskManager.getEpic(epicId);
        final String newName = "safafasa";

        assert (epic1.getStatus() == Status.NEW);
        assert (epic1 == epic);

        epic1.setName(newName);
        inMemoryTaskManager.updateEpic(epic1);

        final Epic epic2 = inMemoryTaskManager.getEpic(epicId);
        assert (epic2.getName() == newName);

//        taskManager.deleteAllEpics();
        inMemoryTaskManager.deleteEpic(epicId);

        // Проверка массивов
        assert (inMemoryTaskManager.getEpics().isEmpty());
        assert (inMemoryTaskManager.getSubtasks().isEmpty());
        assert (inMemoryTaskManager.getTasks().isEmpty());

    }

    public static void addSubtask() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

        final Epic epic1 = new Epic("Эпик 1", "Описание 1");
        final Epic epic2 = new Epic("Эпик 2", "Описание 2");
        final int epic1Id = inMemoryTaskManager.addNewEpic(epic1);
        final int epic2Id = inMemoryTaskManager.addNewEpic(epic2);

        assert (inMemoryTaskManager.getEpics().size() == 2);
        assert (inMemoryTaskManager.getSubtasks().isEmpty());
        assert (inMemoryTaskManager.getTasks().isEmpty());


        final Subtask subtask1 = new Subtask("Саб1", "1", Status.NEW, epic1Id);
        final Subtask subtask2 = new Subtask("Саб2", "2", Status.NEW, epic2Id);
        inMemoryTaskManager.addNewSubtask(subtask1);
        inMemoryTaskManager.addNewSubtask(subtask2);

        assert (inMemoryTaskManager.getEpics().size() == 2);
        assert (inMemoryTaskManager.getSubtasks().size() == 2);
        assert (inMemoryTaskManager.getTasks().isEmpty());

        assert (epic1.getStatus() == Status.NEW);
        assert (epic2.getStatus() == Status.NEW);

        subtask1.setStatus(Status.IN_PROGRESS);
        inMemoryTaskManager.updateSubtask(subtask1);

        assert (epic1.getStatus() == Status.IN_PROGRESS);
        assert (epic2.getStatus() == Status.NEW);

        subtask1.setStatus(Status.DONE);
        inMemoryTaskManager.updateSubtask(subtask1);

        assert (epic1.getStatus() == Status.DONE);
        assert (epic2.getStatus() == Status.NEW);

        final Subtask subtask3 = new Subtask("Саб3", "3", Status.NEW, epic1Id);
        inMemoryTaskManager.addNewSubtask(subtask3);

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

        inMemoryTaskManager.deleteAllEpics();

        assert (inMemoryTaskManager.getEpics().isEmpty());
        assert (inMemoryTaskManager.getSubtasks().isEmpty());
        assert (inMemoryTaskManager.getTasks().isEmpty());

    }

    public static void printAllTasks(InMemoryTaskManager inMemoryTaskManager) {
        System.out.println(inMemoryTaskManager.getEpics());
        System.out.println(inMemoryTaskManager.getSubtasks());
        System.out.println(inMemoryTaskManager.getTasks());
    }
}
