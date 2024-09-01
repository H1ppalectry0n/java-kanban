package tasks;

import java.io.IOException;

public class TaskUtil {
    public static Task fromString(final String value) throws IOException {
        String[] split = value.split(",");
        switch (TaskType.valueOf(split[1])) {
            case EPIC -> {
                Epic epic = new Epic(split[2], split[4]);
                epic.setStatus(Status.valueOf(split[3]));
                epic.setId(Integer.parseInt(split[0]));
                return epic;
            }
            case SUBTASK -> {
                Subtask subtask = new Subtask(split[2], split[4], Status.valueOf(split[3]), Integer.parseInt(split[5]));
                subtask.setId(Integer.parseInt(split[0]));
                return subtask;
            }
            case TASK -> {
                Task task = new Task(split[2], split[4], Status.valueOf(split[3]));
                task.setId(Integer.parseInt(split[0]));
                return task;
            }
            default -> {
                throw new IOException("Неверный формат записи.");
            }
        }
    }
}
