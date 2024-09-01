package tasks;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);

        this.epicId = epicId;
        this.type = TaskType.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return String.join(",", Integer.toString(id), "SUBTASK", name, status.toString(), description, Integer.toString(epicId));
    }
}
