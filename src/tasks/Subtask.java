package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, Status status, LocalDateTime startTime, Duration duration, int epicId) {
        super(name, description, status, startTime, duration);

        this.epicId = epicId;
        this.type = TaskType.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return String.join(",", Integer.toString(id), "SUBTASK", name, status.toString(), description, startTime == null ? "null" : startTime.toString(), Long.toString(duration.toMinutes()), Integer.toString(epicId));
    }
}
